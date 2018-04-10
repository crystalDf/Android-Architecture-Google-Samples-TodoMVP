package com.star.todomvp.util;

import android.support.test.espresso.IdlingResource;

public class SimpleCountingIdlingResource implements IdlingResource {

    private final String mResourceName;

    public SimpleCountingIdlingResource(String resourceName) {
        mResourceName = resourceName;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isIdleNow() {
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {

    }
}
