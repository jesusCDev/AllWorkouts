package com.allvens.allworkouts.data_manager;

public interface PreferencesValues {

    String PREFS_NAMES              = "com.allvens.AllWorkouts";

    /********** Position and Status Keys **********/
    String PULL_POS                 = "pull_pos";
    String PULL_STAT                = "pull_status";
    String PUSH_POS                 = "push_pos";
    String PUSH_STAT                = "push_status";
    String SQT_POS                  = "sqt_pos";
    String SQT_STAT                 = "sqt_status";
    String SIT_POS                  = "sit_pos";
    String SIT_STAT                 = "sit_status";
    String BACK_POS                 = "back_pos";
    String BACK_STAT                = "back_status";

    /********** Settings Keys **********/
    String SOUND_ON                 = "sound_pref";              // boolean
    String VIBRATE_ON               = "vibrate_pref";            // boolean
    String SCREEN_ON                = "screen_on_pref";          // boolean
    String NOTIFICATION_ON          = "notifications_pref";      // boolean
    String NOTIFICATION_TIME_HOUR   = "notification_time_hour";  // int hour
    String NOTIFICATION_TIME_MINUTE = "notification_time_min";   // int minute
    String NOTIFICATION_DAYS        = "notification_days";       // String 1 and 0 for on off for days.
    String MEDIA_CONTROLS_ON        = "media_controls_pref";     // boolean
    
    /********** Backup Keys **********/
    String AUTO_BACKUP_ENABLED       = "auto_backup_enabled";     // boolean
    String BACKUP_PROMPT_DISMISSED   = "backup_prompt_dismissed"; // boolean - resets on reinstall
    String BACKUP_FOLDER_URI         = "backup_folder_uri";       // String URI - persisted SAF folder access
    
    /********** Display Settings **********/
    String SHOW_TIME_ESTIMATE        = "show_time_estimate";      // boolean - default true
    String SHOW_STATS_CARDS          = "show_stats_cards";        // boolean - default true
}
