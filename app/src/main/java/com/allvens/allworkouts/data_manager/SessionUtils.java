package com.allvens.allworkouts.data_manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for managing workout session state.
 * Provides a safety net for session tracking using SharedPreferences.
 */
public class SessionUtils {
    
    private static final String SESSION_PREFS_NAME = "workout_session_prefs";
    private static final String KEY_SESSION_START_WORKOUT = "session_start_workout";
    
    /**
     * Saves the workout that started the current session
     * 
     * @param context Application context
     * @param startWorkout Name of the workout that started the session
     */
    public static void saveSessionStart(Context context, String startWorkout) {
        SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_SESSION_START_WORKOUT, startWorkout)
                .apply();
    }
    
    /**
     * Gets the workout that started the current session
     * 
     * @param context Application context
     * @return Name of the workout that started the session, or null if not found
     */
    public static String getSessionStart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SESSION_START_WORKOUT, null);
    }
    
    /**
     * Clears the session state (call when session is completed or user exits)
     * 
     * @param context Application context
     */
    public static void clearSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_SESSION_START_WORKOUT)
                .apply();
    }
    
    /**
     * Clears the session state and resets the cycle counter (call when session is fully completed)
     * 
     * @param context Application context
     */
    public static void clearSessionAndResetCycle(Context context) {
        clearSession(context);
                
        // Also clear the workout cycle counter
        SharedPreferences cyclePrefs = context.getSharedPreferences("workout_cycle_counter", Context.MODE_PRIVATE);
        cyclePrefs.edit().clear().apply();
    }
    
    /**
     * Initializes a new workout session cycle counter
     * 
     * @param context Application context
     */
    public static void initializeNewCycle(Context context) {
        SharedPreferences cyclePrefs = context.getSharedPreferences("workout_cycle_counter", Context.MODE_PRIVATE);
        cyclePrefs.edit()
                .putInt("completed_workouts", 0)
                .apply();
    }
    
    /**
     * Checks if there is an active session
     * 
     * @param context Application context
     * @return true if there is an active session, false otherwise
     */
    public static boolean hasActiveSession(Context context) {
        return getSessionStart(context) != null;
    }
}