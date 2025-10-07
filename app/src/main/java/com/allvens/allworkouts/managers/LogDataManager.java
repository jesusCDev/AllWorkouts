package com.allvens.allworkouts.managers;

import android.content.Context;
import android.content.Intent;

import com.allvens.allworkouts.WorkoutMaximumActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.base.BaseDataManager;
import com.allvens.allworkouts.base.BaseInterfaces;
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
public class LogDataManager extends BaseDataManager {
    
    public interface LogDataCallback extends BaseInterfaces.BaseDataCallback {
        void onWorkoutLoaded(WorkoutInfo workout);
        void onWorkoutNotFound();
        void onDataUpdated();
    }
    
    private String chosenWorkout;
    private WorkoutWrapper wrapper;
    private WorkoutInfo workout;
    
    public LogDataManager(Context context, LogDataCallback callback) {
        super(context, callback);
        this.wrapper = new WorkoutWrapper(context);
    }
    
    @Override
    protected void initializeDataSources() throws Exception {
        // Wrapper is initialized in constructor
    }
    
    @Override
    protected void loadInitialData() throws Exception {
        // Data loading is handled in initialize() method
    }
    
    @Override
    protected void cleanupDataSources() throws Exception {
        if (wrapper != null) {
            wrapper.close();
        }
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
                notifyWorkoutLoaded(workout);
            } else {
                notifyWorkoutNotFound();
            }
        } catch (Exception e) {
            wrapper.close();
            notifyDataError("Failed to load workout data: " + e.getMessage());
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
            notifyDataError("Failed to load workout history: " + e.getMessage());
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
            notifyDataError("No workout to reset");
            return;
        }
        
        try {
            wrapper.open();
            wrapper.deleteWorkout(workout);
            wrapper.close();
            
            workout = null;
            notifyCustomDataUpdated();
        } catch (Exception e) {
            wrapper.close();
            notifyDataError("Failed to reset workout: " + e.getMessage());
        }
    }
    
    /**
     * Update workout type
     */
    public void updateWorkoutType(int type) {
        if (workout == null) {
            notifyDataError("No workout to update");
            return;
        }
        
        try {
            workout.setType(type);
            
            wrapper.open();
            wrapper.updateWorkout(workout);
            wrapper.close();
            
            notifyCustomDataUpdated();
        } catch (Exception e) {
            wrapper.close();
            notifyDataError("Failed to update workout type: " + e.getMessage());
        }
    }
    
    /**
     * Launch workout maximum edit activity
     */
    public void launchMaxValueEdit() {
        if (workout == null) {
            notifyDataError("No workout to edit");
            return;
        }
        
        try {
            Intent intent = new Intent(getContext(), WorkoutMaximumActivity.class);
            intent.putExtra(Constants.WORKOUT_TYPE_KEY, workout.getType());
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
            intent.putExtra(Constants.UPDATING_MAX_IN_SETTINGS, true);
            
            // Use activity callback for navigation - cast to BaseUICallback
            if (getCallback() instanceof com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback) {
                ((com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback) getCallback()).onNavigationRequested(intent, false);
            }
        } catch (Exception e) {
            notifyDataError("Failed to launch max value editor: " + e.getMessage());
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
     * Notify callback of workout loaded
     */
    private void notifyWorkoutLoaded(WorkoutInfo workout) {
        if (getCallback() instanceof LogDataCallback) {
            ((LogDataCallback) getCallback()).onWorkoutLoaded(workout);
        }
    }
    
    /**
     * Notify callback that workout was not found
     */
    private void notifyWorkoutNotFound() {
        if (getCallback() instanceof LogDataCallback) {
            ((LogDataCallback) getCallback()).onWorkoutNotFound();
        }
    }
    
    /**
     * Notify callback that data was updated
     */
    private void notifyCustomDataUpdated() {
        if (getCallback() instanceof LogDataCallback) {
            ((LogDataCallback) getCallback()).onDataUpdated();
        }
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