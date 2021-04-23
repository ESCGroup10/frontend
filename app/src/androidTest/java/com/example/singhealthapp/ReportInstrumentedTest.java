package com.example.singhealthapp;

import android.text.InputType;
import android.view.Gravity;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.singhealthapp.Containers.AuditorFragmentContainer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.singhealthapp.HelperClasses.StandardHelperMethods.clickChildViewWithId;
import static com.example.singhealthapp.HelperClasses.StandardHelperMethods.sleep;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Precondition: Log in as auditor
 * */

@RunWith(AndroidJUnit4ClassRunner.class)
public class ReportInstrumentedTest {
    @Rule
    public ActivityScenarioRule<AuditorFragmentContainer> activityRule =
            new ActivityScenarioRule<>(AuditorFragmentContainer.class);

    @Before
    public void init() throws InterruptedException {
        onView(withId(R.id.auditor_main_page)).check(matches(isDisplayed()));
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_Reports)).perform(click());
        sleep(2);
    }

    @Test
    public void TestViewReport() {
        onView(withId(R.id.reportPreviewUnresolvedButton)).check(matches(isDisplayed()));
    }

    @Test
    public void TestHideReport() {
        sleep(2);
        onView(withId(R.id.reportPreviewUnresolvedButton)).perform(click());
        try {
            onView(withId(R.id.reportPreviewRecyclerViewUnresolved)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.card_report)));
        } catch (PerformException e) {
            // pass
        }

    }

    @Test
    public void TestSearchReport() {
        onView(withId(R.id.reportPreviewRecyclerViewUnresolved)).check(matches(isDisplayed()));
        onView(withInputType(InputType.TYPE_CLASS_TEXT)).perform(typeText("2020-03-22"));
        onView(withId(R.id.reportPreviewSearchButton)).perform(click());
        onView(withId(R.id.reportPreviewRecyclerViewUnresolved)).check(matches(not(isDisplayed())));

    }

    @Test
    public void TestClickTest() throws InterruptedException {
        onView(withId(R.id.reportPreviewRecyclerViewUnresolved)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.card_report)));
        onView(withId(R.id.summaryReportScrollView)).check(matches(isDisplayed()));
    }
}
