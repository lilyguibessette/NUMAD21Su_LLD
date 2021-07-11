package edu.neu.madcourse.numad21su_lld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import edu.neu.madcourse.numad21su_lld.fcm_server.Utils;


public class ReceivedActivity extends AppCompatActivity implements SendStickerDialogFragment.SendStickerDialogListener {
    // Recycler view related variables
    private final ArrayList<StickerMessage> stickerHistory = new ArrayList<>();
    private RecyclerView stickerRecyclerView;
    private ReceivedStickerAdapter receivedStickerAdapter;
    private RecyclerView.LayoutManager receivedStickerLayoutManager;
    int received_history_size;
    private static final String KEY_OF_STICKER = "KEY_OF_STICKER";
    private static final String NUMBER_OF_STICKERS = "NUMBER_OF_STICKERS";
    // Buttons
    private Button sendStickerButton;
    private Button accountInfoButton;
    // Information for this user
    private String my_username;
    private String my_token;
    private int my_number_sent;
    // Dynamic information for each sticker sent
    private Integer sticker_to_send = R.drawable.coffee; // default to avoid current error
    private Spinner sticker_spinner;
    private HashMap<String, Boolean> validatedUsers = new HashMap<>();
    // Updating data from database resources
    private FirebaseDatabase database;
    private DatabaseReference myUserRef;
    private DatabaseReference myUserHistoryRef;
    private DatabaseReference allUsersRef;
    private ChildEventListener validatedUsersListener;
    private ChildEventListener myUserHistoryListener;

    //
    private static final String TAG = ReceivedActivity.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAA5-WnK0Y:APA91bGSNkJBv6lna--2EgJvdjxNtxt1eUc8yTKroB8nKJ3Tq_VSrWjSDFJ4ydON6OxM5sRr8QRNcnnZAXiTTzTL6dib9_XJIJEGe75h0oHKjrbvJMENomYQuZZUq0OiDrksuKPffK74";

    // ImageArray stores the IDs of our drawables
    Integer[] imageArray = { R.drawable.coffee,
            R.drawable.donut,
            R.drawable.egg,
            R.drawable.french_fries,
            R.drawable.hamburger,
            R.drawable.ice_cream,
            R.drawable.milk,
            R.drawable.toast,
            R.drawable.watermelon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.muncha_crunch);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        // Getting current username that is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("userName", "Not found");
        my_token = sharedPreferences.getString("CLIENT_REGISTRATION_TOKEN", "Not found");

        // If we don't have the userName or token, restart the login activity
        if(my_username == "Not found" || my_token == "Not found"){
            Intent intent = new Intent(ReceivedActivity.this, MainActivity.class);
            startActivity(intent);
        }

        // Initialize resources, notification channel, sticker behavior and set content view
        createDatabaseResources();
        createNotificationChannel();
        setContentView(R.layout.activity_received_history);

        // Initialize our received history size to 0
        received_history_size = 0;

        // get saved state and initialize the recyclerview
        init(savedInstanceState);

