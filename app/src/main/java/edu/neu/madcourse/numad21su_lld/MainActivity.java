package edu.neu.madcourse.numad21su_lld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public String my_username;
    private static final String USERNAME = "USERNAME";
    private static String CLIENT_REGISTRATION_TOKEN;
    private static String SERVER_KEY = "key=AAAA5-WnK0Y:APA91bGSNkJBv6lna--2EgJvdjxNtxt1eUc8y" +
            "TKroB8nKJ3Tq_VSrWjSDFJ4ydON6OxM5sRr8QRNcnnZAXiTTzTL6dib9_XJIJEGe75h0oHKjrbvJMEN" +
            "omYQuZZUq0OiDrksuKPffK74\t\n";
    private static Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hide the action bar for aesthetics
        getSupportActionBar().hide();

        // if the user is returning to the app, open ReceivedActivity
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME)) {
            startActivity(new Intent(MainActivity.this, ReceivedActivity.class));
        }

        // else recognize the login button
        login_button = findViewById(R.id.login_button);

        // and generate the user token for the first time ... then no need to do later
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

            // if there is an error, display some error information to the user
            if (!task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Something is wrong!",
                        Toast.LENGTH_SHORT).show();

            } else {
                // otherwise if the token worked, store it for later
                if (CLIENT_REGISTRATION_TOKEN == null) {
                    CLIENT_REGISTRATION_TOKEN = task.getResult();
                }
                Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
            }
        });

        // store the preferences in the username
        my_username = getSharedPreferences("MySharedPref", MODE_PRIVATE).getString(
                "userName", null);

        // if the username is not null, go to the ReceivedActivity class
        if (my_username != null) {
            startActivity(new Intent(MainActivity.this, ReceivedActivity.class));
        }

        // Listen for a click on the login button
        login_button.setOnClickListener(view -> {

            // Save down the username from the user
            my_username = ((EditText) findViewById(R.id.enter_username)).getText().toString();

            // Write a message to the database
            login_user();

            // Store the username in shared preferences to skip login if already done
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",
                    MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("userName", my_username);
            myEdit.putString("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
            myEdit.commit();

            // start the new activity
            startActivity(new Intent(MainActivity.this, ReceivedActivity.class));
        });
    }


    private void login_user() {
        new Thread(() -> {
            // connect to the database and look at the users
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                    "Users/" + my_username);

            myUserRef.addValueEventListener(new ValueEventListener() {

                public User my_user;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if the user exists, get their data
                    if (dataSnapshot.exists()) {
                        my_user = dataSnapshot.getValue(User.class);
                    } else {
                        // else create a new user and store their token
                        myUserRef.setValue(new User(my_username,CLIENT_REGISTRATION_TOKEN));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // if getting post failed, log a message
                    Log.w(TAG, "my_user start login onCancelled",
                            databaseError.toException());
                }
            });

        }).start();
    }
}
