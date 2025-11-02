package com.example.chatpet.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatpet.R;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.ui.MainActivity;
import com.example.chatpet.util.ValidationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private EditText passwordEt, emailEt;
    private Button btnLogin;
    private Button btnRegister;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //find all input fields
        emailEt = findViewById(R.id.et_Email);
        passwordEt = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btnRegister);

        //add listeners<-------------------------------------------------->

        //Login listener
        btnLogin.setOnClickListener(v -> {
            //grab values
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString();

            AuthManager.login(email,password,(boolean success, String errorMessage) -> {
                if(success){
                    Toast.makeText(this,"You have been logged in",Toast.LENGTH_LONG).show();
                    //open dashboard
                    Toast.makeText(this,"Loaded Dashboard",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else{
                    Toast.makeText(this,"Login failed",Toast.LENGTH_LONG).show();

                }
            });
        });

        //Register listener
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    }

