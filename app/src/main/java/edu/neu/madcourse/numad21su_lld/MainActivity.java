package edu.neu.madcourse.numad21su_lld;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // maybe make this a login screen and if logged in before then go to received activity
        // here need to send token and username to database
        // database should contain username -> tokens
        // and then username -> number sent
        // username -> number received
        // username -> sticker, username pairs, timestamp?
        //          -> so that users can send same sticker multiple times?
    }
}