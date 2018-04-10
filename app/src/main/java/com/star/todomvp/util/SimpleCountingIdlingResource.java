package com.star.todomvp.util;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleCountingIdlingResource implements IdlingResource {

    private final String mResourceName;
    private final AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private volatile ResourceCallback mResourceCallback;

    public SimpleCountingIdlingResource(String resourceName) {
        mResourceName = checkNotNull(resourceName);
    }

    @Override
    public String getName() {
        return mResourceName;
    }

    @Override
    public boolean isIdleNow() {
        return mAtomicInteger.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.mResourceCallback = callback;
    }

    public void increment() {
        mAtomicInteger.getAndIncrement();
    }

    public void decrement() {

        int countVal = mAtomicInteger.decrementAndGet();

        if (countVal == 0) {

            if (null != mResourceCallback) {
                mResourceCallback.onTransitionToIdle();
            }
        }

        if (countVal < 0) {

            throw new IllegalArgumentException("Counter has been corrupted!");
        }
    }
}
