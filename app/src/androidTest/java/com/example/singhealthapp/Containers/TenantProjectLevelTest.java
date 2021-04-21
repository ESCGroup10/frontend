package com.example.singhealthapp.Containers;

import android.view.Gravity;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.singhealthapp.HelperClasses.EspressoCountingIdlingResource;
import com.example.singhealthapp.HelperClasses.StandardHelperMethods;
import com.example.singhealthapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TenantProjectLevelTest extends StandardHelperMethods {

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

    @Rule
    public ActivityScenarioRule<TenantFragmentContainer> rule = new ActivityScenarioRule<>(TenantFragmentContainer.class);

    @Before
    public void setUp() {
        // register idling resources here
        rule.getScenario().onActivity(TenantFragmentContainer::activateEspressoIdlingResource);
        IdlingRegistry.getInstance().register(EspressoCountingIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() {
        // unregister idling resources here if any
        IdlingRegistry.getInstance().unregister(EspressoCountingIdlingResource.getIdlingResource());
    }

    @Test
    public void checkFirstFragment() {
        onView(withId(R.id.latestReportScrollView)).check(matches(isDisplayed()));
    }

    @Test
    public void NavTenantContainerToStatisticsFragment() {
        onView(withId(R.id.tenant_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(2); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_Tenant_Statistics)).perform(click());
        onView(withId(R.id.fragment_statistics)).check(matches(isDisplayed()));
    }

    @Test
    public void NavTenantContainerToMyReportsFragment() {
        onView(withId(R.id.tenant_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(2); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_MyReport)).perform(click());
        onView(withId(R.id.reports)).check(matches(isDisplayed()));
    }

    @Test
    public void NavTenantContainerToLatestReportFragment() {
        onView(withId(R.id.tenant_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        sleep(2); // click operation is handled in external class so we use sleep instead of idling resource here
        onView(withId(R.id.nav_LatestReport)).perform(click());
        onView(withId(R.id.latestReportScrollView)).check(matches(isDisplayed()));
    }

    @Test
    public void MyReportsFragmentToReportSummaryFragment() {
        NavTenantContainerToMyReportsFragment();
        onView(withId(R.id.reportPreviewRecyclerViewUnresolved)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.card_report)));
        onView(withId(R.id.summaryReportScrollView)).check(matches(isDisplayed()));
    }

    @Test
    public void ReportSummaryFragmentToCasesPreviewFragment() {
        MyReportsFragmentToReportSummaryFragment();
        sleep(5);
        onView(withId(R.id.auditorReportViewCases)).perform(click());
        onView(withId(R.id.CasesPreview)).check(matches(isDisplayed()));
    }

//    @Test
//    public void CasesPreviewFragmentToCaseExpanded() {
//        ReportSummaryFragmentToCasesPreviewFragment();
//        sleep(5);
//        onView(withId(R.id.casePreviewRecyclerViewUnresolved)).perform(
//                RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.casePreviewCard)));
//        onView(withId(R.id.fragment_expanded_case)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void CaseExpandedToStatusConfirmationFragment() {
//        // We cannot test setting a picture so we will test trying to submit without a picture
//        CasesPreviewFragmentToCaseExpanded();
//        onView(withId(R.id.resolveButton)).perform(click());
//        onView(withId(R.id.fragment_expanded_case)).perform(swipeUp());
//        onView(withId(R.id.confirmButton)).perform(click());
//        onView(withId(R.id.fragment_expanded_case)).check(matches(isDisplayed()));
//    }



}