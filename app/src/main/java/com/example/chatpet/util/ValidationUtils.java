package com.example.chatpet.util;

public class ValidationUtils {
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_PET_NAME_LENGTH = 1;
    private static final int MAX_PET_NAME_LENGTH = 15;

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        int length = username.length();
        if (length < MIN_USERNAME_LENGTH || length > MAX_USERNAME_LENGTH) {
            return false;
        }

        // Username should only contain alphanumeric characters and underscores
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean isValidPetName(String petName) {
        if (petName == null || petName.trim().isEmpty()) {
            return false;
        }

        int length = petName.trim().length();
        return length >= MIN_PET_NAME_LENGTH && length <= MAX_PET_NAME_LENGTH;
    }

    public static String getUsernameError(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty";
        }

        int length = username.length();
        if (length < MIN_USERNAME_LENGTH) {
            return "Username must be at least " + MIN_USERNAME_LENGTH + " characters";
        }

        if (length > MAX_USERNAME_LENGTH) {
            return "Username must be at most " + MAX_USERNAME_LENGTH + " characters";
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "Username can only contain letters, numbers, and underscores";
        }

        return null;
    }

    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
        }

        return null;
    }

    public static String getPetNameError(String petName) {
        if (petName == null || petName.trim().isEmpty()) {
            return "Pet name cannot be empty";
        }

        int length = petName.trim().length();
        if (length < MIN_PET_NAME_LENGTH) {
            return "Pet name must be at least " + MIN_PET_NAME_LENGTH + " character";
        }

        if (length > MAX_PET_NAME_LENGTH) {
            return "Pet name must be at most " + MAX_PET_NAME_LENGTH + " characters";
        }

        return null;
    }
}