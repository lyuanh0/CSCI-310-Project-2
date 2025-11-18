package com.example.chatpet.logic;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
//    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseAuth auth;
    private static AuthManager instance;

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    private static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    //just for testing
    public static void setFirebaseAuth(FirebaseAuth mockAuth) {
        auth = mockAuth;
    }
    public interface AuthCallback {
        void onComplete(boolean success, String errorMessage);
    }
    private static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
    public static void register(String email, String password, final AuthCallback callback) {
        getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onComplete(true, null);
                    else callback.onComplete(false, task.getException() != null ? task.getException().getMessage() : "Unknown error");
                });
    }

    public static void login(String email, String password, final AuthCallback callback) {
        getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onComplete(true, null);
                    else callback.onComplete(false, task.getException() != null ? task.getException().getMessage() : "Unknown error");
                });
    }

    public static void logout() {
        getAuth().signOut();
    }

    public static FirebaseUser currentUser() {
        return getAuth().getCurrentUser();
    }

    public static boolean isLoggedIn(){
        FirebaseUser curr = currentUser();

        if(curr != null){
            return true;
        }
        return false;
    }
}