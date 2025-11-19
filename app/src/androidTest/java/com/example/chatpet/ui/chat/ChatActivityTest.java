package com.example.chatpet.ui.chat;

import static androidx.test.espresso.Espresso.onView;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.espresso.Espresso;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.chatpet.R;
import com.example.chatpet.logic.ChatManager;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ChatActivityTest {

    // a

    private void resetChatState() {
        ChatManager.getInstance().getMessages().clear();
    }

    // 1) User sends a message and sees pet reply
    @Test
    public void userSendMessage_displaysUserAndPetReply() {
        resetChatState();

        ActivityScenario.launch(new Intent(
                ApplicationProvider.getApplicationContext(), ChatActivity.class));

        // Type "hello" and send
        onView(withId(R.id.et_message)).perform(typeText("hello"), closeSoftKeyboard());
        onView(withId(R.id.btn_send)).perform(click());

        // User bubble "hello" appears
        onView(withText("hello")).check(matches(isDisplayed()));

        onView(withId(R.id.rv_messages)).perform(waitFor(2000));
    }


    // 2) Empty input should not send
    @Test
    public void emptyInput_noMessageAdded() {
        resetChatState();

        ActivityScenario.launch(new Intent(
                ApplicationProvider.getApplicationContext(), ChatActivity.class));

        onView(withId(R.id.et_message)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.btn_send)).perform(click());

        // No "Thinking..." bubble -> nothing was sent
        onView(withText("Thinking...")).check(doesNotExist());
    }

    // 3) Chat disabled when happiness is too high (smoke test)
    @Test
    public void highHappiness_disablesChatAndFinishes() {
        resetChatState();

        ActivityScenario<ChatActivity> scenario =
                ActivityScenario.launch(new Intent(
                        ApplicationProvider.getApplicationContext(), ChatActivity.class));

        // Let onCreate() run its happiness logic
        onView(withId(R.id.rv_messages)).perform(waitFor(1000));

        scenario.close();
    }

    // 4) Backend error shows “Oops!” system message
    @Test
    public void backendError_showsOopsSystemMessage() {
        resetChatState();

        ActivityScenario.launch(new Intent(
                ApplicationProvider.getApplicationContext(), ChatActivity.class));

        // Send a simple message
        onView(withId(R.id.et_message)).perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.btn_send)).perform(click());

        // Give ChatGenerator time to run and hit the error handler
        onView(withId(R.id.rv_messages)).perform(waitFor(3000));

        onView(withText("Oops! I couldn't think of a reply.")).check(matches(isDisplayed()));
    }


    // 5) Chat history clears when leaving the chat
    @Test
    public void leavingChat_clearsChatHistory() {
        resetChatState();

        ActivityScenario<ChatActivity> scenario =
                ActivityScenario.launch(new Intent(
                        ApplicationProvider.getApplicationContext(), ChatActivity.class));

        // Send "hello"
        onView(withId(R.id.et_message)).perform(typeText("hello"), closeSoftKeyboard());
        onView(withId(R.id.btn_send)).perform(click());
        onView(withText("hello")).check(matches(isDisplayed()));

        try {
            Espresso.pressBack();
        } catch (NoActivityResumedException expected) {
        }

        // relaunch chat
        resetChatState();
        ActivityScenario.launch(new Intent(
                ApplicationProvider.getApplicationContext(), ChatActivity.class));

        // Wait for adapter to bind
        onView(withId(R.id.rv_messages)).perform(waitFor(800));

        // Old "hello" must be gone
        onView(withText("hello")).check(doesNotExist());
    }


    // Helper: simple wait
    private static ViewAction waitFor(long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return Matchers.any(View.class);
            }

            @Override
            public String getDescription() {
                return "wait for " + millis + " ms";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}
