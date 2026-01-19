package com.allvens.allworkouts.data_manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Manages daily limits for features like extra breaks.
 * Tracks usage per day and resets automatically when a new day starts.
 */
public class DailyLimitManager {

    private static final int MAX_DAILY_EXTRA_BREAKS = 3;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private final SharedPreferences prefs;

    public DailyLimitManager(Context context) {
        this.prefs = context.getSharedPreferences(PreferencesValues.PREFS_NAMES, Context.MODE_PRIVATE);
    }

    /**
     * Get the current date as a string in yyyy-MM-dd format
     */
    private String getTodayDateString() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * Reset the daily counter if it's a new day
     */
    private void resetIfNewDay() {
        String storedDate = prefs.getString(PreferencesValues.EXTRA_BREAKS_DATE, "");
        String today = getTodayDateString();

        if (!today.equals(storedDate)) {
            // New day - reset the counter
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PreferencesValues.EXTRA_BREAKS_DATE, today);
            editor.putInt(PreferencesValues.EXTRA_BREAKS_USED_TODAY, 0);
            editor.apply();
        }
    }

    /**
     * Get the number of extra breaks remaining for today
     * @return Number of extra breaks remaining (0 to MAX_DAILY_EXTRA_BREAKS)
     */
    public int getExtraBreaksRemaining() {
        resetIfNewDay();
        int usedToday = prefs.getInt(PreferencesValues.EXTRA_BREAKS_USED_TODAY, 0);
        return Math.max(0, MAX_DAILY_EXTRA_BREAKS - usedToday);
    }

    /**
     * Use one extra break. Call this when the user requests an extra break.
     * @return true if the break was used successfully, false if no breaks remaining
     */
    public boolean useExtraBreak() {
        resetIfNewDay();
        int usedToday = prefs.getInt(PreferencesValues.EXTRA_BREAKS_USED_TODAY, 0);

        if (usedToday >= MAX_DAILY_EXTRA_BREAKS) {
            return false; // No breaks remaining
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PreferencesValues.EXTRA_BREAKS_USED_TODAY, usedToday + 1);
        editor.apply();
        return true;
    }

    /**
     * Check if there are any extra breaks remaining today
     * @return true if at least one break is available
     */
    public boolean hasExtraBreaksRemaining() {
        return getExtraBreaksRemaining() > 0;
    }
}
