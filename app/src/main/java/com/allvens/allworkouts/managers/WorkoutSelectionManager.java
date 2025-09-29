package com.allvens.allworkouts.managers;

import android.content.Context;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefsChecker;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;

/**
 * Manages workout selection logic and data operations:
 * - Fetches available workouts from preferences
 * - Validates workout selections
 * - Handles workout data filtering and sorting
 * - Manages workout persistence and state
 */
public class WorkoutSelectionManager {

    private final Context context;
    private WorkoutBasicsPrefsChecker prefsChecker;
    
    // Current state
    private String[] availableWorkouts = new String[0];
    private String currentSelectedWorkout = null;
    
    // Callback interface for data events
    public interface DataEventListener {
        void onWorkoutsRefreshed(String[] workouts);
        void onWorkoutSelectionChanged(String workout);
        void onError(String error);
    }
    
    private DataEventListener eventListener;

    public WorkoutSelectionManager(Context context) {
        this.context = context;
        this.prefsChecker = new WorkoutBasicsPrefsChecker(context);
    }
    
    /**
     * Set the event listener for data changes
     */
    public void setEventListener(DataEventListener listener) {
        this.eventListener = listener;
    }
    
    /**
     * Refresh the list of available workouts from preferences
     * This should be called in onResume to get latest data
     */
    public void refreshAvailableWorkouts() {
        try {
            WorkoutPosAndStatus[] workoutPositions = prefsChecker.getWorkoutPositions(false);
            
            availableWorkouts = new String[workoutPositions.length];
            for (int i = 0; i < workoutPositions.length; i++) {
                availableWorkouts[i] = workoutPositions[i].getName();
            }
            
            // Notify listener of updated workout list
            if (eventListener != null) {
                eventListener.onWorkoutsRefreshed(availableWorkouts);
            }
            
        } catch (Exception e) {
            // Handle error and notify listener
            if (eventListener != null) {
                eventListener.onError("Failed to load workouts: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get the currently available workouts
     */
    public String[] getAvailableWorkouts() {
        return availableWorkouts.clone(); // Return defensive copy
    }
    
    /**
     * Select a specific workout and validate it's available
     */
    public boolean selectWorkout(String workoutName) {
        if (workoutName == null || workoutName.trim().isEmpty()) {
            if (eventListener != null) {
                eventListener.onError("Invalid workout name");
            }
            return false;
        }
        
        // Validate workout exists in available workouts
        if (!isWorkoutAvailable(workoutName)) {
            if (eventListener != null) {
                eventListener.onError("Workout '" + workoutName + "' is not available");
            }
            return false;
        }
        
        currentSelectedWorkout = workoutName;
        
        // Notify listener of selection
        if (eventListener != null) {
            eventListener.onWorkoutSelectionChanged(workoutName);
        }
        
        return true;
    }
    
    /**
     * Get the currently selected workout
     */
    public String getSelectedWorkout() {
        return currentSelectedWorkout;
    }
    
    /**
     * Check if a workout exists in the available workouts list
     */
    public boolean isWorkoutAvailable(String workoutName) {
        if (workoutName == null || availableWorkouts.length == 0) {
            return false;
        }
        
        for (String workout : availableWorkouts) {
            if (workout != null && workout.equalsIgnoreCase(workoutName)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get the first available workout (useful for defaults)
     */
    public String getFirstAvailableWorkout() {
        return (availableWorkouts.length > 0) ? availableWorkouts[0] : null;
    }
    
    /**
     * Ensure we have a valid selected workout
     * If current selection is invalid, select the first available workout
     */
    public void ensureValidSelection() {
        if (currentSelectedWorkout == null || !isWorkoutAvailable(currentSelectedWorkout)) {
            String firstWorkout = getFirstAvailableWorkout();
            if (firstWorkout != null) {
                selectWorkout(firstWorkout);
            }
        }
    }
    
    /**
     * Get count of available workouts
     */
    public int getWorkoutCount() {
        return availableWorkouts.length;
    }
    
    /**
     * Check if any workouts are available
     */
    public boolean hasWorkouts() {
        return availableWorkouts.length > 0;
    }
    
    /**
     * Get workout at specific index (bounds checked)
     */
    public String getWorkoutAt(int index) {
        if (index >= 0 && index < availableWorkouts.length) {
            return availableWorkouts[index];
        }
        return null;
    }
    
    /**
     * Find index of specific workout in available workouts
     */
    public int getWorkoutIndex(String workoutName) {
        if (workoutName == null) return -1;
        
        for (int i = 0; i < availableWorkouts.length; i++) {
            if (availableWorkouts[i] != null && availableWorkouts[i].equalsIgnoreCase(workoutName)) {
                return i;
            }
        }
        
        return -1;
    }
}