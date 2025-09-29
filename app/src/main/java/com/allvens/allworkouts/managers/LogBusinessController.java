package com.allvens.allworkouts.managers;

import android.content.Context;

import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.log_manager.LogUIManager;
import com.allvens.allworkouts.log_manager.log_chart.LineChartDataEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Business Controller for LogActivity
 * Handles business logic, validation, data processing, and coordination
 * between UI and data layers
 */
public class LogBusinessController {
    
    public interface LogControllerCallback {
        void onUIUpdateRequested();
        void onErrorOccurred(String error);
        void onInfoMessage(String message);
        void onWorkoutNotStarted();
    }
    
    private Context context;
    private LogControllerCallback callback;
    private LogDataManager dataManager;
    private LogUIManager logUIManager;
    
    // State tracking
    private boolean isInitialized = false;
    private boolean hasValidWorkout = false;
    
    public LogBusinessController(Context context, LogControllerCallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    /**
     * Initialize the controller with data and UI managers
     */
    public void initialize(LogDataManager dataManager, LogUIManager logUIManager) {
        this.dataManager = dataManager;
        this.logUIManager = logUIManager;
        this.isInitialized = true;
        
        // Trigger initial data load and UI update
        refreshScreen();
    }
    
    /**
     * Refresh screen with current data
     */
    public void refreshScreen() {
        if (!isInitialized) {
            callback.onErrorOccurred("Controller not initialized");
            return;
        }
        
        if (dataManager.hasWorkout()) {
            hasValidWorkout = true;
            updateUIWithWorkoutData();
        } else {
            hasValidWorkout = false;
            updateUIWithNoWorkout();
        }
    }
    
    /**
     * Update UI with workout data
     */
    private void updateUIWithWorkoutData() {
        try {
            // Get data from data manager
            ArrayList<LineChartDataEntry> chartData = dataManager.getChartData();
            List<WorkoutHistoryInfo> historyData = dataManager.getWorkoutHistory();
            int currentMax = dataManager.getCurrentMax();
            int currentType = dataManager.getCurrentType();
            
            // Update UI through LogUIManager
            logUIManager.update_Graph(chartData);
            logUIManager.update_SetList(historyData);
            logUIManager.update_CurrentMax(currentMax);
            logUIManager.update_CurrentType(currentType);
            
        } catch (Exception e) {
            callback.onErrorOccurred("Failed to update UI: " + e.getMessage());
        }
    }
    
    /**
     * Update UI when no workout data is available
     */
    private void updateUIWithNoWorkout() {
        try {
            logUIManager.reset_GraphToZero();
            logUIManager.reset_SetList();
            logUIManager.update_CurrentMax(0);
        } catch (Exception e) {
            callback.onErrorOccurred("Failed to reset UI: " + e.getMessage());
        }
    }
    
    /**
     * Handle reset workout request
     */
    public void handleResetWorkout() {
        if (!hasValidWorkout) {
            callback.onWorkoutNotStarted();
            return;
        }
        
        try {
            dataManager.resetWorkout();
            // Data manager will trigger callback which will refresh the screen
        } catch (Exception e) {
            callback.onErrorOccurred("Failed to reset workout: " + e.getMessage());
        }
    }
    
    /**
     * Handle edit max value request
     */
    public void handleEditMaxValue() {
        if (!hasValidWorkout) {
            callback.onWorkoutNotStarted();
            return;
        }
        
        try {
            dataManager.launchMaxValueEdit();
        } catch (Exception e) {
            callback.onErrorOccurred("Failed to launch max value editor: " + e.getMessage());
        }
    }
    
    /**
     * Handle edit type request
     */
    public void handleEditType(int selectedType) {
        if (!hasValidWorkout) {
            callback.onWorkoutNotStarted();
            return;
        }
        
        try {
            dataManager.updateWorkoutType(selectedType);
            // Data manager will trigger callback which will refresh the screen
        } catch (Exception e) {
            callback.onErrorOccurred("Failed to update workout type: " + e.getMessage());
        }
    }
    
    /**
     * Handle data updated notification from data manager
     */
    public void onDataUpdated() {
        refreshScreen();
        callback.onUIUpdateRequested();
    }
    
    /**
     * Handle workout loaded notification from data manager
     */
    public void onWorkoutLoaded(WorkoutInfo workout) {
        hasValidWorkout = true;
        refreshScreen();
        callback.onUIUpdateRequested();
    }
    
    /**
     * Handle workout not found notification from data manager
     */
    public void onWorkoutNotFound() {
        hasValidWorkout = false;
        refreshScreen();
        callback.onUIUpdateRequested();
    }
    
    /**
     * Validate workout operations
     */
    public boolean canPerformWorkoutOperations() {
        return isInitialized && hasValidWorkout && dataManager != null;
    }
    
    /**
     * Get current workout status
     */
    public boolean hasWorkout() {
        return hasValidWorkout;
    }
    
    /**
     * Get workout name for display
     */
    public String getWorkoutName() {
        return dataManager != null ? dataManager.getWorkoutName() : "";
    }
    
    /**
     * Force refresh data from database
     */
    public void forceRefresh() {
        if (dataManager != null) {
            dataManager.refreshData();
        }
    }
    
    /**
     * Cleanup controller resources
     */
    public void cleanup() {
        isInitialized = false;
        hasValidWorkout = false;
        dataManager = null;
        logUIManager = null;
    }
}