        // Define button actions
        sendStickerButton = findViewById(R.id.sendStickerButton);
        sendStickerButton.setTooltipText("Send a Sticker");
        sendStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSendDialog();
            }
        });

        accountInfoButton = findViewById(R.id.account_info_button);
        accountInfoButton.setTooltipText("View Account Information");
        accountInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAccountInformation(v);
            }
        });
    }

    private void init(Bundle savedInstanceState) {
        initialStickerData(savedInstanceState);
        createRecyclerView();
    }

    // Starts send dialog
    public void startSendDialog() {
        DialogFragment sendDialog = new SendStickerDialogFragment();
        sendDialog.show(getSupportFragmentManager(), "sendDialogFragment");
    }

    public void onDialogPositiveClick(DialogFragment sendDialog) {
        Dialog addSendDialog = sendDialog.getDialog();
        String other_username = ((EditText) addSendDialog.findViewById(R.id.username)).getText().toString();

        // get selected sticker from spinner
        sticker_spinner = addSendDialog.findViewById(R.id.sticker_spinner);
        sticker_to_send = imageArray[sticker_spinner.getSelectedItemPosition()];

        // If username to send to is valid, run send operations
        if (isValidUsername(other_username)) {
            sendDialog.dismiss();

            // Use FireBaseMessaging to push a notification that sticker was sent
            sendStickertoUserTopic(other_username, sticker_to_send);

            // push sticker to database
            sendStickerMessageToDB(other_username, sticker_to_send);

            // Inform user that sticker was sent
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, R.string.send_sticker_confirm, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else {
            // inform of invalid username
            Toast.makeText(ReceivedActivity.this, R.string.send_sticker_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Determine if an input userName is valid
    private boolean isValidUsername(String other_username) {
        // Check database for username
        if (validatedUsers.containsKey(other_username) && validatedUsers.get(other_username)){
            Log.d(TAG, "Already Validated: " + other_username);
            return true;
        }
        // If username is not valid, check if it exists in the db and validate it
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference otherUserRef = database.getReference("Users/" + other_username);
        otherUserRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    validatedUsers.put(other_username, true);
                    Toast.makeText(ReceivedActivity.this, "Validated: " + other_username, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Validated: " + other_username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReceivedActivity.this, "Failed to get data.", Toast.LENGTH_SHORT).show();
                Toast.makeText(ReceivedActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Cancelled, failed to get: " + other_username);
            }
        });
        if (validatedUsers.containsKey(other_username) && validatedUsers.get(other_username)){
            return true;
        }

        // If user is not validated and doesn't exist in db, return false
        return false;
    }


    @Override
    public void onDialogNegativeClick(DialogFragment sendDialog) {
        sendDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Save the sticker history so we can retrieve it on orientation change
        int size = stickerHistory == null ? 0 : stickerHistory.size();
        outState.putInt(NUMBER_OF_STICKERS, size);
        for (int i = 0; i < size; i++) {
            outState.putString(KEY_OF_STICKER + i + "0", stickerHistory.get(i).getUsername());
            outState.putString(KEY_OF_STICKER + i + "1", String.valueOf(stickerHistory.get(i).getSticker_id()));
        }
        super.onSaveInstanceState(outState);
    }


    private void initialStickerData(Bundle savedInstanceState) {
        // recreate the sticker history on orientation change or open
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_STICKERS)) {
            if (stickerHistory == null || stickerHistory.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_STICKERS);
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String username = savedInstanceState.getString(KEY_OF_STICKER + i + "0");
                    String sticker = savedInstanceState.getString(KEY_OF_STICKER + i + "1");
                    StickerMessage StickerMessage = new StickerMessage(username, Integer.parseInt(sticker));// , Integer.parseInt( sticker_png_id));
                    stickerHistory.add(StickerMessage);
                }
            }
        }
    }

    private void createRecyclerView() {
        // Create the recyclerview and populate it with the sticker history
        receivedStickerLayoutManager = new LinearLayoutManager(this);
        stickerRecyclerView = findViewById(R.id.recycler_view);
        stickerRecyclerView.setHasFixedSize(true);
        receivedStickerAdapter = new ReceivedStickerAdapter(stickerHistory);
        stickerRecyclerView.setAdapter(receivedStickerAdapter);
        stickerRecyclerView.setLayoutManager(receivedStickerLayoutManager);
    }


    public void viewAccountInformation(View view) {
        // Show user information in a new dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View account_info_view = getLayoutInflater().inflate(R.layout.account_info, null);

        // populate dialog with user information
        Button back_button = account_info_view.findViewById(R.id.back_button);
        TextView tv_username = account_info_view.findViewById(R.id.user_stats);
        tv_username.setText("Way to go!\n" + my_username + " has sent " + my_number_sent + " stickers");

        // create and show the dialog
        dialogBuilder.setView(account_info_view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // set back button action
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel
                    ("Muncha_Crunch_Channel", my_username, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications for "+ my_username);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            subscribeToMyMessages();
        }
    }


    public void sendStickertoUserTopic(String other_user, int sticker_to_send) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jPayload = new JSONObject();
                JSONObject jNotification = new JSONObject();
                try {
                    jNotification.put("title", "New Sticker from "+ my_username);
                    jNotification.put("body", sticker_to_send);
                    jNotification.put("sound", "default");
                    jNotification.put("badge", "1");

                    // Populate the Payload object.
                    // Note that "to" is a topic, not a token representing an app instance
                    jPayload.put("to", "/topics/" + other_user);
                    jPayload.put("priority", "high");
                    jPayload.put("notification", jNotification);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String resp = Utils.fcmHttpConnection(SERVER_KEY, jPayload);
                Log.d(TAG, "STICKER SENT TO " + other_user);
                Log.d(TAG, resp);
                //Utils.postToastMessage("Status from Server: " + resp, getApplicationContext());
            }
        }).start();
    }

    public void subscribeToMyMessages() {
        FirebaseMessaging.getInstance().subscribeToTopic(my_username)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to "+my_username;
                        if (!task.isSuccessful()) {
                            msg = "Failed to subscribe to "+my_username;
                            Toast.makeText(ReceivedActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ReceivedActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendStickerMessageToDB(String other_username, int sticker) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference otherUserRef = database.getReference("Users/"+other_username);
                otherUserRef.addValueEventListener(new ValueEventListener() {
                    public User other_user;
                    public Boolean first_history_data_change = true;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        other_user = dataSnapshot.getValue(User.class);
                        if (other_user != null && first_history_data_change){
                            other_user.addMessage(new StickerMessage(my_username, sticker));
                            otherUserRef.setValue(other_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "Update received history: " + other_user.toString());
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "FAILED to update received history: " + other_user.toString());
                                        }
                                    });
                        }
                        first_history_data_change = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "other_user sendStickerMessageToDB onCancelled", databaseError.toException());
                    }

                });

                // This is working
                DatabaseReference myUserRef = database.getReference("Users/"+my_username);
                myUserRef.addValueEventListener(new ValueEventListener() {
                    public User my_user;
                    public Boolean first_sticker_data_change = true;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        my_user = dataSnapshot.getValue(User.class);
                        if (first_sticker_data_change) {
                            my_user.stickers_sent += 1;
                            Log.w(TAG, "Update sitckers sent: " + my_user.toString());
                            myUserRef.setValue(my_user);
                        }
                        first_sticker_data_change = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "my_user sendStickerMessageToDB onCancelled", databaseError.toException());
                    }
                });

            }
        }).start();
    }

    /**
     * LISTENERS FOR DATA CHANGES
     * - Listen for change for number of stickers sent
     * - Listen for change in all users to validate
     * - Listen for change in received history
     */
    private void createDatabaseResources() {
        database = FirebaseDatabase.getInstance();
        myUserRef = database.getReference("Users/"+my_username);
        myUserHistoryRef = database.getReference("Users/"+my_username+"/received_history");
        allUsersRef = database.getReference("Users");
        setReceivedHistoryListener();
        setValidatedUsersListener();
        setNumberOfStickersListener();
    }

    public void setNumberOfStickersListener(){
        myUserRef = database.getReference("Users/"+my_username);
        myUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && my_username != null) {
                    User user = snapshot.getValue(User.class);
                    Log.d(TAG, "Found: " + my_username);
                    if (user != null) {
                        my_number_sent = user.getStickers_sent();
                        Log.d(TAG, "Number Sent: " + my_username);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReceivedActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setReceivedHistoryListener(){
        myUserHistoryListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new data item has been added, add it to the list
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                StickerMessage message = dataSnapshot.getValue(StickerMessage.class);
                Log.d(TAG, "onChildAdded:" + message.username);
                stickerHistory.add(0, message);
                receivedStickerAdapter.notifyItemInserted(0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        myUserHistoryRef.addChildEventListener(myUserHistoryListener);
    }


    private void setValidatedUsersListener() {
        validatedUsersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new data item has been added, add it to the list
                User user = dataSnapshot.getValue(User.class);
                validatedUsers.put(user.getUsername(), true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                // Do nothing
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                validatedUsers.put(user.getUsername(), false);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        allUsersRef.addChildEventListener(validatedUsersListener);
    }

}



