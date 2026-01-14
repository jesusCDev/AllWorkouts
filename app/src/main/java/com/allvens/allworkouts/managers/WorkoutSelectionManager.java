package com.allvens.allworkouts.managers;

import android.content.Context;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefsChecker;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;

import java.util.ArrayList;
import java.util.List;

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
    private java.util.Set<String> maxDayWorkouts = new java.util.HashSet<>();
    private java.util.Set<String> maxSoonWorkouts = new java.util.HashSet<>();
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
     * Sorts workouts so maxing workouts (progress >= 8) appear first
     */
    public void refreshAvailableWorkouts() {
        try {
            // Create a fresh prefsChecker to ensure we get the latest data
            // (WorkoutBasicsPrefsChecker caches data in its constructor)
            prefsChecker = new WorkoutBasicsPrefsChecker(context);
            WorkoutPosAndStatus[] workoutPositions = prefsChecker.getWorkoutPositions(false);

            // Get workout progress info from database
            WorkoutWrapper wrapper = new WorkoutWrapper(context);
            wrapper.open();
            List<WorkoutInfo> allWorkoutInfo = wrapper.getAllWorkouts();
            wrapper.close();

            // Separate into maxing and non-maxing workouts while preserving order
            List<String> maxingWorkouts = new ArrayList<>();
            List<String> normalWorkouts = new ArrayList<>();
            maxDayWorkouts.clear();
            maxSoonWorkouts.clear();

            for (WorkoutPosAndStatus pos : workoutPositions) {
                String workoutName = pos.getName();
                boolean isMaxDay = isWorkoutMaxDay(workoutName, allWorkoutInfo);
                boolean isMaxSoon = isWorkoutMaxSoon(workoutName, allWorkoutInfo);

                if (isMaxDay) {
                    maxingWorkouts.add(workoutName);
                    maxDayWorkouts.add(workoutName);
                } else {
                    normalWorkouts.add(workoutName);
                    if (isMaxSoon) {
                        maxSoonWorkouts.add(workoutName);
                    }
                }
            }

            // Combine: maxing workouts first, then normal workouts
            availableWorkouts = new String[maxingWorkouts.size() + normalWorkouts.size()];
            int index = 0;
            for (String workout : maxingWorkouts) {
                availableWorkouts[index++] = workout;
            }
            for (String workout : normalWorkouts) {
                availableWorkouts[index++] = workout;
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
     * Check if a workout is on a max day (progress >= 8)
     */
    private boolean isWorkoutMaxDay(String workoutName, List<WorkoutInfo> allWorkouts) {
        for (WorkoutInfo info : allWorkouts) {
            if (info.getWorkout().equalsIgnoreCase(workoutName)) {
                return info.getProgress() >= 8;
            }
        }
        return false;
    }

    /**
     * Check if a workout is close to max day (progress 6-7)
     */
    private boolean isWorkoutMaxSoon(String workoutName, List<WorkoutInfo> allWorkouts) {
        for (WorkoutInfo info : allWorkouts) {
            if (info.getWorkout().equalsIgnoreCase(workoutName)) {
                int progress = info.getProgress();
                return progress >= 6 && progress < 8;
            }
        }
        return false;
    }
    
    /**
     * Get the currently available workouts
     */
    public String[] getAvailableWorkouts() {
        return availableWorkouts.clone(); // Return defensive copy
    }

    /**
     * Get the set of workouts that are on max day (progress >= 8)
     */
    public java.util.Set<String> getMaxDayWorkouts() {
        return new java.util.HashSet<>(maxDayWorkouts); // Return defensive copy
    }

    /**
     * Check if a specific workout is on max day
     */
    public boolean isMaxDay(String workoutName) {
        return maxDayWorkouts.contains(workoutName);
    }

    /**
     * Get the set of workouts that are close to max day (progress 6-7)
     */
    public java.util.Set<String> getMaxSoonWorkouts() {
        return new java.util.HashSet<>(maxSoonWorkouts); // Return defensive copy
    }

    /**
     * Check if a specific workout is close to max day
     */
    public boolean isMaxSoon(String workoutName) {
        return maxSoonWorkouts.contains(workoutName);
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
     * If current selection is invalid, select the first uncompleted workout (or first available)
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
     * Ensure we have a valid selected workout, preferring uncompleted workouts
     * @param completedToday Set of workout names completed today
     */
    public void ensureValidSelection(java.util.Set<String> completedToday) {
        if (currentSelectedWorkout == null || !isWorkoutAvailable(currentSelectedWorkout)) {
            // Try to select first uncompleted workout
            String firstUncompleted = getFirstUncompletedWorkout(completedToday);
            if (firstUncompleted != null) {
                selectWorkout(firstUncompleted);
            } else {
                // All workouts completed, select first available
                String firstWorkout = getFirstAvailableWorkout();
                if (firstWorkout != null) {
                    selectWorkout(firstWorkout);
                }
            }
        }
    }
    
    /**
     * Get the first uncompleted workout from available workouts
     * @param completedToday Set of workout names completed today
     * @return First uncompleted workout name, or null if all completed
     */
    private String getFirstUncompletedWorkout(java.util.Set<String> completedToday) {
        if (completedToday == null || availableWorkouts.length == 0) {
            return getFirstAvailableWorkout();
        }
        
        for (String workout : availableWorkouts) {
            if (!completedToday.contains(workout)) {
                return workout;
            }
        }
        
        return null; // All workouts completed
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
    
    /**
     * Align all workouts to reach max on the same day (days from today)
     * @param daysFromToday How many days from today should all workouts reach max
     * @return true if successful, false otherwise
     */
    public boolean alignMaxWorkouts(int daysFromToday) {
        if (daysFromToday < 0) return false;
        
        WorkoutWrapper wrapper = new WorkoutWrapper(context);
        try {
            wrapper.open();
            List<WorkoutInfo> allWorkouts = wrapper.getAllWorkouts();
            
            // For each workout, calculate what progress it needs to reach max in daysFromToday days
            // Max is at progress 8
            // If we want max in N days, and we assume today's workout completes (progress+1),
            // then we need: (progress+1) + N = 8, so progress = 7 - N
            int targetProgress = 7 - daysFromToday;
            
            if (targetProgress < 0) {
                // Can't go back in progress, max day already passed
                return false;
            }
            
            // Update all workouts to this progress
            for (WorkoutInfo workout : allWorkouts) {
                workout.setProgress(targetProgress);
                wrapper.updateWorkout(workout);
            }
            
            return true;
        } catch (Exception e) {
            android.util.Log.e("WorkoutSelection", "Error aligning max workouts: " + e.getMessage());
            return false;
        } finally {
            wrapper.close();
        }
    }
}
