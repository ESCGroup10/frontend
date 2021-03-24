package com.example.singhealthapp;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;


import com.example.singhealthapp.Containers.AuditorFragmentContainer;

import org.junit.Before;
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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AddTenantTest {
    @Rule
    public ActivityTestRule<AuditorFragmentContainer> activityRule =
            new ActivityTestRule<>(AuditorFragmentContainer.class);

    @Before
    public void init(){
        activityRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void TestValidInput(){
        onView(withId(R.id.text2)).perform(typeText("TestName"));
        onView(withId(R.id.text3)).perform(typeText("TestCompany"));
        onView(withId(R.id.text4)).perform(typeText("Test@Email.com"));
        onView(withId(R.id.text5)).perform(typeText("TestLocation"));
        onView(withId(R.id.text6)).perform(typeText("TestInstitution"));

        onView(withId(R.id.addTenantConfirm)).perform(click());
        onView(withText("SUCCESS")).check(matches(isDisplayed()));
        pressBack();
    }

    @Test
    public void TestInvalidEmailInput(){
        onView(withId(R.id.text2)).perform(typeText("TestName"));
        onView(withId(R.id.text3)).perform(typeText("TestCompany"));
        onView(withId(R.id.text4)).perform(typeText("Test@Email.4"));
        onView(withId(R.id.text5)).perform(typeText("TestLocation"));
        onView(withId(R.id.text6)).perform(typeText("TestInstitution"));

        onView(withId(R.id.addTenantConfirm)).perform(click());
        onView(withText("ERROR")).check(matches(isDisplayed()));
        pressBack();

    }

    @Test
    public void TestEmptyFieldInput(){

        onView(withId(R.id.addTenantConfirm)).perform(click());
        onView(withText("ERROR")).check(matches(isDisplayed()));
        pressBack();

    }
}
