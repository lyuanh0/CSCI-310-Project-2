package com.example.chatpet.ui.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpet.R;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.logic.ChatManager;
import com.example.chatpet.logic.PetManager;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;

    private ChatAdapter chatAdapter;
    private ChatManager chatManager;
    private PetManager petManager;
    private Pet currentPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatManager = ChatManager.getInstance();
        petManager = PetManager.getInstance();
        currentPet = petManager.getCurrentPet();

        if (currentPet == null) {
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupListeners();

        // Show welcome message
        if (chatManager.getMessages().isEmpty()) {
            String welcomeMessage = "Hi! I'm " + currentPet.getName() + "! Let's chat!";
            chatManager.sendMessage("Hello");
            chatAdapter.notifyDataSetChanged();
        }
    }

    private void initializeViews() {
        rvMessages = findViewById(R.id.rv_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chat with " + currentPet.getName());
        }
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(chatManager.getMessages(), this);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        if (message.isEmpty()) {
            return;
        }

        // Clear input
        etMessage.setText("");

        // Send message and get response
        String response = chatManager.sendMessage(message);

        // Update UI
        chatAdapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }
}