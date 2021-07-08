package edu.neu.madcourse.numad21su_lld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    public String my_username;
    private static final String USERNAME = "USERNAME";
    Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO make it so login screen doesn't always appear
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME)) {
            Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
            startActivity(intent);
        } else{
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
                    DatabaseReference myRef = database.getReference("message");

                    myRef.setValue("Hello, World!");
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