package com.example.singhealthapp;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.singhealthapp.Container.AuditorFragmentContainer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CasePreviewInstrumentedTest {
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
        Thread.sleep(2000);
        onView(withText("Report 4")).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.casePreviewScrollView)).perform(swipeUp());
        onView(withId(R.id.auditorReportViewCases))
                .perform(click());
    }

    @Test
    public void TestViewCase() {
        onView(withText("Case 11")).check(matches(isDisplayed()));
    }
}
