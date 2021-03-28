package com.example.singhealthapp;

import android.text.InputType;
import android.view.Gravity;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.DrawerActions;
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
import static org.hamcrest.core.StringContains.containsString;

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
        Thread.sleep(2000);
    }

    @Test
    public void TestViewReport() {
        onView(withText("Report 4")).check(matches(isDisplayed()));
    }

    @Test
    public void TestHideReport() {
        onView(withId(R.id.reportPreviewUnresolvedButton)).perform(click());
        onView(withText("Report 4")).check(doesNotExist());
        onView(withText("Report 6")).check(matches(isDisplayed()));

    }

    @Test
    public void TestSearchReport() {
        onView(withInputType(InputType.TYPE_CLASS_TEXT)).perform(typeText("2021-03-22 08:37:44"));
        onView(withId(R.id.reportPreviewSearchButton)).perform(click());
        onView(withText("Report 4")).check(matches(isDisplayed()));
        onView(withText("Report 6")).check(doesNotExist());

    }

    @Test
    public void TestClickTest() throws InterruptedException {
        onView(withText("Report 4")).perform(click());
        onView(withId(R.id.auditorReportViewCases)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.auditorReportResolved)).check(matches(withText(containsString("2"))));
    }
}
