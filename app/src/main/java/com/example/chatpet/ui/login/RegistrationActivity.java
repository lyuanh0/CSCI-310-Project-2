package com.example.chatpet.ui.login;

import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatpet.R;
import com.example.chatpet.logic.AuthManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
// intake variables
    EditText emailEt, passwordEt, birthdayEt, petTypeEt, petNameEt, avatarEt;
    Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // build layout with matching ids

        //grab values
        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        birthdayEt = findViewById(R.id.birthday);
        petNameEt = findViewById(R.id.petName);
        petTypeEt = findViewById(R.id.petType);
        avatarEt = findViewById(R.id.avatar);

        registerBtn = findViewById(R.id.registerButton);

        //when reg button is hit set values
        registerBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString();
            String birthday = birthdayEt.getText().toString().trim();
            String petName = petNameEt.getText().toString().trim();
            String petType = petTypeEt.getText().toString().trim();
            String avatar = avatarEt.getText().toString().trim();

            //create user
            AuthManager.register(email,password,(success, errorMessage) -> {
                if(success){
                    //get current user Id
                    String uid = AuthManager.currentUser().getUid();
                    //call setup pet/ initialize pet
                    //save user data to database

                } else{
                    // auth failed send error message
                }
            });
        });
    };
}
