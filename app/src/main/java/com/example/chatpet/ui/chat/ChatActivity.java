// ChatActivity.java

// ChatActivity.java (Java 11 compatible)

package com.example.chatpet.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpet.R;
import com.example.chatpet.data.model.Message;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.logic.ChatGenerator;
import com.example.chatpet.logic.ChatManager;
import com.example.chatpet.logic.PetManager;
import com.example.chatpet.ui.MainActivity;
import com.example.chatpet.ui.journal.JournalActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private BottomNavigationView bottomNav;

    private ChatAdapter chatAdapter;
    private ChatManager chatManager;
    private PetManager petManager;
    private Pet currentPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatManager = ChatManager.getInstance();
        petManager  = PetManager.getInstance();
        currentPet  = petManager.getCurrentPet();
        if (currentPet == null) currentPet = new Pet("Buddy", "Dog");

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        String titleName = getIntent().getStringExtra("petName");
        if (titleName == null || titleName.isEmpty()) titleName = currentPet.getName();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(titleName);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { clearChatAndFinish(); }
        });

        rvMessages = findViewById(R.id.rv_messages);
        etMessage  = findViewById(R.id.et_message);
        btnSend    = findViewById(R.id.btn_send);
        bottomNav  = findViewById(R.id.bottom_navigation);

        if (currentPet.getHappiness() > 90) {
            Toast.makeText(this, "Your pet is already super happy—chat is disabled.", Toast.LENGTH_SHORT).show();
            clearChatAndFinish();
            return;
        }

        bottomNav.setSelectedItemId(R.id.nav_chat);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            String dest = null;
            if (id == R.id.nav_chat) return true;
            if (id == R.id.nav_pet) dest = "pet";
            else if (id == R.id.nav_journal) dest = "journal";
            else if (id == R.id.nav_profile) dest = "profile";

            clearChat();
            Intent i = new Intent(this, MainActivity.class)
                    .putExtra("dest", dest)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
            return true;
        });

        chatAdapter = new ChatAdapter(chatManager.getMessages(), this);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> sendMessage());
        etMessage.setOnEditorActionListener((v, actionId, event) -> { sendMessage(); return true; });

        if (chatManager.getMessages().isEmpty()) {
            String petType = getIntent().getStringExtra("petType");
            if (petType == null || petType.isEmpty()) petType = currentPet.getType();
            petType = (petType == null) ? "dog" : petType.trim().toLowerCase();

            // ---- Java 11 friendly switch ----
            String greeting;
            switch (petType) {
                case "cat":
                    greeting = "Meow! I'm happy to see you!";
                    break;
                case "dragon":
                    greeting = "Roar! I’m ready for adventure!";
                    break;
                case "fish":
                    greeting = "Glub glub! I’m bubbling with joy to meet you!";
                    break;
                default:
                    greeting = "Woof! I'm happy to meet you!";
                    break;
            }
            // ---------------------------------

            chatManager.getMessages().add(new Message(currentPet.getName(), greeting));
            chatAdapter.notifyDataSetChanged();
        }
    }

    private void sendMessage() {
        if (currentPet.getHappiness() > 90) {
            Toast.makeText(this, "Chat disabled — your pet is already super happy!", Toast.LENGTH_SHORT).show();
            clearChatAndFinish();
            return;
        }

        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        etMessage.setText("");
        chatManager.getMessages().add(new Message("User", message));
        chatAdapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        Pet pet = petManager.getCurrentPet();
        if (pet != null) pet.addXP(5); // +5 XP per message

        Message thinking = new Message(currentPet.getName(), "Thinking...");
        chatManager.getMessages().add(thinking);
        chatAdapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        ChatGenerator.getInstance().generateChatResponse(this, message, new ChatGenerator.ChatCallback() {
            @Override public void onLoading() { }

            @Override
            public void onSuccess(String response) {
                chatManager.getMessages().remove(thinking);
                chatManager.getMessages().add(new Message(currentPet.getName(), response));
                chatAdapter.notifyDataSetChanged();
                rvMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onError(String error) {
                chatManager.getMessages().remove(thinking);
                chatManager.getMessages().add(new Message("System", "Oops! I couldn't think of a reply."));
                chatAdapter.notifyDataSetChanged();
                rvMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }

    private void clearChat() {
        ChatManager.getInstance().getMessages().clear();
    }

    private void clearChatAndFinish() {
        clearChat();
        setResult(RESULT_CANCELED);
        finish();
    }
}
