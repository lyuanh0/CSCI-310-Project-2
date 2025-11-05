package com.example.chatpet.logic;

import com.example.chatpet.data.model.Chat;
import com.example.chatpet.data.model.Message;
import com.example.chatpet.data.model.Pet;

import java.util.List;

public class ChatManager {
    private static ChatManager instance;
    private Chat currentChat;
    private PetManager petManager;

    private ChatManager() {
        currentChat = new Chat();
        petManager = PetManager.getInstance();
    }

    public static ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public String sendMessage(String userMessage) {
        Pet currentPet = petManager.getCurrentPet();

        // Add user message to chat
        Message userMsg = new Message("User", userMessage);
        currentChat.getMessages().add(userMsg);

        // Get response from LLM (placeholder for now)
//        String petResponse = llmClient.generateResponse(userMessage, currentPet);
        String petResponse = null;
        // Add pet response to chat
        Message petMsg = new Message(currentPet != null ? currentPet.getName() : "Pet", petResponse);
        currentChat.getMessages().add(petMsg);

        // Update pet happiness based on conversation
        if (currentPet != null) {
            petManager.updateHappiness(1); // Small happiness boost per message
        }

        return petResponse;
    }

    public List<Message> getMessages() {
        return currentChat.getMessages();
    }

    public void clearChat() {
        currentChat = new Chat();
    }

    public Chat getCurrentChat() {
        return currentChat;
    }
}