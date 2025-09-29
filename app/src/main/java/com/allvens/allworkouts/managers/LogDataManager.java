package com.allvens.allworkouts.managers;

import android.content.Context;
import android.content.Intent;

import com.allvens.allworkouts.WorkoutMaximumActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.log_manager.log_chart.LineChartDataEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Manager for LogActivity
 * Handles all data-related operations including database access,
 * workout data retrieval, data processing for charts, and workout persistence
 */
public class LogDataManager {
    
    public interface LogDataCallback {
        void onWorkoutLoaded(WorkoutInfo workout);
        void onWorkoutNotFound();
        void onDataUpdated();
        void onDataError(String error);
        void onNavigationRequested(Intent intent);
    }
    
    private Context context;
    private LogDataCallback callback;
    private String chosenWorkout;
    private WorkoutWrapper wrapper;
    private WorkoutInfo workout;
    
    public LogDataManager(Context context, LogDataCallback callback) {
        this.context = context;
        this.callback = callback;
        this.wrapper = new WorkoutWrapper(context);
    }
    
    /**
     * Initialize data manager with chosen workout
     */
    public void initialize(String chosenWorkout) {
        this.chosenWorkout = chosenWorkout;
        loadWorkoutData();
    }
    
    /**
     * Load workout data from database
     */
    private void loadWorkoutData() {
        try {
            wrapper.open();
            workout = wrapper.getWorkout(chosenWorkout);
            wrapper.close();
            
            if (workout != null) {
                callback.onWorkoutLoaded(workout);
            } else {
                callback.onWorkoutNotFound();
            }
        } catch (Exception e) {
            wrapper.close();
            callback.onDataError("Failed to load workout data: " + e.getMessage());
        }
    }
    
    /**
     * Get workout history data for charts and lists
     */
    public List<WorkoutHistoryInfo> getWorkoutHistory() {
        if (workout == null) {
            return new ArrayList<>();
        }
        
        try {
            wrapper.open();
            List<WorkoutHistoryInfo> history = wrapper.getHistoryForWorkout(workout.getId());
            wrapper.close();
            return history != null ? history : new ArrayList<>();
        } catch (Exception e) {
            wrapper.close();
            callback.onDataError("Failed to load workout history: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get chart data processed from workout history
     * Limits to last 21 entries for chart readability
     */
    public ArrayList<LineChartDataEntry> getChartData() {
        List<WorkoutHistoryInfo> historyList = getWorkoutHistory();
        ArrayList<LineChartDataEntry> entries = new ArrayList<>();
        
        if (historyList != null && !historyList.isEmpty()) {
            int startIndex = 0;
            if (historyList.size() > 21) {
                startIndex = historyList.size() - 21;
            }
            
            for (int i = startIndex; i < historyList.size(); i++) {
                entries.add(new LineChartDataEntry(i, historyList.get(i).get_TotalReps()));
            }
        }
        
        return entries;
    }
    
    /**
     * Reset workout data (delete workout and history)
     */
    public void resetWorkout() {
        if (workout == null) {
            callback.onDataError("No workout to reset");
            return;
        }
        
        try {
            wrapper.open();
            wrapper.deleteWorkout(workout);
            wrapper.close();
            
            workout = null;
            callback.onDataUpdated();
        } catch (Exception e) {
            wrapper.close();
            callback.onDataError("Failed to reset workout: " + e.getMessage());
        }
    }
    
    /**
     * Update workout type
     */
    public void updateWorkoutType(int type) {
        if (workout == null) {
            callback.onDataError("No workout to update");
            return;
        }
        
        try {
            workout.setType(type);
            
            wrapper.open();
            wrapper.updateWorkout(workout);
            wrapper.close();
            
            callback.onDataUpdated();
        } catch (Exception e) {
            wrapper.close();
            callback.onDataError("Failed to update workout type: " + e.getMessage());
        }
    }
    
    /**
     * Launch workout maximum edit activity
     */
    public void launchMaxValueEdit() {
        if (workout == null) {
            callback.onDataError("No workout to edit");
            return;
        }
        
        try {
            Intent intent = new Intent(context, WorkoutMaximumActivity.class);
            intent.putExtra(Constants.WORKOUT_TYPE_KEY, workout.getType());
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
            intent.putExtra(Constants.UPDATING_MAX_IN_SETTINGS, true);
            
            callback.onNavigationRequested(intent);
        } catch (Exception e) {
            callback.onDataError("Failed to launch max value editor: " + e.getMessage());
        }
    }
    
    /**
     * Get current workout info
     */
    public WorkoutInfo getWorkout() {
        return workout;
    }
    
    /**
     * Get current workout name
     */
    public String getWorkoutName() {
        return chosenWorkout;
    }
    
    /**
     * Get current max value
     */
    public int getCurrentMax() {
        return workout != null ? workout.getMax() : 0;
    }
    
    /**
     * Get current workout type
     */
    public int getCurrentType() {
        return workout != null ? workout.getType() : 0;
    }
    
    /**
     * Check if workout exists
     */
    public boolean hasWorkout() {
        return workout != null;
    }
    
    /**
     * Refresh workout data from database
     */
    public void refreshData() {
        loadWorkoutData();
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        try {
            if (wrapper != null) {
                wrapper.close();
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
        workout = null;
        chosenWorkout = null;
    }
}