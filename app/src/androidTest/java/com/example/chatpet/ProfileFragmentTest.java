package com.example.chatpet;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.chatpet.R;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.ui.login.LoginActivity;
import com.example.chatpet.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProfileFragmentTest {

    FirebaseAuth mockAuth;
    FirebaseUser mockUser;
    FirebaseDatabase mockDb;
    DatabaseReference mockUsersRef;
    DatabaseReference mockUserRef;

    @Before
    public void setup() {
        // Mock FirebaseAuth and currentUser
        mockAuth = Mockito.mock(FirebaseAuth.class);
        mockUser = Mockito.mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("TEST_UID");
        when(mockUser.getDisplayName()).thenReturn("Tester");
        AuthManager.setFirebaseAuth(mockAuth);

        // Mock FirebaseDatabase
        mockDb = Mockito.mock(FirebaseDatabase.class);
        mockUsersRef = Mockito.mock(DatabaseReference.class);
        mockUserRef = Mockito.mock(DatabaseReference.class);

        // Wire the mock calls
        when(mockDb.getReference("users")).thenReturn(mockUsersRef);
        when(mockUsersRef.child("TEST_UID")).thenReturn(mockUserRef);
        when(mockUserRef.child(Mockito.anyString())).thenReturn(mockUserRef);
        when(mockUserRef.setValue(Mockito.any())).thenReturn(Mockito.mock(Task.class));

        // Inject mock into provider
        AuthManager.FirebaseDatabaseProvider.setFirebaseDatabase(mockDb);
    }


    @Test
    public void testGreetingLoads() {
        FragmentScenario<ProfileFragment> scenario = FragmentScenario.launch(ProfileFragment.class);

        scenario.onFragment(fragment -> {
            TextView hello = fragment.getView().findViewById(R.id.hello_user_text);
            assert(hello.getText().toString().equals("Hello Tester"));
        });
    }

    @Test
    public void testAvatarSelectionUpdatesPreviewAndDatabase() {
        FragmentScenario<ProfileFragment> scenario = FragmentScenario.launch(ProfileFragment.class);

        scenario.onFragment(fragment -> {
            ImageView avatar2 = fragment.getView().findViewById(R.id.avatar2);

            avatar2.performClick();

            // selectedAvatar should now be pf2
            assert(fragment.selectedAvatar == R.drawable.pf2);

            // Firebase write should be triggered
            verify(mockUserRef).child("avatar");
            verify(mockUserRef.child("avatar")).setValue(R.drawable.pf2);
        });
    }


    @Test
    public void testSaveUsernameWritesToDatabase() {
        FragmentScenario<ProfileFragment> scenario = FragmentScenario.launch(ProfileFragment.class);

        scenario.onFragment(fragment -> {
            fragment.etUsername.setText("Alice");

            fragment.btnSaveUsername.performClick();

            verify(mockUserRef).child("username");
        });
    }

    @Test
    public void testSaveEmailWritesToDatabase() {
        com.google.android.gms.tasks.Task<Void> mockTask = Mockito.mock(com.google.android.gms.tasks.Task.class);

        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockTask;
        }).when(mockTask).addOnSuccessListener(Mockito.any());

        // Mock currentUser.updateEmail to return the mock task
        when(mockUser.updateEmail(anyString())).thenReturn(mockTask);

        FragmentScenario<ProfileFragment> scenario = FragmentScenario.launch(ProfileFragment.class);

        scenario.onFragment(fragment -> {
            fragment.etEmail.setText("new@email.com");
            fragment.btnSaveEmail.performClick();

            verify(mockUser).updateEmail("new@email.com");
            verify(mockUserRef).child("email");
            verify(mockUserRef.child("email")).setValue("new@email.com");
        });
    }



}
