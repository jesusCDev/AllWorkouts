package com.allvens.allworkouts.data_manager;

public interface Preferences_Values {

    String PREFS_NAMES = "com.allvens.AllWorkouts";
    String FIRST_TIME_USING = "first_time_using";

    String PULL_POS = "pull_pos";
    String PULL_STAT = "pull_status";
    String PUSH_POS = "push_pos";
    String PUSH_STAT = "push_status";
    String SQT_POS = "sqt_pos";
    String SQT_STAT = "sqt_status";
    String SIT_POS = "sit_pos";
    String SIT_STAT = "sit_status";

    String SOUND_ON = "sound_pref"; // boolean
    String VIBRATE_ON = "vibrate_pref"; // boolean
    String SCREEN_ON = "screen_on_pref"; // boolean
    String NOTIFICATION_ON = "notifications_pref"; // boolean
    String NOTIFICATION_TIME_HOUR = "notification_time_hour"; // int hour
    String NOTIFICATION_TIME_MINTUE = "notification_time_min"; // int minute
    String NOTIFICATION_DAYS = "notification_days"; // String 1 and 0 for on off for days.
}