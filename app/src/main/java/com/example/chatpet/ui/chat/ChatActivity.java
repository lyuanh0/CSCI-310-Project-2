// ChatActivity.java

package com.example.chatpet.ui.chat;
import android.content.Intent;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpet.R;
import com.example.chatpet.data.model.Message;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.logic.ChatGenerator;
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
    private boolean hasSentMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatManager = ChatManager.getInstance();
        petManager = PetManager.getInstance();
        currentPet = petManager.getCurrentPet();
        if (currentPet == null) currentPet = new Pet("Buddy", "Dog");

        // 🔹 Handle system/gesture back: clear chat, return OK so caller can grant XP
        // system/gesture back
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ChatManager.getInstance().getMessages().clear();
                Intent data = new Intent();
                data.putExtra("chatted", hasSentMessage);   // ✅ pass flag
                setResult(RESULT_OK, data);
                finish();
            }
        });


        // ── Title + back arrow using petName from intent (fallback to currentPet name)
        String titleName = getIntent().getStringExtra("petName");
        if (titleName == null || titleName.isEmpty()) titleName = currentPet.getName();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        setupRecyclerView();
        setupListeners();

        // Optional welcome bubble on first open
        if (chatManager.getMessages().isEmpty()) {
            String petType = getIntent().getStringExtra("petType");
            if (petType == null || petType.isEmpty()) petType = currentPet.getType();
            petType = petType == null ? "dog" : petType.trim().toLowerCase();

            String greeting;
            switch (petType) {
                case "cat":    greeting = "Meow! I'm happy to see you!"; break;
                case "dragon": greeting = "Roar! I’m ready for adventure!"; break;
                case "fish":   greeting = "Glub glub! I’m bubbling with joy to meet you!"; break;
                default:       greeting = "Woof! I'm happy to meet you!"; break;
            }

            chatManager.getMessages().add(new Message(currentPet.getName(), greeting));
            chatAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ChatManager.getInstance().getMessages().clear();
            Intent data = new Intent();
            data.putExtra("chatted", hasSentMessage);   // ✅ pass flag
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (message.isEmpty()) return;

        hasSentMessage = true;
        etMessage.setText("");
        chatManager.getMessages().add(new Message("User", message));
        chatAdapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

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
}