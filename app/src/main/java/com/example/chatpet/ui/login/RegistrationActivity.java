package com.example.chatpet.ui.login;

import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatpet.MainActivity;
import com.example.chatpet.R;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.data.model.User;
import com.example.chatpet.logic.AuthManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();

// intake variables
    EditText emailEt, passwordEt, birthdayEt, avatarEt, usernameEt;
    Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // build layout with matching ids

        //find all input fields
        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        birthdayEt = findViewById(R.id.birthday);
        avatarEt = findViewById(R.id.avatar);
        usernameEt = findViewById(R.id.username);
        registerBtn = findViewById(R.id.registerButton);

        //when reg button is hit, grab values
        registerBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString();
            String birthday = birthdayEt.getText().toString().trim();
            String avatar = avatarEt.getText().toString().trim();
            String username = usernameEt.getText().toString().trim();

            //create user
            AuthManager.register(email,password,(success, errorMessage) -> {
                if(success){
                    //get current user Id
                    String uid = AuthManager.currentUser().getUid();
                    //create user/pet
                    User newUser = new User(username,email,password,null,birthday,avatar);
                    //save to database
                    database.getReference("users").child(uid).setValue(newUser).addOnCompleteListener(dbTask ->
                    { if(dbTask.isSuccessful()){
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        finish();
                        }else{
                        Toast.makeText(this, "Database save failed", Toast.LENGTH_LONG).show();
                    }
                    });
                    //call setup pet/ initialize pet
                    //save user data to database

                } else{
                    // auth failed send error message
                    Toast.makeText(this, "Auth failed: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
