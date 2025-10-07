package com.allvens.allworkouts.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.base.BaseUIManager;
import com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback;
import com.allvens.allworkouts.log_manager.LogUIManager;
import com.github.mikephil.charting.charts.LineChart;

import java.io.File;

/**
 * UI Manager for LogActivity
 * Extends BaseUIManager to follow consistent UI management patterns
 * Handles all UI coordination, view binding, and dialog management
 * Works with the existing LogUIManager for specific UI updates
 */
public class LogActivityUIManager extends BaseUIManager {
    
    public interface LogUICallback extends BaseUICallback {
        void onResetWorkout();
        void onEditMaxValue();
        void onEditType(int selectedType);
        void onWorkoutOperationResult(String message);
    }
    
    private LogUICallback logCallback;
    private LogUIManager logUIManager;
    
    // UI Elements
    private RecyclerView rvShowAllWorkoutSets;
    private LineChart lcShowWorkoutProgress;
    private TextView tvCurrentMax;
    private TextView tvType;
    private TextView tvWorkoutName;
    private String chosenWorkout;
    
    public LogActivityUIManager(Context context, LogUICallback callback) {
        super(context, callback);
        this.logCallback = callback;
    }
    
    /**
     * Initialize all views and bind them to the UI manager
     * Called from BaseUIManager.initialize() lifecycle
     */
    public void initializeViews(String chosenWorkout) {
        this.chosenWorkout = chosenWorkout;
        initialize(); // This calls the BaseUIManager lifecycle
    }
    
    @Override
    protected void initializeViews() {
        Activity activity = (Activity) context;
        
        // Find all UI elements
        rvShowAllWorkoutSets = activity.findViewById(R.id.rv_log_ShowAllWorkoutSets);
        lcShowWorkoutProgress = activity.findViewById(R.id.lc_log_ShowWorkoutProgression);
        tvCurrentMax = activity.findViewById(R.id.tv_log_CurrentMaxContainer);
        tvType = activity.findViewById(R.id.tv_log_type);
        tvWorkoutName = activity.findViewById(R.id.tv_log_workout_name);
    }
    
    @Override
    protected void setupViews() {
        // Set the workout name in the header
        if (chosenWorkout != null && tvWorkoutName != null) {
            tvWorkoutName.setText(chosenWorkout + " Log");
        }
        
        // Initialize the LogUIManager with the UI elements
        if (chosenWorkout != null) {
            logUIManager = new LogUIManager(context, chosenWorkout, 
                                           rvShowAllWorkoutSets, lcShowWorkoutProgress, 
                                           tvCurrentMax, tvType);
        }
    }
    
    @Override
    protected void setupListeners() {
        // Listeners are handled through dialog methods and callbacks
    }
    
    @Override
    protected void cleanupViews() {
        logUIManager = null;
        rvShowAllWorkoutSets = null;
        lcShowWorkoutProgress = null;
        tvCurrentMax = null;
        tvType = null;
        tvWorkoutName = null;
    }
    
    /**
     * Get the LogUIManager for delegation of specific UI updates
     */
    public LogUIManager getLogUIManager() {
        return logUIManager;
    }
    
    /**
     * Show reset workout confirmation dialog
     */
    public void showResetWorkoutDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        logCallback.onResetWorkout();
                        showSuccessMessage("Workout Data Deleted");
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        showInfoMessage("Nothing was deleted");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
        builder.setMessage("Are you sure you want to reset workout to zero?")
                .setTitle("Reset Workout")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
    
    /**
     * Show workout type selection dialog
     */
    public void showWorkoutTypeDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int type = 0;
                if (which == DialogInterface.BUTTON_NEGATIVE) type = 1;
                logCallback.onEditType(type);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
        builder.setMessage("Choose workout type")
                .setTitle("Change Workout Type")
                .setPositiveButton("Simple", dialogClickListener)
                .setNegativeButton("Mix", dialogClickListener)
                .show();
    }
    
    /**
     * Handle edit max value action
     */
    public void handleEditMaxValue() {
        logCallback.onEditMaxValue();
    }
    
    /**
     * Show workout not started error message
     */
    public void showWorkoutNotStartedError() {
        showInfoMessage("First Start Workout");
    }
    
    // Message display methods are inherited from BaseUIManager
    
    /**
     * Handle reset workout button click
     */
    public void handleResetWorkoutClick() {
        showResetWorkoutDialog();
    }
    
    /**
     * Handle edit max value button click
     */
    public void handleEditMaxValueClick() {
        handleEditMaxValue();
    }
    
    /**
     * Handle edit type button click
     */
    public void handleEditTypeClick() {
        showWorkoutTypeDialog();
    }
    
    /**
     * Update the UI after data changes
     * Overrides BaseUIManager.updateViewState() for specific log UI updates
     */
    @Override
    public void updateViewState() {
        // This will be called by the controller when data changes
        // The actual UI updates will be handled by LogUIManager through the controller
        super.updateViewState();
    }
    
    /**
     * Public method to refresh UI - delegates to base class refreshView()
     */
    public void refreshUI() {
        refreshView();
    }
}