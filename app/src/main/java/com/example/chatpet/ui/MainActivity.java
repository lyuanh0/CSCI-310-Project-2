package com.example.chatpet.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatpet.ui.journal.JournalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.chatpet.R;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.logic.PetManager;
import com.example.chatpet.ui.chat.ChatActivity;
import com.example.chatpet.ui.login.LoginActivity;
import com.example.chatpet.ui.petview.PetViewActivity;
import com.example.chatpet.ui.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private AuthManager authManager;
    private PetManager petManager;
    private FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authManager = AuthManager.getInstance();
        petManager = PetManager.getInstance();

        // Check if user is logged in
        if (!authManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        //set user
        currUser = AuthManager.currentUser();

        // Check if user has a pet, if not navigate to pet creation
        if (petManager.getCurrentPet() == null) {
            navigateToPetView();
            return;
        }

        initializeViews();
        setupBottomNavigation();
    }

    private void initializeViews() {
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_pet) {
                navigateToPetView();
                return true;
            } else if (itemId == R.id.nav_chat) {
                navigateToChat();
                return true;
            } else if (itemId == R.id.nav_journal) {
                loadFragment(new JournalFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }

            return false;
        });

        // Set default selection
      //  bottomNav.setSelectedItemId(R.id.nav_pet);
    }

    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToPetView() {
        Intent intent = new Intent(this, PetViewActivity.class);
        startActivity(intent);
    }

    private void navigateToChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }


}