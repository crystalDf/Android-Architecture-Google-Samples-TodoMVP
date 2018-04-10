package com.star.todomvp.util;

public class EspressoIdlingResource {

    private static final String RESOURCE = "GLOBAL";

    private static SimpleCountingIdlingResource sSimpleCountingIdlingResource =
            new SimpleCountingIdlingResource(RESOURCE);


}
