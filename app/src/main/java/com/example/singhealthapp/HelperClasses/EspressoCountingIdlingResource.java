package com.example.singhealthapp.HelperClasses;

import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class EspressoCountingIdlingResource {
    private static final String RESOURCE = "GLOBAL";
    private static boolean activated = false;
    private static int count = 0;
    private static final CountingIdlingResource mCountingIdlingResource = new CountingIdlingResource(RESOURCE);

    public static void increment() {
        if (activated) {
            mCountingIdlingResource.increment();
            count++;
        }
    }

    public static void increment(int i) {
        if (activated) {
            for (int j=0; j<i; j++) {
                mCountingIdlingResource.increment();
                count++;
            }
        }
    }

    public static void decrement() {
        if (activated) {
            mCountingIdlingResource.decrement();
            count--;
        }
    }

    public static int getCount() {
        return count;
    }

    public static void dumpStateToLogs() {
        mCountingIdlingResource.dumpStateToLogs();
    }

    public static IdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }

    public static void activate() {
        activated = true;
    }
}
