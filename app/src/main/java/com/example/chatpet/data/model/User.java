package com.example.chatpet.data.model;

import java.util.Date;

public class User {
    private String username;
    private String email;
    private String password;
    private Pet currentPet;
    private String birthday;
    private String avatar;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User(String username, String email, String password, Pet currentPet, String birthday, String avatar) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.currentPet = currentPet;
        this.birthday = birthday;
        this.avatar = avatar;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getBirthday() {return  birthday;}
    public void setBirthday(String birthday){this.birthday = birthday;}
    public String getAvatar(){return avatar;}
    public void setAvatar(String avatar){this.avatar = avatar;}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Pet getCurrentPet() { return currentPet; }
    public void setCurrentPet(Pet pet) { this.currentPet = pet; }
    public String getEmail() {return email;}
    public void setEmail(String emaill){this.email = emaill;}

    public void choosePet(String petType, String petName) {

    }

    public void createChat() {

    }

    public void openJournal() {

    }
}