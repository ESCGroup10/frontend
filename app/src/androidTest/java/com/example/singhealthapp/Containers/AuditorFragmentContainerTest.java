package com.example.singhealthapp.Containers;

import android.view.Gravity;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.singhealthapp.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class AuditorFragmentContainerTest {

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    @Rule
    public ActivityScenarioRule<AuditorFragmentContainer> activityRule
            = new ActivityScenarioRule<>(AuditorFragmentContainer.class);

    @Test
    public void NavTenantsTest() {
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_Tenants)).perform(click());

        onView(withId(R.id.tenantSearchFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.tenantRecycler)).check(matches(isDisplayed()));
        onView(withId(R.id.tenantRecycler)).perform(
        RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.cardView)));

        onView(withId(R.id.expandedTenantFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.startSafetyChecklistButton)).perform(click());

        onView(withId(R.id.safetyChecklistFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.start_audit_button)).perform(click());


    }


}