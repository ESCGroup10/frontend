package com.example.singhealthapp.HelperClasses;

import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

// TODO: make counting resource only apply during test
public class EspressoCountingIdlingResource {
    private static final String RESOURCE = "GLOBAL";

    private static CountingIdlingResource mCountingIdlingResource;

    public static void increment() {
        if (mCountingIdlingResource != null) {
            System.out.println("incrementing");
            mCountingIdlingResource.increment();
        }
    }

    public static void decrement() {
        if (mCountingIdlingResource != null) {
            System.out.println("decrementing");
            mCountingIdlingResource.decrement();
        }
    }

    public static IdlingResource getIdlingResource() {
        mCountingIdlingResource = new CountingIdlingResource(RESOURCE);
        return mCountingIdlingResource;
    }

}
