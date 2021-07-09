package edu.neu.madcourse.numad21su_lld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {
    public String my_username;
    private static final String USERNAME = "USERNAME";
    private static String CLIENT_REGISTRATION_TOKEN;
    //TODO change this from hardcode to a getter
    private static String SERVER_KEY = "key=AAAA5-WnK0Y:APA91bGSNkJBv6lna--2EgJvdjxNtxt1eUc8yTKroB8nKJ3Tq_VSrWjSDFJ4ydON6OxM5sRr8QRNcnnZAXiTTzTL6dib9_XJIJEGe75h0oHKjrbvJMENomYQuZZUq0OiDrksuKPffK74\t\n";
    private User userObject;
    Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME)) {
            Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
            startActivity(intent);
        }

        login_button = findViewById(R.id.login_button);

        // Generate the token for the first time, then no need to do later
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                } else {
                    if (CLIENT_REGISTRATION_TOKEN == null) {
                        CLIENT_REGISTRATION_TOKEN = task.getResult();
                    }
                    Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
                    Toast.makeText(MainActivity.this, "CLIENT_REGISTRATION_TOKEN Existed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        my_username = sh.getString("userName", null);

        if (my_username != null) {
            Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
            startActivity(intent);
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                my_username = ((EditText) findViewById(R.id.enter_username)).getText().toString();
                userObject = new User(my_username,CLIENT_REGISTRATION_TOKEN);

                // TODO store this username and get this instanceID and send to database
                Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);

                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myUserRef = database.getReference("Users");
                //myUserRef.child(my_username).child("CLIENT_REGISTRATION_TOKEN");
                myUserRef.child(userObject.username).setValue(userObject);

                // we need to make sure this ^ writes to the database
               /*
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Task t_user = myUserRef.child(userObject.username).setValue(userObject);
                        if(!t_user.isSuccessful()){
                            Toast.makeText(getApplicationContext()
                                    , "Failed to write user into firebase. Re-enter." , Toast.LENGTH_SHORT).show();
                            //return;
                        }
                    }
                }).start();
                */

                // Store the userName in shared preferences to skip login if already done
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("userName", my_username);
                myEdit.putString("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
                myEdit.commit();
                startActivity(intent);
            }
        });

        // maybe make this a login screen and if logged in before then go to received activity
        // here need to send token and username to database
        // database should contain username -> tokens
        // and then username -> number sent
        // username -> number received
        // username -> sticker, username pairs, timestamp?
        //          -> so that users can send same sticker multiple times?
    }
}
