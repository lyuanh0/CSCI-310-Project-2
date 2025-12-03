package com.example.chatpet.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatpet.MainActivity;
import com.example.chatpet.R;
import com.example.chatpet.data.model.User;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.util.ValidationUtils;
import com.google.firebase.database.FirebaseDatabase;


public class RegistrationActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    // input fields
    EditText emailEt, passwordEt, birthdayEt, usernameEt;
    ImageView avatar1, avatar2, avatar3, avatar4;
    private int selectedAvatar = R.drawable.pf3; // default avatar

    Button registerBtn, returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // build layout with matching ids

        // find all input fields
        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        birthdayEt = findViewById(R.id.birthday);
        usernameEt = findViewById(R.id.username);
        registerBtn = findViewById(R.id.registerButton);
        returnBtn = findViewById(R.id.backButton);

        avatar1 = findViewById(R.id.avatar1);
        avatar2 = findViewById(R.id.avatar2);
        avatar3 = findViewById(R.id.avatar3);
        avatar4 = findViewById(R.id.avatar4);

        // Setup avatar selection
        setupAvatarSelection();

        // when reg button is hit, grab values
        registerBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString();
            String birthday = birthdayEt.getText().toString().trim();
            String username = usernameEt.getText().toString().trim();

            if(!ValidationUtils.isValidDate(birthday)){
                Toast.makeText(this, "Please enter a valid Date", Toast.LENGTH_SHORT).show();
                return;
            }else if(!ValidationUtils.isValidUsername(username)){
                Toast.makeText(this, "Please enter a valid Username", Toast.LENGTH_SHORT).show();
                return;
            }else if(!ValidationUtils.isValidPassword(password)){
                Toast.makeText(this, "Please Enter a valid Password", Toast.LENGTH_SHORT).show();
                return;
            }
            // create user in Firebase Auth
            AuthManager.register(email, password, (success, errorMessage) -> {
                if (success) {
                    String uid = AuthManager.currentUser().getUid();

                    // create user object
                    User newUser = new User(username, email, password, null, birthday, selectedAvatar);

                    // save to database
                    database.getReference("users").child(uid).setValue(newUser)
                            .addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Database save failed", Toast.LENGTH_LONG).show();
                                }
                            });
                    AuthManager.currentUser().sendEmailVerification().addOnCompleteListener(task -> {

                        if(task.isSuccessful()){
                            Toast.makeText(this, "Please verify your Email" , Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    Toast.makeText(this, "Auth failed: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
        //when retur button is pressed load login activity
        returnBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void setupAvatarSelection() {
        View.OnClickListener avatarClickListener = v -> {
            int avatarId = v.getId();

            if (avatarId == R.id.avatar1) selectedAvatar = R.drawable.pf1;
            else if (avatarId == R.id.avatar2) selectedAvatar = R.drawable.pf2;
            else if (avatarId == R.id.avatar3) selectedAvatar = R.drawable.pf3;
            else if (avatarId == R.id.avatar4) selectedAvatar = R.drawable.pf4;

            Toast.makeText(this, "Avatar selected!", Toast.LENGTH_SHORT).show();
        };

        avatar1.setOnClickListener(avatarClickListener);
        avatar2.setOnClickListener(avatarClickListener);
        avatar3.setOnClickListener(avatarClickListener);
        avatar4.setOnClickListener(avatarClickListener);
    }
}
