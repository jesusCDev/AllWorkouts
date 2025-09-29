package com.allvens.allworkouts.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.WorkoutSessionFinishActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.SessionUtils;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.allvens.allworkouts.workout_session_manager.workouts.WorkoutGenerator;

/**
 * Data Manager for WorkoutSessionActivity
 * Handles workout data retrieval, session persistence, intent data handling,
 * and coordination with database operations
 */
public class WorkoutSessionDataManager {
    
    public interface WorkoutSessionDataCallback {
        void onWorkoutDataLoaded(Workout workout, WorkoutInfo workoutInfo);
        void onSessionStarted(String workoutName);
        void onSessionError(String error);
        void onNavigationRequested(Intent intent, boolean finishCurrent);
    }
    
    private Context context;
    private WorkoutSessionDataCallback callback;
    private Workout workout;
    private WorkoutInfo workoutInfo;
    private String sessionStartWorkout;
    
    public WorkoutSessionDataManager(Context context, WorkoutSessionDataCallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    /**
     * Initialize workout session with data from intent
     */
    public void initializeSession(Bundle intentExtras, String sessionStartWorkoutExtra) {
        try {
            // Extract workout choice from intent
            String workoutChoice = intentExtras.get(Constants.CHOSEN_WORKOUT_EXTRA_KEY).toString();
            
            // Handle session start workout
            sessionStartWorkout = sessionStartWorkoutExtra;
            if (sessionStartWorkout == null) {
                sessionStartWorkout = SessionUtils.getSessionStart(context);
            }
            
            // Save to SharedPreferences as backup
            if (sessionStartWorkout != null) {
                SessionUtils.saveSessionStart(context, sessionStartWorkout);
            }
            
            // Load workout data from database
            loadWorkoutData(workoutChoice);
            
        } catch (Exception e) {
            callback.onSessionError("Failed to initialize session: " + e.getMessage());
        }
    }
    
    /**
     * Load workout data from database
     */
    private void loadWorkoutData(String workoutChoice) {
        try {
            WorkoutWrapper wrapper = new WorkoutWrapper(context);
            wrapper.open();
            
            WorkoutGenerator workoutGenerator = new WorkoutGenerator(wrapper.getWorkout(workoutChoice));
            workoutInfo = workoutGenerator.getWorkoutInfo();
            workout = workoutGenerator.getWorkout();
            
            wrapper.close();
            
            // Notify callback that data is loaded
            callback.onWorkoutDataLoaded(workout, workoutInfo);
            callback.onSessionStarted(workoutInfo.getWorkout());
            
        } catch (Exception e) {
            callback.onSessionError("Failed to load workout data: " + e.getMessage());
        }
    }
    
    /**
     * Handle session completion navigation
     */
    public void handleSessionCompletion() {
        try {
            Intent intent = new Intent(context, WorkoutSessionFinishActivity.class);
            // Pass the workout name as string, not the Workout object
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, getWorkoutName());
            
            // Thread the session start workout to WorkoutSessionFinishActivity
            if (sessionStartWorkout != null) {
                intent.putExtra(Constants.SESSION_START_WORKOUT_KEY, sessionStartWorkout);
            }
            
            callback.onNavigationRequested(intent, true);
            
        } catch (Exception e) {
            callback.onSessionError("Failed to handle session completion: " + e.getMessage());
        }
    }
    
    /**
     * Handle session exit navigation
     */
    public void handleSessionExit() {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            callback.onNavigationRequested(intent, false);
            
        } catch (Exception e) {
            callback.onSessionError("Failed to handle session exit: " + e.getMessage());
        }
    }
    
    /**
     * Get current workout
     */
    public Workout getWorkout() {
        return workout;
    }
    
    /**
     * Get current workout info
     */
    public WorkoutInfo getWorkoutInfo() {
        return workoutInfo;
    }
    
    /**
     * Get session start workout name
     */
    public String getSessionStartWorkout() {
        return sessionStartWorkout;
    }
    
    /**
     * Get workout name for display
     */
    public String getWorkoutName() {
        return workoutInfo != null ? workoutInfo.getWorkout() : "";
    }
    
    /**
     * Save current session state
     */
    public void saveSessionState() {
        if (sessionStartWorkout != null) {
            SessionUtils.saveSessionStart(context, sessionStartWorkout);
        }
    }
    
    /**
     * Clear session data on cleanup
     */
    public void cleanup() {
        workout = null;
        workoutInfo = null;
        sessionStartWorkout = null;
    }
}