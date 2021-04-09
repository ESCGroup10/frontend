package com.example.singhealthapp;

import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.singhealthapp.Containers.AuditorFragmentContainer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

public class StatisticsTest {
    @Rule
    public ActivityScenarioRule<AuditorFragmentContainer> activityRule =
            new ActivityScenarioRule<>(AuditorFragmentContainer.class);

    @Before
    public void init() throws InterruptedException {
        onView(withId(R.id.auditor_main_page)).check(matches(isDisplayed()));
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_Auditor_Statistics)).perform(click());
        Thread.sleep(3000);
    }

    @Test
    public void TestEmptyFieldInput() {
        onView(withId(R.id.searchTenantId_button)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.chartLayout)).perform(ViewActions.swipeUp());
        onView(withId(R.id.exportcases_button)).check(matches(not(isEnabled())));
    }

    @Test
    public void TestInvalidReportInput() throws InterruptedException {
        onView(withId(R.id.tenantId_edittext)).perform(typeText("$@vfehr29   ")).perform(closeSoftKeyboard());
        onView(withId(R.id.searchTenantId_button)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(ViewMatchers.withId(R.id.chartLayout)).perform(ViewActions.swipeUp());
        onView(withId(R.id.exportcases_button)).check(matches(not(isEnabled())));
    }

    @Test
    public void TestInvalidTotalInput() throws InterruptedException {
        onView(withId(R.id.tenantId_edittext)).perform(typeText("$@vfehr29   ")).perform(closeSoftKeyboard());
        onView(withId(R.id.searchTenantId_button)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(ViewMatchers.withId(R.id.chartLayout)).perform(ViewActions.swipeUp());
        onView(withId(R.id.exportcases_button)).check(matches(not(isEnabled())));
    }

    @Test
    public void TestValidReportsInput() throws InterruptedException {
        onView(withId(R.id.tenantId_edittext)).perform(typeText("28")).perform(closeSoftKeyboard());
        onView(withId(R.id.searchTenantId_button)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(ViewMatchers.withId(R.id.chartLayout)).perform(ViewActions.swipeUp());
        onView(withId(R.id.exportcases_button)).perform(click());
    }

    @Test
    public void TestValidTotalInput() throws InterruptedException {
        onView(withId(R.id.tenantId_edittext)).perform(typeText("28")).perform(closeSoftKeyboard());
        onView(withId(R.id.searchTenantId_button)).perform(click());

        Thread.sleep(3000);

        onView(withId(R.id.stats_viewPager)).perform(swipeLeft());
        Espresso.onView(ViewMatchers.withId(R.id.chartLayout)).perform(ViewActions.swipeUp());

        onView(withId(R.id.exportscore_button)).perform(click());
    }
}
