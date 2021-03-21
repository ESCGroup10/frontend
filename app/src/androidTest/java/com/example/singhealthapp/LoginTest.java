package com.example.singhealthapp;

import android.util.Log;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Log.d("Tag", "starting up");

        try {
            onView(withId(R.id.login_page)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException notExist) {
            //proceed to the next screen
            pressBack();
            onView(withId(android.R.id.button1)).perform(click());
        }
    }

    @Test
    public void isActivityInView() {
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void WrongEmailLogin() {
        onView(withId(R.id.login_email)).perform(typeText("xxxx@test.com"));
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void WrongPasswordLogin() {
        onView(withId(R.id.login_email)).perform(typeText("auditor@test.com"));
        onView(withId(R.id.login_password)).perform(typeText("xxxx")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void EmptyPasswordLogin() {
        onView(withId(R.id.login_email)).perform(typeText("auditor@test.com"));
        onView(withId(R.id.login_password)).perform(typeText("")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
    }

    @Test
    public void EmptyEmailLogin() {
        onView(withId(R.id.login_email)).perform(typeText(""));
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void CorrectAuditorLogin() throws InterruptedException {
        onView(withId(R.id.login_email)).perform(typeText("auditor@test.com"));
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.auditor_main_page)).check(matches(isDisplayed()));
    }

    @Test
    public void CorrectTenantLogin() throws InterruptedException {
        onView(withId(R.id.login_email)).perform(typeText("tenant@test.com"));
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.tenant_main_page)).check(matches(isDisplayed()));
    }

}