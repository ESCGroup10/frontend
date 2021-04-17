package com.example.singhealthapp.HelperClasses;

import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class EspressoCountingIdlingResource {
    private static final String RESOURCE = "GLOBAL";
    private static boolean activated = false;
    private static CountingIdlingResource mCountingIdlingResource = new CountingIdlingResource(RESOURCE);

    public static void increment() {
        if (activated) {
            System.out.println("incrementing");
            mCountingIdlingResource.increment();
        }
    }

    public static void increment(int i) {
        if (activated) {
            System.out.println("incrementing");
            for (int j=0; j<i; j++) {
                mCountingIdlingResource.increment();
            }
        }
    }

    public static void decrement() {
        if (activated) {
            System.out.println("decrementing");
            mCountingIdlingResource.decrement();
        }
    }

    public static void activate() {
        activated = true;
    }

    public static IdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }

}
