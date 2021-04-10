package com.example.singhealthapp;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.singhealthapp.Views.Login.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.not;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

// Instrumented test for UI
// ensure that users only navigate to homepage if they successfully log in

// Robustness test:
// if users key in the wrong email/password for 5 times, the login button will be disabled for 10s
// saboteurs will take much longer for brute force login attacks to succeed (much harder)

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void isActivityInView() {
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void CorrectAuditorLogin() throws InterruptedException {
        onView(withId(R.id.login_email)).perform(typeText("auditor@test.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.auditor_main_page)).check(matches(isDisplayed()));

        onView(withId(R.id.tenant_search_bar)).perform(closeSoftKeyboard());
        pressBack();
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void CorrectTenantLogin() throws InterruptedException {
        onView(withId(R.id.login_email)).perform(typeText("tenant@test.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.tenant_main_page)).check(matches(isDisplayed()));

        pressBack();
        onView(withId(android.R.id.button1)).perform(click());
    }


    @Test
    public void WrongEmailLogin() {
        onView(withId(R.id.login_email)).perform(typeText("xxxx@test.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void WrongPasswordLogin() {
        onView(withId(R.id.login_email)).perform(typeText("auditor@test.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("xxxx")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void EmptyPasswordLogin() {
        onView(withId(R.id.login_email)).perform(typeText("auditor@test.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
    }

    @Test
    public void EmptyEmailLogin() {
        onView(withId(R.id.login_email)).perform(typeText("")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void disableLogin() throws InterruptedException {
        onView(withId(R.id.login_email)).perform(typeText("xxxx@test.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("1234")).perform(closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click()).perform(click()); Thread.sleep(1000);
        onView(withId(R.id.loginButton)).perform(click()).perform(click()); Thread.sleep(1000);
        onView(withId(R.id.loginButton)).perform(click()).perform(click()); Thread.sleep(1000);
        onView(withId(R.id.loginButton)).perform(click()).perform(click()); Thread.sleep(1000);
        onView(withId(R.id.loginButton)).perform(click()).perform(click()); Thread.sleep(1000);

        onView(withId(R.id.loginButton)).check(matches(not(isEnabled())));
    }

}