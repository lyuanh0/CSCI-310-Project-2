package com.example.chatpet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.chatpet.logic.AuthManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;


public class AuthManagerTest {
    private FirebaseAuth mockAuth;
    private FirebaseUser mockUser;

    @Before
    public void setUp() {
        mockAuth = mock(FirebaseAuth.class);
        mockUser = mock(FirebaseUser.class);
        AuthManager.setFirebaseAuth(mockAuth); // inject mock into AuthManager
    }


    @Test
    public void testCurrentUser_ValidUser() {
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);

        assertSame("Expected mockUser", mockUser, AuthManager.currentUser());
    }

    @Test
    public void testCurrentUser_NoUser() {
        when(mockAuth.getCurrentUser()).thenReturn(null);

        assertNull("Expected null", AuthManager.currentUser());
    }

    @Test
    public void testIsLoggedIn_ValidUser() {
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);

        assertTrue("Expected true", AuthManager.isLoggedIn());
    }

    @Test
    public void testIsLoggedIn_NoUser() {
        when(mockAuth.getCurrentUser()).thenReturn(null);

        assertFalse("Expected false", AuthManager.isLoggedIn());
    }



}
