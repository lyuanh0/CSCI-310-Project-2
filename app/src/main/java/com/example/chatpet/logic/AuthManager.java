package com.example.chatpet.logic;

import com.example.chatpet.data.model.User;
import com.example.chatpet.data.repository.UserRepository;

public class AuthManager {
    private static AuthManager instance;
    private UserRepository userRepository;
    private User currentUser;

    private AuthManager() {
        userRepository = UserRepository.getInstance();
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public boolean register(String username, String password) {
        // Validate username and password
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        // Check if user already exists
        if (userRepository.userExists(username)) {
            return false;
        }

        // Create new user
        User newUser = new User(username, password);
        return userRepository.createUser(newUser);
    }

    public boolean login(String username, String password) {
        User user = userRepository.getUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean updateUsername(String oldUsername, String newUsername) {
        if (currentUser == null) return false;

        currentUser.setUsername(newUsername);
        return userRepository.updateUser(currentUser);
    }

    public boolean updatePassword(String oldPassword, String newPassword) {
        if (currentUser == null) return false;

        if (!currentUser.getPassword().equals(oldPassword)) {
            return false;
        }

        currentUser.setPassword(newPassword);
        return userRepository.updateUser(currentUser);
    }
}