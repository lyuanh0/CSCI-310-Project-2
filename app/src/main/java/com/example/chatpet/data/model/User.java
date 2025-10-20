package com.example.chatpet.data.model;

public class User {
    private String username;
    private String password;
    private Pet currentPet;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Pet getCurrentPet() { return currentPet; }
    public void setCurrentPet(Pet pet) { this.currentPet = pet; }
}