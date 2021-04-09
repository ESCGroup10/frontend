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
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.R;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuditorFragmentContainerTest {
    EspressoCountingIdlingResource idlingResource;

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

    public void sleep(int multiplier) {
        try {
            Thread.sleep(100*multiplier);
        } catch (InterruptedException e) {
            System.out.println("caught "+e);
        }
    }

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

    @Before
    public void setUp() {
        // register idling resources here
        ActivityScenario activityScenario = ActivityScenario.launch(AuditorFragmentContainer.class);
        IdlingRegistry.getInstance().register(EspressoCountingIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() {
        // unregister idling resources here if any
        IdlingRegistry.getInstance().unregister(EspressoCountingIdlingResource.getIdlingResource());
    }

    @Test
    public void NavAuditorContainerToTenantStatisticsFragment() {
        // tenant search fragment should be the first fragment displayed
        // we can go to tenant search fragment by pressing the Tenants button in the drawer
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(2); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_Auditor_Statistics)).perform(click());
        onView(withId(R.id.fragment_statistics)).check(matches(isDisplayed()));
    }

    @Test
    public void NavAuditorContainerToTenantSearchFragment() {
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(2); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.tenantSearchFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.tenantRecycler)).check(matches(isDisplayed()));
    }

    @Test
    public void NavAuditorContainerToTenantExpandedFragment() {
        onView(withId(R.id.tenantRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.cardView)));
        onView(withId(R.id.expandedTenantFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void NavAuditorContainerToReportsFragment() {
        // tenant search fragment should be the first fragment displayed
        // we can go to tenant search fragment by pressing the Tenants button in the drawer
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(2); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_Reports)).perform(click());

        onView(withId(R.id.reports)).check(matches(isDisplayed()));
    }

    @Test
    public void NavAuditorContainerToAddTenantFragment() {
        // tenant search fragment should be the first fragment displayed
        // we can go to tenant search fragment by pressing the Tenants button in the drawer
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(2); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_Add_Tenant)).perform(click());

        onView(withId(16909135)).check(matches(isDisplayed())); //alert dialogue layout
    }

    @Test
    public void NavTenantExpandedToSafetyChecklist() {
        NavAuditorContainerToTenantExpandedFragment();
        // we can click a button to start the safety checklist
        onView(withId(R.id.startSafetyChecklistButton)).perform(click());
        onView(withId(R.id.safetyChecklistFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void NavSafetyChecklistToAuditChecklist() {
        NavTenantExpandedToSafetyChecklist();
        onView(withId(R.id.safetyChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.safetyChecklistFragment)).perform(swipeUp());
        sleep(4);
        onView(withId(R.id.start_audit_button)).perform(click());
        onView(withId(R.id.auditChecklistFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void NavAuditChecklistToStatusConfirmationChecklist() {
        NavSafetyChecklistToAuditChecklist();
        onView(withId(R.id.WorkplaceSafetyAndHealthFAB)).perform(click()); // clicks the menu fab
        onView(withId(R.id.WorkplaceSafetyAndHealthFAB)).perform(click()); // clicks the workplace fab
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        onView(withId(R.id.auditChecklistFragment)).perform(swipeUp());
        sleep(1);
        onView(withId(R.id.submit_audit_button)).perform(click());
        onView(withText("Yes")).perform(click());
        onView(withId(R.id.statusConfirmationFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void NavStatusConfirmationToReportFragment() {
        NavAuditChecklistToStatusConfirmationChecklist();
        onView(withId(R.id.button_return)).check(matches(isDisplayed()));
        // the below cannot be tested due to problems testing shared pref
//        onView(withId(R.id.button_return)).perform(click());
//        onView(withId(R.id.reports)).check(matches(isDisplayed()));
//        onView(withId(R.id.reportPreviewRecyclerViewUnresolved)).check(matches(isDisplayed()));
    }
}