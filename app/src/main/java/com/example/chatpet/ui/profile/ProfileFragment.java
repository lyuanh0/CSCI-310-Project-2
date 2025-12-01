package com.example.chatpet.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

import com.example.chatpet.R;
import com.example.chatpet.util.ValidationUtils;
import com.example.chatpet.data.model.User;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private TextView helloUserText;
    public EditText etUsername;
    private EditText etBirthday;
    private EditText etPassword;
    public EditText etEmail;
    public Button btnSaveUsername;
    private Button btnSaveBirthday;
    private Button btnSavePassword;
    public Button btnSaveEmail;
    public Button btnLogout;
    private ImageView avatar0, avatar1, avatar2, avatar3, avatar4;

    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private User user;


    public int selectedAvatar = R.drawable.catawake1; // default avatar

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        currentUser = AuthManager.currentUser();

        if (currentUser != null) {
            userRef = AuthManager.FirebaseDatabaseProvider.getUsersRef(currentUser.getUid());
        }

        // Find all views
        helloUserText = view.findViewById(R.id.hello_user_text);
        etUsername = view.findViewById(R.id.et_username);
        etBirthday = view.findViewById(R.id.et_birthday);
        etPassword = view.findViewById(R.id.et_password);
        etEmail = view.findViewById(R.id.et_email);

        btnSaveUsername = view.findViewById(R.id.btn_save_username);
        btnSaveBirthday = view.findViewById(R.id.btn_save_birthday);
        btnSavePassword = view.findViewById(R.id.btn_save_password);
        btnSaveEmail = view.findViewById(R.id.btn_save_email);
        btnLogout = view.findViewById(R.id.btn_logout);

        avatar0 = view.findViewById(R.id.avatar0);
        avatar1 = view.findViewById(R.id.avatar1);
        avatar2 = view.findViewById(R.id.avatar2);
        avatar3 = view.findViewById(R.id.avatar3);
        avatar4 = view.findViewById(R.id.avatar4);

        setupAvatarSelection();
        setupButtons();
        loadUserData();

        // Example greeting
        if (currentUser != null && currentUser.getDisplayName() != null) {
            helloUserText.setText("Hello " + currentUser.getDisplayName());
        }

        return view;
    }

    private void setupAvatarSelection() {
        View.OnClickListener avatarClickListener = v -> {
            int avatarId = v.getId();

            if (avatarId == R.id.avatar1) selectedAvatar = R.drawable.pf1;
            else if (avatarId == R.id.avatar2) selectedAvatar = R.drawable.pf2;
            else if (avatarId == R.id.avatar3) selectedAvatar = R.drawable.pf3;
            else if (avatarId == R.id.avatar4) selectedAvatar = R.drawable.pf4;

            avatar0.setImageResource(selectedAvatar);

            // Optional: Save avatar to database
            if (userRef != null)
                userRef.child("avatar").setValue(selectedAvatar);

            //update pic

        };

        avatar1.setOnClickListener(avatarClickListener);
        avatar2.setOnClickListener(avatarClickListener);
        avatar3.setOnClickListener(avatarClickListener);
        avatar4.setOnClickListener(avatarClickListener);
    }

    private void setupButtons() {
        btnSaveUsername.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (ValidationUtils.isValidUsername(username)) {
                userRef.child("username").setValue(username);
                Toast.makeText(getContext(), "Username updated!", Toast.LENGTH_SHORT).show();
                helloUserText.setText("Hello " + username);
            }
        });

        btnSaveBirthday.setOnClickListener(v -> {
            String birthday = etBirthday.getText().toString().trim();
            if (ValidationUtils.isValidDate(birthday)) {
                userRef.child("birthday").setValue(birthday);
                Toast.makeText(getContext(), "Birthday updated!", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getContext(), "Please enter a valid Date", Toast.LENGTH_SHORT).show();

            }
        });

        btnSavePassword.setOnClickListener(v -> {
            String newPassword = etPassword.getText().toString().trim();
            if (!newPassword.isEmpty() && currentUser != null) {
                currentUser.updatePassword(newPassword)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        userRef.child("password").setValue(newPassword);
                                        Toast.makeText(getContext(), "Password updated!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getContext(), "Failed: " , Toast.LENGTH_SHORT).show();
                                    }
                                });

            }
        });

        btnSaveEmail.setOnClickListener(v -> {
            String newEmail = etEmail.getText().toString().trim();
            if (!newEmail.isEmpty() && currentUser != null) {
                currentUser.updateEmail(newEmail)
                        .addOnSuccessListener(unused -> {
                            if (userRef != null)
                                userRef.child("email").setValue(newEmail);
                            Toast.makeText(getContext(), "Email updated!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Logged out!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireActivity(), LoginActivity.class));

        });
    }
    private void loadUserData() {
        if (userRef == null) return;

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get user fields safely
                    String username = snapshot.child("username").getValue(String.class);
                    String birthday = snapshot.child("birthday").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    Integer avatarRes = snapshot.child("avatar").getValue(Integer.class);

                    // Update UI
                    if (username != null && !username.isEmpty()) {
                        etUsername.setText(username);
                        helloUserText.setText("Hello " + username);
                    }

                    if (birthday != null && !birthday.isEmpty())
                        etBirthday.setText(birthday);

                    if (email != null && !email.isEmpty())
                        etEmail.setText(email);

                    if (avatarRes != null) {
                        selectedAvatar = avatarRes;
                        avatar0.setImageResource(avatarRes);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

