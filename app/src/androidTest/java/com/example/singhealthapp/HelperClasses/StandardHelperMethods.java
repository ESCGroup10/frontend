package com.example.singhealthapp.HelperClasses;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class StandardHelperMethods {

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

    public static void sleep(int multiplier) {
        try {
            Thread.sleep(100*multiplier);
        } catch (InterruptedException e) {
            System.out.println("caught "+e);
        }
    }

}
