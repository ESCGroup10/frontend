package com.example.singhealthapp;
import android.text.InputType;
import android.view.Gravity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.singhealthapp.Containers.AuditorFragmentContainer;

import androidx.test.espresso.contrib.DrawerActions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AddTenantInstrumentedTest {
    @Rule
    public ActivityScenarioRule<AuditorFragmentContainer> activityRule =
            new ActivityScenarioRule<>(AuditorFragmentContainer.class);

    @Before
    public void init() throws InterruptedException {
        onView(withId(R.id.auditor_main_page)).check(matches(isDisplayed()));
        onView(withId(R.id.auditor_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_Add_Tenant)).perform(click());
        Thread.sleep(100);
    }

    @Test
    public void TestEmptyFieldInput(){
        onView(withId(R.id.addTenantConfirm)).perform(click());
        onView(withText("ERROR")).check(matches(isDisplayed()));
    }

    @Test
    public void TestValidInput() {
        fillText("TestUser", R.id.text2);
        fillText("TestCompany", R.id.text3);
        onView(withId(R.id.text4)).perform(click());
        onView(withInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)).perform(typeText(generateString(8)+"@test.com"));
        onView(withText("OK")).perform(click());
        fillText("TestLocation", R.id.text5);
        fillText("TestInstitution", R.id.text6);

        onView(withId(R.id.addTenantConfirm)).perform(click());
        onView(withText("SUCCESS")).check(matches(isDisplayed()));
    }

    static void fillText(String string, Integer id){
        onView(withId(id)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT)).perform(typeText(string));
        onView(withText("OK")).perform(click());
    }

    @Test
    public void TestInvalidEmailInput(){
        fillText("TestUser", R.id.text2);
        fillText("TestCompany", R.id.text3);
        onView(withId(R.id.text4)).perform(click());
        onView(withInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)).perform(typeText("w"+"@test.u"));
        onView(withText("OK")).perform(click());
        onView(withText("ERROR")).check(matches(isDisplayed()));
    }

    static String generateString(int length){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
