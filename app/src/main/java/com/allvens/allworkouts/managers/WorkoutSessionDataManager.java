package com.allvens.allworkouts.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.WorkoutSessionFinishActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.base.BaseDataManager;
import com.allvens.allworkouts.base.BaseInterfaces;
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
public class WorkoutSessionDataManager extends BaseDataManager {
    
    public interface WorkoutSessionDataCallback extends BaseInterfaces.BaseDataCallback {
        void onWorkoutDataLoaded(Workout workout, WorkoutInfo workoutInfo);
        void onSessionStarted(String workoutName);
    }
    
    private Workout workout;
    private WorkoutInfo workoutInfo;
    private String sessionStartWorkout;
    
    public WorkoutSessionDataManager(Context context, WorkoutSessionDataCallback callback) {
        super(context, callback);
    }
    
    @Override
    protected void initializeDataSources() throws Exception {
        // Data sources are managed per-session
    }
    
    @Override
    protected void loadInitialData() throws Exception {
        // Data loading is handled in initializeSession
    }
    
    @Override
    protected void cleanupDataSources() throws Exception {
        // Session cleanup is handled in cleanup() method
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
                sessionStartWorkout = SessionUtils.getSessionStart(getContext());
            }
            
            // Save to SharedPreferences as backup
            if (sessionStartWorkout != null) {
                SessionUtils.saveSessionStart(getContext(), sessionStartWorkout);
            }
            
            // Load workout data from database
            loadWorkoutData(workoutChoice);
            
        } catch (Exception e) {
            notifyDataError("Failed to initialize session: " + e.getMessage());
        }
    }
    
    /**
     * Load workout data from database
     */
    private void loadWorkoutData(String workoutChoice) {
        try {
            WorkoutWrapper wrapper = new WorkoutWrapper(getContext());
            wrapper.open();
            
            WorkoutGenerator workoutGenerator = new WorkoutGenerator(wrapper.getWorkout(workoutChoice));
            workoutInfo = workoutGenerator.getWorkoutInfo();
            workout = workoutGenerator.getWorkout();
            
            wrapper.close();
            
            // Notify callback that data is loaded
            notifyWorkoutDataLoaded(workout, workoutInfo);
            notifySessionStarted(workoutInfo.getWorkout());
            
        } catch (Exception e) {
            notifyDataError("Failed to load workout data: " + e.getMessage());
        }
    }
    
    /**
     * Handle session completion navigation
     */
    public void handleSessionCompletion() {
        handleSessionCompletion(0, false);
    }
    
    /**
     * Handle session completion navigation with duration tracking
     * @param durationSeconds Duration of the workout in seconds
     * @param isValidDuration Whether the duration is valid (not an outlier)
     */
    public void handleSessionCompletion(long durationSeconds, boolean isValidDuration) {
        android.util.Log.d("WorkoutSession", "DataManager.handleSessionCompletion() called with duration: " + durationSeconds + "s, valid: " + isValidDuration);
        try {
            Intent intent = new Intent(getContext(), WorkoutSessionFinishActivity.class);
            // Pass the workout name as string, not the Workout object
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, getWorkoutName());
            android.util.Log.d("WorkoutSession", "Intent created for WorkoutSessionFinishActivity with workout: " + getWorkoutName());
            
            // Thread the session start workout to WorkoutSessionFinishActivity
            if (sessionStartWorkout != null) {
                intent.putExtra(Constants.SESSION_START_WORKOUT_KEY, sessionStartWorkout);
                android.util.Log.d("WorkoutSession", "Added session start workout to intent: " + sessionStartWorkout);
            }
            
            // Pass duration if valid
            if (isValidDuration && durationSeconds > 0) {
                intent.putExtra(Constants.DURATION_SECONDS_KEY, durationSeconds);
                android.util.Log.d("WorkoutSession", "Added valid duration to intent: " + durationSeconds + "s");
            }
            
            // Use activity callback for navigation - cast to BaseUICallback
            if (getCallback() instanceof com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback) {
                android.util.Log.d("WorkoutSession", "Calling onNavigationRequested to WorkoutSessionFinishActivity");
                ((com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback) getCallback()).onNavigationRequested(intent, true);
                android.util.Log.d("WorkoutSession", "onNavigationRequested called successfully");
            } else {
                android.util.Log.e("WorkoutSession", "Callback is not an instance of BaseUICallback!");
            }
            
        } catch (Exception e) {
            android.util.Log.e("WorkoutSession", "Exception in handleSessionCompletion: " + e.getMessage());
            notifyDataError("Failed to handle session completion: " + e.getMessage());
        }
    }
    
    /**
     * Handle session exit navigation
     */
    public void handleSessionExit() {
        try {
            Intent intent = new Intent(getContext(), MainActivity.class);
            // Use activity callback for navigation - cast to BaseUICallback
            if (getCallback() instanceof com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback) {
                ((com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback) getCallback()).onNavigationRequested(intent, false);
            }
            
        } catch (Exception e) {
            notifyDataError("Failed to handle session exit: " + e.getMessage());
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
            SessionUtils.saveSessionStart(getContext(), sessionStartWorkout);
        }
    }
    
    /**
     * Notify callback of workout data loaded
     */
    private void notifyWorkoutDataLoaded(Workout workout, WorkoutInfo workoutInfo) {
        if (getCallback() instanceof WorkoutSessionDataCallback) {
            ((WorkoutSessionDataCallback) getCallback()).onWorkoutDataLoaded(workout, workoutInfo);
        }
    }
    
    /**
     * Notify callback of session started
     */
    private void notifySessionStarted(String workoutName) {
        if (getCallback() instanceof WorkoutSessionDataCallback) {
            ((WorkoutSessionDataCallback) getCallback()).onSessionStarted(workoutName);
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