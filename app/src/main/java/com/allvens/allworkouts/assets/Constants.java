package com.allvens.allworkouts.assets;

/**
 * Values used consitantly throughout the app
 * todo might switch them out for resources
 * todo might switch values for key for passing intents
 */
public interface Constants {
    String PULL_UPS                  = "Pull Ups";
    String PUSH_UPS                  = "Push Ups";
    String SIT_UPS                   = "Sit Ups";
    String SQUATS                    = "Squats";
    String BACK_STRENGTHENING        = "Back Strengthening";

    String CHOSEN_WORKOUT_EXTRA_KEY  = "chosen_workout";
    String SESSION_START_WORKOUT_KEY = "session_start_workout"; // Tracks which workout started the session for circular progression
    String WORKOUT_TYPE_KEY          = "mix_or_simple_workout";
    String UPDATING_MAX_IN_SETTINGS  = "update_max_value";

    String OPEN_SOURCE              = "open_source";
    String TERMS_OF_USE             = "terms_of_use";
    String CHOSEN_DOCUMENTATION      = "chosenDocumentation";
}
