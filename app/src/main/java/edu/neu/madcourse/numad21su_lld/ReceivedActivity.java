package edu.neu.madcourse.numad21su_lld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class ReceivedActivity extends AppCompatActivity implements SendStickerDialogFragment.SendStickerDialogListener {
    private final ArrayList<StickerCard> stickerHistory = new ArrayList<>();
    private RecyclerView stickerRecyclerView;
    private ReceivedStickerAdapter receivedStickerAdapter;
    private RecyclerView.LayoutManager receivedStickerLayoutManager;
    private FloatingActionButton sendStickerButton;
    private Button accountInfoButton;
    private String my_username;
    private String my_token;
    private String sticker_to_send;
    int received_history_size;
    GridView sticker_grid;
    DatabaseReference myUserHistoryRef;
    private HashMap<String, Boolean> validatedUsers = new HashMap<>();
    private final Handler handler = new Handler();
    private static final String KEY_OF_STICKER = "KEY_OF_STICKER";
    private static final String NUMBER_OF_STICKERS = "NUMBER_OF_STICKERS";
    private static final String TAG = ReceivedActivity.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAA5-WnK0Y:APA91bGSNkJBv6lna--2EgJvdjxNtxt1eUc8yTKroB8nKJ3Tq_VSrWjSDFJ4ydON6OxM5sRr8QRNcnnZAXiTTzTL6dib9_XJIJEGe75h0oHKjrbvJMENomYQuZZUq0OiDrksuKPffK74";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("userName", "Not found");
        my_token = sharedPreferences.getString("CLIENT_REGISTRATION_TOKEN", "Not found");
        if(my_username == "Not found" || my_token == "Not found"){
            Intent intent = new Intent(ReceivedActivity.this, MainActivity.class);
            startActivity(intent);
        }
        createNotificationChannel();
        setContentView(R.layout.activity_received_history);
        received_history_size = 1;
        init(savedInstanceState);
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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(ReceivedActivity.this, "Send Sticker Back!", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getLayoutPosition();
                //TODO maybe allow this to open dialog for that particular username?
                // - autopopulate the username field in the dialog frag?
                //stickerHistory.remove(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(stickerRecyclerView);

        // TODO Configure receiving from database the list of stickers
        // updateHistory();
        //TODO this needs to be fixed because i broke our app

        // Need to add these to when receiving from database
        // --- stickerHistory.add(0, new StickerCard(username, sticker));
        // --- receivedStickerAdapter.notifyItemInserted(0);
        // Also need to keep track of number received and sent
    }

    public void startSendDialog() {
        DialogFragment sendDialog = new SendStickerDialogFragment();
        sendDialog.show(getSupportFragmentManager(), "sendDialogFragment");
    }

    public void onDialogPositiveClick(DialogFragment sendDialog) {
        Dialog addSendDialog = sendDialog.getDialog();
        String other_username = ((EditText) addSendDialog.findViewById(R.id.username)).getText().toString();

        /*// Need to figure out how to select icons and associate them with a string/enum
        // TODO currently just have stickers as text/string but need to change to icon
        // https://stackoverflow.com/questions/3609231/how-is-it-possible-to-create-a-spinner-with-images-instead-of-text
        // https://stackoverflow.com/questions/13151847/how-to-add-image-to-spinner-in-android
        Spinner sticker_spinner = addSendDialog.findViewById(R.id.sticker_spinner);

        // TODO maybe this is a solution
        // https://www.it1228.com/658881.html
        SpinnerAdapter adapter = new ImageArrayAdapter(this, new Integer[] {
                R.drawable.coffee,
                R.drawable.donut,
                R.drawable.egg,
                R.drawable.french_fries,
                R.drawable.hamburger,
                R.drawable.ice_cream,
                R.drawable.milk,
                R.drawable.toast,
                R.drawable.watermelon});
        sticker_spinner.setAdapter(adapter);
        */

        // TODO: not sure if this is where it belongs... might want to move to a SendActivity
        // https://www.geeksforgeeks.org/gridview-in-android-with-example/
        sticker_grid = findViewById(R.id.sticker_grid);
        ArrayList<Sticker> stickerArrayList = new ArrayList<Sticker>();
        stickerArrayList.add(new Sticker("Coffee", R.drawable.coffee));
        stickerArrayList.add(new Sticker("Donut", R.drawable.donut));
        stickerArrayList.add(new Sticker("Egg", R.drawable.egg));
        stickerArrayList.add(new Sticker("French Fries", R.drawable.french_fries));
        stickerArrayList.add(new Sticker("Hamburger", R.drawable.hamburger));
        stickerArrayList.add(new Sticker("Ice Cream", R.drawable.ice_cream));
        stickerArrayList.add(new Sticker("Milk", R.drawable.milk));
        stickerArrayList.add(new Sticker("Toast", R.drawable.toast));
        stickerArrayList.add(new Sticker("Watermwlon", R.drawable.watermelon));

        StickerGridAdapter adapter = new StickerGridAdapter(this, stickerArrayList);
        sticker_grid.setAdapter(adapter);

        sticker_grid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] sticker_choices = getResources().getStringArray(R.array.sticker_array);
                sticker_to_send = sticker_choices[position];
                Log.e(TAG, sticker_to_send + " CHOSEN STICKER");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sticker_to_send = "coffee"; //TODO remove this test
            }
        });

        if (isValidUsername(other_username)) {
            sendDialog.dismiss();
            // TODO transform this to the real sticker IMAGE chosen
            sendSticker(other_username, sticker_to_send);
            sendStickerMessageToDB(other_username, sticker_to_send);
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, R.string.send_sticker_confirm, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else {
            //invalid username
            Toast.makeText(ReceivedActivity.this, R.string.send_sticker_error,
                    Toast.LENGTH_SHORT).show();
        }
    }


    // TODO validate username - this seems to be a little wonky on the first validation - after the second time it is able to send a notif
    private boolean isValidUsername(String other_username) {
        // do in another thread ?
        if (validatedUsers.containsKey(other_username) && validatedUsers.get(other_username)){
            Log.d(TAG, "Already Validated: " + other_username);
            return true;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference("Users/" + other_username);
        myUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    validatedUsers.put(other_username, true);
                    Toast.makeText(ReceivedActivity.this, "Validated: " + other_username, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Validated: " + other_username);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ReceivedActivity.this, "Failed to get data.", Toast.LENGTH_SHORT).show();
                Toast.makeText(ReceivedActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Cancelled, failed to get: " + other_username);
            }
        });

        if (validatedUsers.containsKey(other_username) && validatedUsers.get(other_username)){
            return true;
        }
        return false;
    }


    @Override
    public void onDialogNegativeClick(DialogFragment sendDialog) {
        sendDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int size = stickerHistory == null ? 0 : stickerHistory.size();
        outState.putInt(NUMBER_OF_STICKERS, size);
        for (int i = 0; i < size; i++) {
            outState.putString(KEY_OF_STICKER + i + "0", stickerHistory.get(i).getUsername());
            outState.putString(KEY_OF_STICKER + i + "1", stickerHistory.get(i).getSticker());
            outState.putString(KEY_OF_STICKER + i + "1", stickerHistory.get(i).getStickerPNGID());
        }
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState) {
        initialStickerData(savedInstanceState);
        createRecyclerView();
    }

    private void initialStickerData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_STICKERS)) {
            if (stickerHistory == null || stickerHistory.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_STICKERS);
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String username = savedInstanceState.getString(KEY_OF_STICKER + i + "0");
                    String sticker = savedInstanceState.getString(KEY_OF_STICKER + i + "1");
                    String sticker_png_id = savedInstanceState.getString(KEY_OF_STICKER + i + "2");
                    StickerCard StickerCard = new StickerCard(username, sticker, Integer.parseInt( sticker_png_id));
                    stickerHistory.add(StickerCard);
                }
            }
        }
    }

    private void createRecyclerView() {
        receivedStickerLayoutManager = new LinearLayoutManager(this);
        stickerRecyclerView = findViewById(R.id.recycler_view);
        stickerRecyclerView.setHasFixedSize(true);
        receivedStickerAdapter = new ReceivedStickerAdapter(stickerHistory);
        stickerRecyclerView.setAdapter(receivedStickerAdapter);
        stickerRecyclerView.setLayoutManager(receivedStickerLayoutManager);
    }


    public void viewAccountInformation(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View account_info_view = getLayoutInflater().inflate(R.layout.account_info, null);
        Button back_button = (Button) account_info_view.findViewById(R.id.back_button);
        TextView tv_username = account_info_view.findViewById(R.id.my_username);
        TextView tv_number_sent = account_info_view.findViewById(R.id.my_number_sent);
        TextView tv_token = account_info_view.findViewById(R.id.my_token);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference("Users/"+my_username);
        myUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && my_username != null) {
                    User user = snapshot.getValue(User.class);
                    Log.d(TAG, "Found: " + my_username);
                    int my_number_sent = user.stickers_sent;
                    tv_number_sent.setText(String.valueOf(my_number_sent));
                    Log.d(TAG, "Number Sent: " + my_username);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ReceivedActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

        tv_username.setText(my_username);
        tv_token.setText(my_token);
        dialogBuilder.setView(account_info_view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = my_username;
            String description = "Notifications for "+ my_username;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(my_username, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            subscribeToMyMessages();
        }
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


    public void sendSticker(String other_username, String sticker) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myUserRef = database.getReference("Users/"+other_username);
                // Prepare intent which is triggered if the
                // notification is selected
                Intent intent = new Intent(ReceivedActivity.this, ReceiveNotificationActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(ReceivedActivity.this, (int) System.currentTimeMillis(), intent, 0);
                PendingIntent callIntent = PendingIntent.getActivity(ReceivedActivity.this, (int) System.currentTimeMillis(),
                       new Intent(ReceivedActivity.this, ReceiveNotificationActivity.class), 0);
                // Build notification
                // Need to define a channel ID after Android Oreo
                String channelId = other_username;
                // TODO make this not just coffee
                int pngID = getStickerResourceID(sticker);
                NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(ReceivedActivity.this, channelId)
                        //"Notification icons must be entirely white."
                        .setSmallIcon(pngID) // get resources sticker
                        .setContentTitle("New Sticker From " + my_username)
                        .setContentText("Subject: " + sticker)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        // hide the notification after its selected
                        .setAutoCancel(true)
                        .addAction(pngID, "Call", callIntent)
                        .setContentIntent(pIntent);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ReceivedActivity.this);
                // // notificationId is a unique int for each notification that you must define
                notificationManager.notify(0, notifyBuild.build());
            }
        }).start();
    }


    private void sendStickerMessageToDB(String other_username, String sticker) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference otherUserRef = database.getReference("Users/"+other_username);
                // TODO How to update data in the database
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

