package com.example.chatpet.data.repository;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatpet.R;
import com.example.chatpet.logic.AuthManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class DatabaseRepository {
    private static final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    public interface DBCallback {
        void onComplete(boolean success, String errorMessage);
    }

//    public static void saveUserProfile(String uid, Fireba profile, final DBCallback callback) {
//        rootRef.child("users").child(uid).setValue(profile)
//                .addOnSuccessListener(aVoid -> callback.onComplete(true, null))
//                .addOnFailureListener(e -> callback.onComplete(false, e.getMessage()));
//    }

    public static DatabaseReference userRef(String uid) {
        return rootRef.child("users").child(uid);
    }
}
