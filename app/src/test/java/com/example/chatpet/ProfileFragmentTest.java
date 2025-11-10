package com.example.chatpet;

import static org.mockito.Mockito.mock;

import com.example.chatpet.logic.AuthManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;

public class ProfileFragmentTest{
private FirebaseAuth mockAuth;
private FirebaseUser mockUser;

@Before
public void setUp() {
    mockAuth = mock(FirebaseAuth.class);
    mockUser = mock(FirebaseUser.class);
    AuthManager.setFirebaseAuth(mockAuth); // inject mock into AuthManager
}
    @Test
    public void testLoadUserData_correctFields(){
    //check if mock data is loaded into the correct variables and .setText is called for all input fields



    }
    @Test
    public void testLoadUserData_incorrectFields(){
        //check if mock data is loaded into the correct variables and .setText is called for all input fields



    }

    @Test
    public void testCheckInputFields_correctFields(){
    //set input fields to certain values check if the correct values are set into the corrisponding variables

    }
    @Test
    public void testCheckInputFields_incorrectFields(){
        //set input fields to certain values check if the correct values are set into the corrisponding variables

    }

    @Test
    public void testAvatarSetup(){
    //does the avatar update when pressed
    }

    @Test
    public void testBtnUsername_emptyField(){

    }
    @Test
    public void testBtnUsername_filledField(){

    }
    @Test
    public void testBtnPassword_emptyField(){

    }
    @Test
    public void testBtnPassword_filledField(){

    }
    @Test
    public void testBtnBirthday_emptyField(){

    }
    @Test
    public void testBtnBirthday_filledField(){

    }
    @Test
    public void testBtnLogout(){

    }

    @Test
    public void testBtnEmail_emptyField(){

    }
    @Test
    public void testBtnEmail_filledField(){

    }
    @Test
    public void testUpdateDatabase_emptyField(){

    }
    @Test
    public void testUpdateDatabase_filledField(){

    }

    @Test
    public void nullUser(){

    }

    @Test
    public void nullUserRef(){

    }

}
