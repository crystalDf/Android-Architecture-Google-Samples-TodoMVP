package com.star.todomvp.util;

import android.support.test.espresso.IdlingResource;

public class EspressoIdlingResource {

    private static final String RESOURCE = "GLOBAL";

    private static SimpleCountingIdlingResource sSimpleCountingIdlingResource =
            new SimpleCountingIdlingResource(RESOURCE);

    public static IdlingResource getIdlingResource() {
        return sSimpleCountingIdlingResource;
    }

    public static void increment() {

        sSimpleCountingIdlingResource.increment();
    }

    public static void decrement() {

        sSimpleCountingIdlingResource.decrement();
    }
}
