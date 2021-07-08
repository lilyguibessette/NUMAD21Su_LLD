package edu.neu.madcourse.numad21su_lld;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText userNameText;
    private String userName;
    private DatabaseReference myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userNameText = findViewById(R.id.enter_username);

        //myDatabase = FirebaseDatabase.getInstance().getReference();

        Button loginButton = findViewById(R.id.button);
        loginButton.setOnClickListener(v -> {
            userName = userNameText.getText().toString();
            //myDatabase.child("Users").setValue("test");
            //myDatabase.child("users").child("userName").setValue(userName);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message");
            System.out.println("WTF");
            myRef.setValue("Hello, World!");

        });
    }
}