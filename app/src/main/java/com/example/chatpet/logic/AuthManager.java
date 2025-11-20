package com.example.chatpet.logic;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthManager {
//    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseAuth auth;
    private static AuthManager instance;
    private static boolean testMode = false;
    private static LoginProvider loginProvider = new FirebaseLoginProvider();

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }
    public interface LoginProvider {
        void login(String email, String password, AuthCallback callback);
    }
    public static void setLoginProvider(LoginProvider provider) {
        loginProvider = provider;
    }
    public static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
    public static void setTestMode(boolean value) {
        testMode = value;
    }

    public static boolean isTestMode() {
        return testMode;
    }
    //just for testing
    public static void setFirebaseAuth(FirebaseAuth mockAuth) {
        auth = mockAuth;
    }
    public interface AuthCallback {
        void onComplete(boolean success, String errorMessage);
    }

    public static void register(String email, String password, final AuthCallback callback) {
        getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onComplete(true, null);
                    else callback.onComplete(false, task.getException() != null ? task.getException().getMessage() : "Unknown error");
                });
    }

    public static void login(String email, String password, AuthCallback callback) {
        loginProvider.login(email, password, callback);
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
    private static class FirebaseLoginProvider implements LoginProvider {
        public void login(String email, String password, AuthCallback callback) {
            getAuth().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) callback.onComplete(true, null);
                        else callback.onComplete(false,
                                task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    });
        }
    }
    public static class FirebaseDatabaseProvider {
        private static FirebaseDatabase mockDb;

        public static FirebaseDatabase get() {
            return mockDb != null ? mockDb : FirebaseDatabase.getInstance();
        }

        public static void setFirebaseDatabase(FirebaseDatabase db) {
            mockDb = db;
        }
        public static DatabaseReference getUsersRef(String uid) {
            return get().getReference("users").child(uid);
        }
    }
}