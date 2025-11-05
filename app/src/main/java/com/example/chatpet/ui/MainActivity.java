package com.example.chatpet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.chatpet.ui.journal.JournalFragment;
import com.example.chatpet.ui.petview.PetViewFragment;


import com.example.chatpet.R;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.logic.PetManager;
import com.example.chatpet.ui.chat.ChatActivity;
import com.example.chatpet.ui.journal.JournalActivity;
import com.example.chatpet.ui.login.LoginActivity;
import com.example.chatpet.ui.petview.PetViewActivity;
import com.example.chatpet.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private AuthManager authManager;
    private PetManager petManager;
    private FirebaseUser currUser;

    // Receive result from ChatActivity to grant XP
    private final ActivityResultLauncher<Intent> chatLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean chatted = result.getData().getBooleanExtra("chatted", false);
                    if (chatted && petManager.getCurrentPet() != null) {
                        petManager.getCurrentPet().addXP(10);
                        Toast.makeText(this, "+10 XP for chatting!", Toast.LENGTH_SHORT).show();
                    }
                }
                // keep nav state sane after returning
                if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_pet);
            });

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

        bottomNav = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();
    }

    private void initializeViews() {
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pet) {
                loadFragment(new PetViewFragment());
                return true;
            } else if (id == R.id.nav_chat) {
                navigateToChat();
                return true;
            } else if (id == R.id.nav_journal) {
                loadFragment(new JournalFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_pet);
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    // CHAT tab → gate on happiness, then launch for result so we can award XP
    private void navigateToChat() {
        if (petManager.getCurrentPet() == null) {
            Toast.makeText(this, "Create a pet first!", Toast.LENGTH_SHORT).show();
            bottomNav.setSelectedItemId(R.id.nav_pet);
            return;
        }
        if (petManager.getCurrentPet().getHappiness() > 90) {
            Toast.makeText(this, "Your pet is already super happy—try another activity!", Toast.LENGTH_SHORT).show();
            bottomNav.setSelectedItemId(R.id.nav_pet);
            return;
        }

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("petName", petManager.getCurrentPet().getName());
        intent.putExtra("petType", petManager.getCurrentPet().getType());
        chatLauncher.launch(intent);
    }


}
