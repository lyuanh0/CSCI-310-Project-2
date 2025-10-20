package com.example.chatpet.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatpet.R;
import com.example.chatpet.data.model.User;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.ui.login.LoginActivity;

public class ProfileFragment extends Fragment {
    private TextView tvUsername;
    private Button btnLogout;
    private Button btnChangePassword;

    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        authManager = AuthManager.getInstance();

        initializeViews(view);
        setupListeners();
        loadUserData();

        return view;
    }

    private void initializeViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> handleLogout());
        btnChangePassword.setOnClickListener(v -> handleChangePassword());
    }

    private void loadUserData() {
        User currentUser = authManager.getCurrentUser();
        if (currentUser != null) {
            tvUsername.setText("Username: " + currentUser.getUsername());
        }
    }

    private void handleLogout() {
        authManager.logout();
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void handleChangePassword() {
        // TODO: Implement change password dialog
        Toast.makeText(getContext(), "Change password feature coming soon!", Toast.LENGTH_SHORT).show();
    }
}