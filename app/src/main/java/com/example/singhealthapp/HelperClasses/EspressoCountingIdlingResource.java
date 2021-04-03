package com.example.singhealthapp.HelperClasses;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

public class EspressoCountingIdlingResource {
    private static final String RESOURCE = "GLOBAL";

    private static CountingIdlingResource mCountingIdlingResource =
            new CountingIdlingResource(RESOURCE);

    public static void increment() {
        System.out.println("incrementing");
        mCountingIdlingResource.increment();
    }

    public static void decrement() {
        System.out.println("decrementing");
        mCountingIdlingResource.decrement();
    }

    public static IdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }

}
