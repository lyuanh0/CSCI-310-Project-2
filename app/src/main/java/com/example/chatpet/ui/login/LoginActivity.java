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

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = AuthManager.getInstance();

        // Check if already logged in
        if (authManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validate input
        String usernameError = ValidationUtils.getUsernameError(username);
        if (usernameError != null) {
            etUsername.setError(usernameError);
            return;
        }

        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            etPassword.setError(passwordError);
            return;
        }

        // Attempt login
        boolean success = authManager.login(username, password);

        if (success) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validate input
        String usernameError = ValidationUtils.getUsernameError(username);
        if (usernameError != null) {
            etUsername.setError(usernameError);
            return;
        }

        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            etPassword.setError(passwordError);
            return;
        }

        // Attempt registration
        boolean success = authManager.register(username, password);

        if (success) {
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
        } else {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}