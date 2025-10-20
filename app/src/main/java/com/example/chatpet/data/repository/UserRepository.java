package com.example.chatpet.data.repository;

import com.example.chatpet.data.model.User;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static UserRepository instance;
    private Map<String, User> users; // Placeholder - will be replaced with MySQL

    /*
    THIS IS A VERY CRUDE IMPLEMENTATION WITH A HASHMAP.
    IDEALLY, WE WANNA REPLACE THIS WITH MySQL TO IMPROVE IT!
    */

    private UserRepository() {
        users = new HashMap<>();
        // Add some test data
        users.put("testuser", new User("testuser", "password123"));
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public boolean createUser(User user) {
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public boolean updateUser(User user) {
        if (!users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }

    public boolean deleteUser(String username) {
        return users.remove(username) != null;
    }
}