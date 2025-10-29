package com.example.chatpet.logic;

import com.example.chatpet.data.model.User;
import com.example.chatpet.data.repository.UserRepository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
public class AuthManager {
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static AuthManager instance;

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }
    public static boolean isUserLoggedIn(){
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            return true;
        }
        return false;
    }
    public interface AuthCallback {
        void onComplete(boolean success, String errorMessage);
    }

    public static void register(String email, String password, final AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onComplete(true, null);
                    else callback.onComplete(false, task.getException() != null ? task.getException().getMessage() : "Unknown error");
                });
    }

    public static void login(String email, String password, final AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onComplete(true, null);
                    else callback.onComplete(false, task.getException() != null ? task.getException().getMessage() : "Unknown error");
                });
    }

    public static void logout() {
        auth.signOut();
    }

    public static FirebaseUser currentUser() {
        return auth.getCurrentUser();
    }

    public static boolean isLoggedIn(){
        FirebaseUser curr = currentUser();

        if(curr != null){
            return true;
        }
        return false;
    }
}