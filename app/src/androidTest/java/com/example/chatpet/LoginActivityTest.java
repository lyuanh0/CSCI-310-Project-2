package com.example.chatpet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.ui.MainActivity;
import com.example.chatpet.ui.login.LoginActivity;
import com.example.chatpet.ui.login.RegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {
    private FirebaseAuth mockAuth;
    private FirebaseUser mockUser;


    @Before
    public void setup() {
        Intents.init();   // start intent capturing (only once)
        AuthManager.setTestMode(true);
        // Mock FirebaseAuth and FirebaseUser
        mockAuth = mock(FirebaseAuth.class);
        mockUser = mock(FirebaseUser.class);
        when(mockUser.getUid()).thenReturn("testUserId");
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);

        AuthManager.setFirebaseAuth(mockAuth);
    }

    @After
    public void cleanup() {
        AuthManager.setTestMode(false);
        Intents.release();
    }

    @Test
    public void testProfileFragmentUI() {
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.et_Email)).check(matches(isDisplayed()));
        onView(withId(R.id.et_password)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testLogin_validFields() {

        AuthManager.setLoginProvider((email, password, callback) -> {
            callback.onComplete(true, null);
        });

        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.et_Email)).perform(replaceText("test@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(replaceText("123456"), closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());

        intended(hasComponent(MainActivity.class.getName()));

    }
    @Test
    public void testLogin_emptyFields() {

        AuthManager.setLoginProvider((email, password, callback) -> {
            callback.onComplete(false, null);
        });

        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.et_Email)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(replaceText(""), closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());

        intended(not(hasComponent(MainActivity.class.getName())));

    }

}
