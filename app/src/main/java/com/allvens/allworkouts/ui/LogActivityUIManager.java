package com.allvens.allworkouts.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.log_manager.LogUIManager;
import com.github.mikephil.charting.charts.LineChart;

/**
 * UI Manager for LogActivity
 * Handles all UI coordination, view binding, and dialog management
 * Works with the existing LogUIManager for specific UI updates
 */
public class LogActivityUIManager {
    
    public interface LogUICallback {
        void onResetWorkout();
        void onEditMaxValue();
        void onEditType(int selectedType);
        void onWorkoutOperationResult(String message);
    }
    
    private Context context;
    private LogUICallback callback;
    private LogUIManager logUIManager;
    
    // UI Elements
    private RecyclerView rvShowAllWorkoutSets;
    private LineChart lcShowWorkoutProgress;
    private TextView tvCurrentMax;
    private TextView tvType;
    private TextView tvWorkoutName;
    
    public LogActivityUIManager(Context context, LogUICallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    /**
     * Initialize all views and bind them to the UI manager
     */
    public void initializeViews(String chosenWorkout) {
        Activity activity = (Activity) context;
        
        // Find all UI elements
        rvShowAllWorkoutSets = activity.findViewById(R.id.rv_log_ShowAllWorkoutSets);
        lcShowWorkoutProgress = activity.findViewById(R.id.lc_log_ShowWorkoutProgression);
        tvCurrentMax = activity.findViewById(R.id.tv_log_CurrentMaxContainer);
        tvType = activity.findViewById(R.id.tv_log_type);
        tvWorkoutName = activity.findViewById(R.id.tv_log_workout_name);
        
        // Set the workout name in the header
        tvWorkoutName.setText(chosenWorkout + " Log");
        
        // Initialize the LogUIManager with the UI elements
        logUIManager = new LogUIManager(context, chosenWorkout, 
                                       rvShowAllWorkoutSets, lcShowWorkoutProgress, 
                                       tvCurrentMax, tvType);
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
                        callback.onResetWorkout();
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
                callback.onEditType(type);
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
        callback.onEditMaxValue();
    }
    
    /**
     * Show workout not started error message
     */
    public void showWorkoutNotStartedError() {
        showInfoMessage("First Start Workout");
    }
    
    /**
     * Show success message with proper dark theme styling
     */
    public void showSuccessMessage(String message) {
        showDarkToast(message);
    }
    
    /**
     * Show info message with proper dark theme styling
     */
    public void showInfoMessage(String message) {
        showDarkToast(message);
    }
    
    /**
     * Show error message with proper dark theme styling
     */
    public void showErrorMessage(String message) {
        showDarkToast(message);
    }
    
    /**
     * Show toast message with proper dark theme styling
     */
    private void showDarkToast(String message) {
        // Try to use Snackbar for better theming support
        if (context instanceof Activity) {
            View rootView = ((Activity) context).findViewById(android.R.id.content);
            if (rootView != null) {
                Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
                // Style the snackbar with dark colors
                snackbar.setActionTextColor(context.getResources().getColor(R.color.colorAccent));
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(context.getResources().getColor(R.color.background_elevated));
                snackbar.show();
                return;
            }
        }
        // Fallback to regular toast if snackbar fails
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
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
     */
    public void refreshUI() {
        // This will be called by the controller when data changes
        // The actual UI updates will be handled by LogUIManager through the controller
    }
}