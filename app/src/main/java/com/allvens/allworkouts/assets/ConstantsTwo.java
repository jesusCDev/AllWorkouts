package com.allvens.allworkouts.assets;

// todo DELETE MAYBE?
public enum ConstantsTwo {

    PUll_Ups("Pull Ups"), PUSH_UPS("Push Ups"), SIT_UPS("Sit Ups"), SQUATS("Squats"),
    CHOSEN_WORKOUT_EXTRA_KEY("chosen_workout"), WORKOUT_TYPE_KEY("mix_or_simple_workout"), UPDATING_MAX_IN_SETTINGS("update_max_value");

    private final String text;

    ConstantsTwo(String text) {
        this.text = text;
    }
}
