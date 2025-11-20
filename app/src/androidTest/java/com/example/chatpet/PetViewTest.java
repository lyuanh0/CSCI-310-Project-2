package com.example.chatpet;

import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.widget.TextView;

import com.example.chatpet.ui.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class PetViewTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule =
            new ActivityScenarioRule<>(MainActivity.class);

    private void pause(long ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }

    public void login(String email, String pass) {
        onView(withId(R.id.et_Email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());

    }

    //pick pet only appears on first login
    public void pickPet() {
        try {
            onView(withText("Choose Your Pet"))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()));

            onView(withText("Dog")).inRoot(isDialog()).perform(click());

            onView(isAssignableFrom(EditText.class))
                    .perform(typeText("Buddy"), closeSoftKeyboard());

            onView(withId(android.R.id.button1)) // "Create"
                    .perform(click());

        } catch (NoMatchingRootException ignore) {}
    }

   //open pet tab
    public void openPetTab() {
        onView(withId(R.id.nav_pet)).perform(click());
    }

    //testing feeding increases hunger
    @Test
    public void testFeedingIncreasesHunger() {
        login("testpet@usc.edu", "testpet");
        pickPet();
        openPetTab();

        //read hunger before feeding
        final int[] beforeHunger = new int[1];

        onView(withId(R.id.tv_hunger_value)).check((view, noViewFound) -> {
            String t = ((android.widget.TextView) view)
                    .getText().toString().replace("%", "").trim();
            beforeHunger[0] = Integer.parseInt(t);
        });

        //press feed and then it opens alert dialog
        onView(withId(R.id.btn_feed)).perform(click());

        //Food menu appears — click “Pizza”
        onView(withText(containsString("Pizza"))).perform(click());

        //read hunger after feeding
        onView(withId(R.id.tv_hunger_value)).check((view, noViewFound) -> {
            String t = ((android.widget.TextView) view)
                    .getText().toString().replace("%", "").trim();
            int afterHunger = Integer.parseInt(t);

            //assert hunger increased
            assert(afterHunger > beforeHunger[0]);
        });
    }

    @Test
    public void testTuckInIncreasesHappiness() {

        login("testpet1@usc.edu", "testpet1");
        pickPet();
        openPetTab();

        // Get current Happiness %
        final int[] before = new int[1];
        onView(withId(R.id.tv_happiness_value)).check((view, error) -> {
            String text = ((TextView) view).getText().toString().replace("%", "");
            before[0] = Integer.parseInt(text);
        });

        // Click Tuck In
        onView(withId(R.id.btn_tuck_in)).perform(click());

        // Check updated Happiness %
        onView(withId(R.id.tv_happiness_value)).check((view, error) -> {
            int after = Integer.parseInt(
                    ((TextView) view).getText().toString().replace("%", "")
            );
            assert(after > before[0]);
        });
    }

    //Tuck in increases energy
    @Test
    public void testTuckInIncreasesEnergy() {

        login("testpet2@usc.edu", "testpet2");
        pickPet();
        openPetTab();

        // Get current Energy %
        final int[] before = new int[1];
        onView(withId(R.id.tv_energy_value)).check((view, error) -> {
            String text = ((TextView) view).getText().toString().replace("%", "");
            before[0] = Integer.parseInt(text);
        });

        // Click Tuck In
        onView(withId(R.id.btn_tuck_in)).perform(click());

        // Check updated Energy %
        onView(withId(R.id.tv_energy_value)).check((view, error) -> {
            int after = Integer.parseInt(
                    ((TextView) view).getText().toString().replace("%", "")
            );
            assert(after > before[0]);
        });
    }

    @Test
    public void testFeedButtonDisabledWhenHungerFull() {
        login("testpet3@usc.edu", "testpet3");
        pickPet();
        openPetTab();

        // Feed multiple times until hunger reaches 100
        for (int i = 0; i < 6; i++) {
            // Check hunger percentage
            final int[] hunger = new int[1];
            onView(withId(R.id.tv_hunger_value)).check((view, error) -> {
                String text = ((TextView) view).getText().toString().replace("%", "");
                hunger[0] = Integer.parseInt(text);
            });

            if (hunger[0] >= 100) {
                break; // hunger full
            }

            // Feed interaction with pauses for visual verification
            onView(withId(R.id.btn_feed)).check(matches(isEnabled()));
            onView(withId(R.id.btn_feed)).perform(click());
            pause(1000); // Wait for dialog to appear

            onView(withText(containsString("Pizza"))).perform(click());
            pause(1500); // Wait for hunger to update and dialog to close
        }

        pause(1000); // Wait before final hunger read

        // After feeding, hunger should now be 100% — feed button should be disabled
        final int[] finalHunger = new int[1];
        onView(withId(R.id.tv_hunger_value)).check((view, error) -> {
            String text = ((TextView) view).getText().toString().replace("%", "");
            finalHunger[0] = Integer.parseInt(text);
        });

        assert(finalHunger[0] == 100);

        // Final UI pause before checking feed button
        pause(500);
        onView(withId(R.id.btn_feed)).check(matches(not(isEnabled())));
    }

    @Test
    public void testFeedButtonDisabledWhenPetSleeping() {
        login("testpet3@usc.edu", "testpet3");
        pickPet();
        openPetTab();

        //Click Tuck In
        onView(withId(R.id.btn_tuck_in)).perform(click());

        //Verify pet status text says "sleeping"
        onView(withId(R.id.tv_pet_status))
                .check(matches(withText(containsString("sleeping"))));

        //Verify feed button is now disabled
        onView(withId(R.id.btn_feed)).check(matches(not(isEnabled())));
    }

}
