package com.example.chatpet.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chatpet.data.model.User;
import com.example.chatpet.data.model.Pet;
import com.google.gson.Gson;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "ChatPetPrefs";
    private static final String KEY_CURRENT_USER = "current_user";
    private static final String KEY_CURRENT_PET = "current_pet";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences prefs;
    private Gson gson;

    public SharedPreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // User methods
    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        prefs.edit()
                .putString(KEY_CURRENT_USER, userJson)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public User getUser() {
        String userJson = prefs.getString(KEY_CURRENT_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void clearUser() {
        prefs.edit()
                .remove(KEY_CURRENT_USER)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Pet methods
    public void savePet(Pet pet) {
        String petJson = gson.toJson(pet);
        prefs.edit()
                .putString(KEY_CURRENT_PET, petJson)
                .apply();
    }

    public Pet getPet() {
        String petJson = prefs.getString(KEY_CURRENT_PET, null);
        if (petJson != null) {
            return gson.fromJson(petJson, Pet.class);
        }
        return null;
    }

    public void clearPet() {
        prefs.edit()
                .remove(KEY_CURRENT_PET)
                .apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}