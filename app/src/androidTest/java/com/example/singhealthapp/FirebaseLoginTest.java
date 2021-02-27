package com.example.singhealthapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
public class FirebaseLoginTest {

    @Rule
    public ActivityScenarioRule<FirebaseLogin> activityRule =
            new ActivityScenarioRule<>(FirebaseLogin.class);

    @Test
    public void isActivityInView() {
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void CorrectAuditorLogin() {
        onView(withId(R.id.login_email)).perform(typeText("testauditor@gmail.com"));
        onView(withId(R.id.login_password)).perform(typeText("testpw"));

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.auditor_main_page)).check(matches(isDisplayed()));

        pressBack();
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void CorrectTenantLogin() {
        onView(withId(R.id.login_email)).perform(typeText("testtenant@gmail.com"));
        onView(withId(R.id.login_password)).perform(typeText("testpw"));

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.tenant_main_page)).check(matches(isDisplayed()));

        pressBack();
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void WrongEmailLogin() {
        onView(withId(R.id.login_email)).perform(typeText("wrongemail@gmail.com"));
        onView(withId(R.id.login_password)).perform(typeText("testpw"));

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void WrongPasswordLogin() {
        onView(withId(R.id.login_email)).perform(typeText("testauditor@gmail.com"));
        onView(withId(R.id.login_password)).perform(typeText("wrongpw"));

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void EmptyPasswordLogin() {
        onView(withId(R.id.login_email)).perform(typeText("testauditor@gmail.com"));
        onView(withId(R.id.login_password)).perform(typeText(""));

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    @Test
    public void EmptyEmailLogin() {
        onView(withId(R.id.login_email)).perform(typeText(""));
        onView(withId(R.id.login_password)).perform(typeText("testpw"));

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }


}