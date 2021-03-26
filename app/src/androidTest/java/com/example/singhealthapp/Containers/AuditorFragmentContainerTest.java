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
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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

    public void sleep() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.out.println("caught "+e);
        }
    }

    @Test
    public void NavMakingAuditTest() {
        // tenant search fragment should be the first fragment displayed
//        onView(withId(R.id.tenantSearchFragment)).check(matches(isDisplayed()));
//        onView(withId(R.id.tenantRecycler)).check(matches(isDisplayed()));

        // we can go to tenant search fragment by pressing the Tenants button in the drawer
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep();
        onView(withId(R.id.nav_Tenants)).perform(click());
        sleep();
        onView(withId(R.id.tenantSearchFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.tenantRecycler)).check(matches(isDisplayed()));
        sleep();

        // we can click on a recyclerView item to go to an expanded view of the Tenant
        onView(withId(R.id.tenantRecycler)).perform(
        RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.cardView)));
        sleep();
        onView(withId(R.id.expandedTenantFragment)).check(matches(isDisplayed()));

        // we can click a button to start the safety checklist
        onView(withId(R.id.startSafetyChecklistButton)).perform(click());
        sleep();
        onView(withId(R.id.safetyChecklistFragment)).check(matches(isDisplayed()));

        // we can click on a button to go to the audit checklist
        onView(withId(R.id.safetyChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.safetyChecklistFragment)).perform(swipeUp());
        sleep();
        sleep();
        onView(withId(R.id.start_audit_button)).perform(click());
        sleep();
        onView(withId(R.id.auditChecklistFragment)).check(matches(isDisplayed()));

        // we can go to status confirmation page
        scrollToBottom();
        onView(withId(R.id.submit_audit_button)).perform(click());
        sleep();
        onView(withText("Yes")).perform(click());
        sleep();
        onView(withId(R.id.statusConfirmationFragment)).check(matches(isDisplayed()));

        // we can go to the report fragment
        onView(withId(R.id.button_return)).perform(click());
        sleep();
        onView(withId(R.id.auditor_fragment_container)).check(matches(isDisplayed()));
    }




    public void scrollToBottom() {
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        sleep();
        sleep();
        sleep();
        sleep();
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        sleep();
        sleep();
        sleep();
        sleep();
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        sleep();
        sleep();
        sleep();
        sleep();
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        sleep();
        sleep();
        sleep();
        sleep();
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        sleep();
        sleep();
        sleep();
        sleep();
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        sleep();
        sleep();
        sleep();onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        sleep();
        sleep();
    }



}