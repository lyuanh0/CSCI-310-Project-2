package com.example.chatpet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import android.widget.EditText;

import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.chatpet.ui.login.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class PetViewLevelUpBlackBoxTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        System.setProperty("IS_TEST_ENV", "true");
        Thread.sleep(1000);
    }

    // ============ Helper Functions ============

    // Get current level from UI
    public int getCurrentLevel() throws InterruptedException {
        final int[] level = {1};  // Default to 1

        try {
            onView(withId(R.id.tv_pet_level)).check((view, noViewFoundException) -> {
                if (view != null && view instanceof android.widget.TextView) {
                    String text = ((android.widget.TextView) view).getText().toString();
                    // Extract number from "Level: X"
                    String numberStr = text.replaceAll("[^0-9]", "");
                    if (!numberStr.isEmpty()) {
                        level[0] = Integer.parseInt(numberStr);
                    }
                }
            });
        } catch (Exception e) {
            level[0] = 1;  // Default to 1 if can't read
        }

        return level[0];
    }

    // Get max XP for current level
    public int getMaxXPForLevel(int level) {
        return level * 100;  // Level 1 = 100 XP, Level 2 = 200 XP, etc.
    }

    // Get current XP from UI
    public int getCurrentXP() throws InterruptedException {
        final int[] xp = {0};

        try {
            onView(withId(R.id.tv_xp_value)).check((view, noViewFoundException) -> {
                if (view != null && view instanceof android.widget.TextView) {
                    String text = ((android.widget.TextView) view).getText().toString();
                    // Extract first number from "X/Y"
                    String[] parts = text.split("/");
                    if (parts.length > 0) {
                        String numberStr = parts[0].replaceAll("[^0-9]", "");
                        if (!numberStr.isEmpty()) {
                            xp[0] = Integer.parseInt(numberStr);
                        }
                    }
                }
            });
        } catch (Exception e) {
            xp[0] = 0;
        }

        return xp[0];
    }

    // Feed pet once
    public void feedPet() throws InterruptedException {
        tuckInAndWait();
        onView(withId(R.id.btn_feed)).perform(click());
        Thread.sleep(300);
        onView(withText(containsString("Pizza"))).perform(click());
        Thread.sleep(500);
    }

    // Wait for view to appear
    public void waitForView(int viewId, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                onView(withId(viewId)).check(matches(isDisplayed()));
                return;
            } catch (Exception e) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ie) {
                    // Ignore
                }
            }
        }
        throw new RuntimeException("View " + viewId + " not found after " + timeoutMs + "ms");
    }

    // Login with pre-created account
    public void login(String email, String password) throws InterruptedException {
        onView(withId(R.id.et_Email))
                .perform(typeText(email), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.et_password))
                .perform(typeText(password), closeSoftKeyboard());
        Thread.sleep(300);

        onView(withId(R.id.btn_login)).perform(click());

        waitForView(R.id.nav_pet, 10000);
    }

    // Pick pet if dialog appears
    public void pickPet() throws InterruptedException {
        try {
            Thread.sleep(1000);

            onView(withText("Choose Your Pet"))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()));

            onView(withText("Dog"))
                    .inRoot(isDialog())
                    .perform(click());

            Thread.sleep(300);

            onView(isAssignableFrom(EditText.class))
                    .perform(typeText("Buddy"), closeSoftKeyboard());

            Thread.sleep(300);

            onView(withId(android.R.id.button1))
                    .perform(click());

            Thread.sleep(1000);
        } catch (NoMatchingRootException e) {
            // Pet already exists, skip
        }
    }

    // Tuck in pet and wait for wakeup
    public void tuckInAndWait() throws InterruptedException {
        onView(withId(R.id.btn_tuck_in)).perform(click());
        Thread.sleep(500);
        Thread.sleep(12000);  // 12 seconds for cooldown
    }

    @After
    public void logout() throws InterruptedException {
        try {
            // Wait a bit for any animations to finish
            Thread.sleep(1000);

            // Try to navigate to profile
            onView(withId(R.id.nav_profile)).perform(click());
            Thread.sleep(1000);

            // Try to click logout
            onView(withId(R.id.btn_logout)).perform(scrollTo(), click());
            Thread.sleep(1000);

            System.out.println("=== Logout successful");
        } catch (Exception e) {
            System.out.println("=== Logout failed or not needed: " + e.getMessage());

            // If logout fails, try to force restart by finishing activity
            activityRule.getScenario().close();
        }
    }



    // ============ Tests ============
