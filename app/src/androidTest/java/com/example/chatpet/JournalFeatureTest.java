package com.example.chatpet;

import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.matcher.BoundedMatcher;

import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.allOf;

import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.ui.MainActivity;


@RunWith(AndroidJUnit4.class)
public class JournalFeatureTest {
    // Launches MainActivity before each test
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        System.setProperty("IS_TEST_ENV", "true");
    }

    // Helper Functions
    // Checking items inside a RecyclerView at a specific position
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item at position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    // Checks the number of items in a RecyclerView.
    public static Matcher<View> withItemCount(final int count) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                // Checks adapter's getItemCount()
                return recyclerView.getAdapter() != null &&
                        recyclerView.getAdapter().getItemCount() == count;
            }

            // Error message when test fail
            @Override
            public void describeTo(Description description) {
                description.appendText("has item count: " + count);
            }
        };
    }

    // Starting pet choice popup
    public void pickPet() {
        try {
            // Check if the pet selection dialog is displayed
            onView(withText("Choose Your Pet"))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()));

            onView(withText("Dog"))
                    .inRoot(isDialog())
                    .perform(click());
            onView(isAssignableFrom(EditText.class))
                    .perform(typeText("Buddy"), closeSoftKeyboard());
            onView(withId(android.R.id.button1))  // Create
                    .perform(click());

        } catch (NoMatchingRootException e) {
            // Dialog not present (existing account), skip
            //System.out.println("Pet selection dialog not shown — skipping.");
        }
    }

    // Login
    public void login(String E, String P) {
        onView(withId(R.id.et_Email))
                .perform(typeText(E), closeSoftKeyboard());
        onView(withId(R.id.et_password))
                .perform(typeText(P), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());    // login button
    }

    @After
    public void logout() {
        try {
            onView(withId(R.id.nav_profile)).perform(click());

            // Scroll to logout and click
            onView(withId(R.id.btn_logout)).perform(scrollTo(), click());
        } catch (NoMatchingViewException | PerformException e) {
            // Skip if already logged out or view not visible
            //System.out.println("Logout skipped — not needed");
        }
    }


    // Test navigation to journal that should be initially empty
        // New account initially have empty journal
    @Test
    public void testJournalNavigation() {
        login("testNew@usc.edu", "testNew");
        pickPet();

        onView(withId(R.id.nav_journal)).perform(click());
        onView(withId(R.id.rv_journal)).check(matches(isDisplayed()));
        // Check empty when new account
        onView(withId(R.id.rv_journal)).check(matches(withItemCount(0)));
    }

    // Test navigation to and away from journal
    @Test
    public void testNavigationJournalAway() {
        login("testNew@usc.edu", "testNew");
        pickPet();

        onView(withId(R.id.nav_journal)).perform(click());
        onView(withId(R.id.rv_journal)).check(matches(isDisplayed()));

        // Out of journal page
        onView(withId(R.id.nav_pet)).perform(click());
        onView(withId(R.id.iv_pet)).check(matches(isDisplayed()));

        // Back to journal page
        onView(withId(R.id.nav_journal)).perform(click());
        onView(withId(R.id.rv_journal)).check(matches(isDisplayed()));
    }

    // Test journal entries sorted, latest date at top
        // 3 entries (nov 30th, 26th, 19th, 2025)
    @Test
    public void testJournalEntryContent() {
        login("journalTest@usc.edu", "journal");
        pickPet();

        onView(withId(R.id.nav_journal)).perform(click());

        // First entry at top is latest
        onView(withId(R.id.rv_journal))
                .check(matches(atPosition(0, hasDescendant(withText("Nov 30, 2025")))));

        // Last entry at bottom is oldest
        onView(withId(R.id.rv_journal))
                .check(matches(atPosition(0, hasDescendant(withText("Nov 30, 2025")))))
                .check(matches(atPosition(2, hasDescendant(withText("Nov 19, 2025")))));


    }

    // Test search bar
        // 3 entries (nov 30th, 26th, 19th, 2025)
    @Test
    public void testSearchBar() {
        login("journalTest@usc.edu", "journal");
        pickPet();

        onView(withId(R.id.nav_journal)).perform(click());

        // Search up entry with "26" keyword
        onView(withId(R.id.searchView)).perform(click());
        onView(allOf(
                // TARGET THE TEXT FIELD by its class (SearchAutoComplete)
                isAssignableFrom(androidx.appcompat.widget.SearchView.SearchAutoComplete.class),

                // AND check that it belongs to *your* R.id.searchView container
                isDescendantOfA(withId(R.id.searchView))
        ))
                .check(matches(isDisplayed()))
                .perform(typeText("26"), closeSoftKeyboard());

        // Matching entry is visible
        onView(withText("Nov 26, 2025")).check(matches(isDisplayed()));
        onView(withId(R.id.rv_journal)).check(matches(withItemCount(1)));

        // Non-matching entry is not visible
        onView(withText("Nov 19, 2025")).check(doesNotExist());

        // Clear search text
        onView(withId(R.id.searchView)).perform(clearText(), closeSoftKeyboard());

        // Check all entries reappear
        onView(withId(R.id.rv_journal)).check(matches(withItemCount(3)));
    }

    // Test journal entry content viewability being expandable and collapsable
        // 3 entries (nov 30th, 26th, 19th, 2025)
    @Test
    public void testEntryContentView() {
        login("journalTest@usc.edu", "journal");
        pickPet();

        onView(withId(R.id.nav_journal)).perform(click());

        // Click first entry
        onView(withId(R.id.rv_journal))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check entry is still displayed
        onView(withId(R.id.rv_journal))
                .check(matches(atPosition(0, isDisplayed())));

        // Click again to collapse
        onView(withId(R.id.rv_journal))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check still displayed
        onView(withId(R.id.rv_journal))
                .check(matches(atPosition(0, isDisplayed())));
    }

}