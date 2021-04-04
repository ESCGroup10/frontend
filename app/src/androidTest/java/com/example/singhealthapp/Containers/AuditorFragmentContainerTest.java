package com.example.singhealthapp.Containers;

import android.view.Gravity;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.R;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuditorFragmentContainerTest {

    private IdlingResource mIdlingResource;

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

//    @Rule
//    public ActivityScenarioRule<AuditorFragmentContainer> activityRule
//            = new ActivityScenarioRule<>(AuditorFragmentContainer.class);

    public void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("caught "+e);
        }
    }

    @Before
    public void setUp() {
        // register idling resources here
        ActivityScenario activityScenario = ActivityScenario.launch(AuditorFragmentContainer.class);
        IdlingRegistry.getInstance().register(EspressoCountingIdlingResource.getIdlingResource());
        /**
         * Example for calling method in an Activity:
         *
         * METHOD MUST HAVE @VisibleForTesting TAG
         * activityScenario.onActivity(new ActivityScenario.ActivityAction<AuditorFragmentContainer>() {
         *             @Override
         *             public void perform(AuditorFragmentContainer activity) {
         *                 mIdlingResource = activity.getIdlingResource();
         *                 IdlingRegistry.getInstance().register(mIdlingResource);
         *             }
         *         });
         * */
    }

    @After
    public void tearDown() {
        // unregister idling resources here
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

    @Test
    public void NavToTenantStatisticsFragment() {
        // tenant search fragment should be the first fragment displayed
        // we can go to tenant search fragment by pressing the Tenants button in the drawer
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_Auditor_Statistics)).perform(click());
        onView(withId(R.id.fragment_statistics)).check(matches(isDisplayed()));
    }

    @Test
    public void NavToTenantExpandedFragment() {
        // tenant search fragment should be the first fragment displayed
        // we can go to tenant search fragment by pressing the Tenants button in the drawer
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_Tenants)).perform(click());

        onView(withId(R.id.tenantSearchFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.tenantRecycler)).check(matches(isDisplayed()));
    }

    @Test
    public void NavToReportsFragment() {
        // tenant search fragment should be the first fragment displayed
        // we can go to tenant search fragment by pressing the Tenants button in the drawer
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_Reports)).perform(click());

        onView(withId(R.id.reports)).check(matches(isDisplayed()));
        onView(withId(R.id.reportPreviewRecyclerViewUnresolved)).check(matches(isDisplayed()));
        onView(withId(R.id.reportPreviewRecyclerView)).check(matches(not(isDisplayed())));
    }

    @Test
    public void NavToAddTenantFragment() {
        // tenant search fragment should be the first fragment displayed
        // we can go to tenant search fragment by pressing the Tenants button in the drawer
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_Add_Tenant)).perform(click());

        onView(withId(16909135)).check(matches(isDisplayed())); //alert dialogue layout
    }

//    @Test
//    public void NavToSafetyChecklist() {
//        // we can click on a recyclerView item to go to an expanded view of the Tenant
//        onView(withId(R.id.tenantRecycler)).perform(
//                RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.cardView)));
//        sleep();
//        onView(withId(R.id.expandedTenantFragment)).check(matches(isDisplayed()));
//
//        // we can click a button to start the safety checklist
//        onView(withId(R.id.startSafetyChecklistButton)).perform(click());
//        sleep();
//        onView(withId(R.id.safetyChecklistFragment)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void NavToAuditChecklist() {
//        // we can click on a button to go to the audit checklist
//        onView(withId(R.id.safetyChecklistFragment)).perform(swipeUp());
//        onView(withId(R.id.safetyChecklistFragment)).perform(swipeUp());
//        sleep();
//        sleep();
//        onView(withId(R.id.start_audit_button)).perform(click());
//        sleep();
//        onView(withId(R.id.auditChecklistFragment)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void NavToStatusConfirmationChecklist() {
//        // we can go to status confirmation page
//        scrollToBottom();
//        onView(withId(R.id.submit_audit_button)).perform(click());
//        sleep();
//        onView(withText("Yes")).perform(click());
//        sleep();
//        onView(withId(R.id.statusConfirmationFragment)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void NavToReportFragment() {
//        // we can go to the report fragment
//        onView(withId(R.id.button_return)).perform(click());
//        sleep();
//        onView(withId(R.id.casePreviewScrollView)).check(matches(isDisplayed()));
//    }




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