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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {
    public String my_username;
    private static final String USERNAME = "USERNAME";
    private static String CLIENT_REGISTRATION_TOKEN;
    private static String SERVER_KEY = "key=AAAA5-WnK0Y:APA91bGSNkJBv6lna--2EgJvdjxNtxt1eUc8yTKroB8nKJ3Tq_VSrWjSDFJ4ydON6OxM5sRr8QRNcnnZAXiTTzTL6dib9_XJIJEGe75h0oHKjrbvJMENomYQuZZUq0OiDrksuKPffK74\t\n";
    Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME)) {
            Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
            startActivity(intent);
        }

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
        my_username = sh.getString("userName", null);;
        if(my_username != null){
            Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
            startActivity(intent);
        }



        login_button.setOnClickListener(new View.OnClickListener() {
        } else {
            setContentView(R.layout.activity_login);
            login_button = findViewById(R.id.login_button);
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    my_username = ((EditText) findViewById(R.id.enter_username)).getText().toString();

                    // TODO store this username and get this instanceID and send to database
                    Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myUserRef = database.getReference("Users");
                    myUserRef.child(my_username).child("CLIENT_REGISTRATION_TOKEN");

                    // Storing data into SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    // Storing the key and its value as the data fetched from edittext
                    myEdit.putString("userName", my_username);

                    // Once the changes have been made,
                    // we need to commit to apply those changes made,
                    // otherwise, it will throw an error
                    myEdit.commit();



                    // Read from the database
                    myUserRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            String value = dataSnapshot.getValue(String.class);
                            Toast.makeText(getApplicationContext()
                                    , value, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Value is: " + value);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });

                    startActivity(intent);
                }
            });
        }
        }
        // maybe make this a login screen and if logged in before then go to received activity
        // here need to send token and username to database
        // database should contain username -> tokens
        // and then username -> number sent
        // username -> number received
        // username -> sticker, username pairs, timestamp?
        //          -> so that users can send same sticker multiple times?


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (my_username != null) {
            outState.putString(USERNAME, my_username);
        }
    }
}