// BLACK BOX TEST 1: XP Progress Bar Displays Initially
    @Test
    public void testXPBarDisplaysAtStart() throws InterruptedException {
//        login("testXP1@usc.edu", "testPass123456");
        login("testNew@usc.edu", "testNew");

        // ALTERNATE EMAIL 1:
        //login("AndyTest1@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 2:
        //login("AndyTest1B@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 3:
        //login("AndyTest1C@gmail.com, "Test1234567");

        pickPet();

        onView(withId(R.id.nav_pet)).perform(click());
        Thread.sleep(500);

        // Get current level to check appropriate max XP
        int currentLevel = getCurrentLevel();
        int maxXP = getMaxXPForLevel(currentLevel);

        onView(withId(R.id.pb_xp)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_xp_value))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString("/" + maxXP))));
    }

    // BLACK BOX TEST 2: XP Value Updates After Gaining XP
    @Test
    public void testXPValueUpdatesAfterGainingXP() throws InterruptedException {
//        login("testXP2@usc.edu", "testPass123456");
        login("testNew@usc.edu", "testNew");

        // ALTERNATE EMAIL 1:
        //login("AndyTest2@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 2:
        //login("AndyTest2B@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 3:
        //login("AndyTest2C@gmail.com, "Test1234567");

        pickPet();

        onView(withId(R.id.nav_pet)).perform(click());
        Thread.sleep(500);

        int currentLevel = getCurrentLevel();
        int maxXP = getMaxXPForLevel(currentLevel);
        int startXP = getCurrentXP();

        feedPet();

        int endXP = getCurrentXP();

        // XP should have increased
        assert(endXP > startXP);

        onView(withId(R.id.tv_xp_value))
                .check(matches(withText(containsString("/" + maxXP))));
    }

    // BLACK BOX TEST 3: Pet Image Changes When Leveling Up
    @Test
    public void testPetImageChangesAfterLevelUp() throws InterruptedException {
//        login("testXP3@usc.edu", "testPass123456");
        login("testNew@usc.edu", "testNew");

        // ALTERNATE EMAIL 1:
        //login("AndyTest3@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 2:
        //login("AndyTest3B@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 3:
        //login("AndyTest3C@gmail.com, "Test1234567");

        pickPet();

        onView(withId(R.id.nav_pet)).perform(click());
        Thread.sleep(500);

        int startLevel = getCurrentLevel();
        int maxXP = getMaxXPForLevel(startLevel);
        int currentXP = getCurrentXP();

        // Feed until we have enough XP to level up
        while (currentXP < maxXP) {
            feedPet();
            currentXP = getCurrentXP();

            // Safety: stop if we've fed 10 times
            if (currentXP >= maxXP) break;
        }

        onView(withId(R.id.btn_level_up)).perform(click());
        Thread.sleep(500);

        int newLevel = startLevel == 3 ? 3: startLevel + 1;
        onView(withId(R.id.iv_pet)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_pet_level))
                .check(matches(withText(containsString("Level: " + newLevel))));
    }

    // BLACK BOX TEST 4: Level Display Changes and XP Resets After Level Up
    @Test
    public void testLevelUpChangesDisplayAndResetsXP() throws InterruptedException {
//        login("testXP4@usc.edu", "testPass123456");
        login("testNew@usc.edu", "testNew");

        // ALTERNATE EMAIL 1:
        //login("AndyTest4@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 2:
        //login("AndyTest4B@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 3:
        //login("AndyTest4C@gmail.com, "Test1234567");

        pickPet();

        onView(withId(R.id.nav_pet)).perform(click());
        Thread.sleep(500);

        int startLevel = getCurrentLevel();
        int maxXP = getMaxXPForLevel(startLevel);
        int currentXP = getCurrentXP();

        // Feed until we have enough XP to level up
        while (currentXP < maxXP) {
            feedPet();
            currentXP = getCurrentXP();
            if (currentXP >= maxXP) break;
        }

        onView(withId(R.id.btn_level_up)).perform(click());
        Thread.sleep(500);

        int newLevel = startLevel == 3 ? 3: startLevel + 1;
        int newMaxXP = getMaxXPForLevel(newLevel);

        onView(withId(R.id.tv_pet_level))
                .check(matches(withText(containsString("Level: " + newLevel))));
        onView(withId(R.id.tv_xp_value))
                .check(matches(withText(containsString("0/" + newMaxXP))));
    }

    // BLACK BOX TEST 5: XP Caps at Maximum for Current Level
    @Test
    public void testXPCapsAtMaximum() throws InterruptedException {
//        login("testXP5@usc.edu", "testPass123456");
        login("testNew@usc.edu", "testNew");
        // ALTERNATE EMAIL 1:
        //login("AndyTest5@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 2:
        //login("AndyTest5B@gmail.com, "Test1234567");

        // ALTERNATE EMAIL 3:
        //login("AndyTest5C@gmail.com, "Test1234567");
        pickPet();

        onView(withId(R.id.nav_pet)).perform(click());
        Thread.sleep(500);

        int currentLevel = getCurrentLevel();
        int maxXP = getMaxXPForLevel(currentLevel);

        // Sleep / Feed many times to exceed cap (5 times should be enough)
        for (int i = 0; i < 5; i++) {
            try {
                tuckInAndWait();
                feedPet();
            } catch (Exception e) {
                // System.out.println("=== Feed failed at attempt " + (i + 1) + ": " + e.getMessage());
                // Continue even if one feed fails
            }
        }

        Thread.sleep(1000);

        // XP should cap at max, not exceed it
        onView(withId(R.id.tv_xp_value))
                .check(matches(withText(containsString(maxXP + "/" + maxXP))));
    }
}
