package edu.neu.madcourse.numad21su_lld;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public String my_username;
    private static final String USERNAME = "USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME)) {
            Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_login);
        }
        // maybe make this a login screen and if logged in before then go to received activity
        // here need to send token and username to database
        // database should contain username -> tokens
        // and then username -> number sent
        // username -> number received
        // username -> sticker, username pairs, timestamp?
        //          -> so that users can send same sticker multiple times?
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(USERNAME, my_username);
        super.onSaveInstanceState(outState);
    }

}