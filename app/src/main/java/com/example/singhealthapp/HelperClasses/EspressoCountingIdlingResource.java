package com.example.singhealthapp.HelperClasses;

import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class EspressoCountingIdlingResource {
    private static final String RESOURCE = "GLOBAL";
    private static CountingIdlingResource mCountingIdlingResource = new CountingIdlingResource(RESOURCE);

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
        return mCountingIdlingResource;
    }

}
