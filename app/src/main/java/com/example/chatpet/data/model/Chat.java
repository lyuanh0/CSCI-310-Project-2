package com.example.chatpet.data.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<Message> messages;
    private List<Message> userResponses;

    public Chat() {
        this.messages = new ArrayList<>();
        this.userResponses = new ArrayList<>();
    }

    public String chatWithUser(String userMessage) {
        Message userMsg = new Message("User", userMessage);
        messages.add(userMsg);

        // Placeholder response - will be replaced with LLM
        String petResponse = "I'm so happy you're talking to me!";
        Message petMsg = new Message("Pet", petResponse);
        messages.add(petMsg);

        return petResponse;
    }

    public String chatWithPet(String petMessage) {
        Message petMsg = new Message("Pet", petMessage);
        messages.add(petMsg);
        return petMessage;
    }

    public void displayMessages() {
        for (Message msg : messages) {
            System.out.println(msg.getSender() + ": " + msg.getText());
        }
    }

    public List<Message> getMessages() { return messages; }
    public List<Message> getUserResponses() { return userResponses; }
}