/*
    public void sendStickerToken(String targetToken, String sticker) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "New message from "+my_username);
            jNotification.put("body", my_username +" sent you a "+sticker );
            jNotification.put("sound", "default");
            jNotification.put("badge", "1"); // sticker? convert to image
            *//*
            // We can add more details into the notification if we want.
            // We happen to be ignoring them for this demo.
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            *//*
            jdata.put("title", "data title from 'sendStickerToken'");
            jdata.put("content", "data content from 'sendStickerToken'");
            jPayload.put("to", targetToken);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String resp = Utils.stickerHttpConnection(SERVER_KEY, jPayload);
        handler.post(() -> {Toast.makeText(ReceivedActivity.this, "Status from Server: " + resp, Toast.LENGTH_SHORT).show();});
    }
        */

    private void updateHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                myUserHistoryRef = database.getReference("Users/"+my_username);
                myUserHistoryRef.addValueEventListener(new ValueEventListener() {
                    public User my_user;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && my_username != null) {
                            my_user = dataSnapshot.getValue(User.class);
                            // Check size of received history
                            ArrayList<StickerMessage> received_history = my_user.getReceived_history();
                            int new_received_history_size = received_history.size();
                            if (new_received_history_size > received_history_size) {
                                Log.e(TAG,String.valueOf(new_received_history_size));
                                for (int i = 1; i < new_received_history_size; i++) {
                                    StickerMessage stickerMessage = received_history.get(i);
                                    Log.e(TAG,stickerMessage.toString());
                                    String username = stickerMessage.getUsername();
                                    String sticker = stickerMessage.getSticker_id();
                                    int pngID = getStickerResourceID(sticker);
                                    stickerHistory.add(0, new StickerCard(username, sticker, pngID));
                                    receivedStickerAdapter.notifyItemInserted(0);
                                }
                            }
                        }
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


    public int getStickerResourceID(String sticker){
        Resources res = getResources();
        int pngID = res.getIdentifier(sticker+".png" , "drawable", getPackageName());
        // for now return coffee need to fix ^
        return R.drawable.coffee;
    }

}



