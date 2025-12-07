package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.StartWorkoutSession;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.managers.WorkoutSelectionManager;
import com.allvens.allworkouts.ui.MainActivityUIManager;
import com.allvens.allworkouts.ui.WorkoutCalendarView;

import java.util.List;

public class MainActivity extends AppCompatActivity 
    implements MainActivityUIManager.UIEventListener, WorkoutSelectionManager.DataEventListener {

    // Manager classes for separation of concerns
    private MainActivityUIManager uiManager;
    private WorkoutSelectionManager workoutManager;
    private WorkoutCalendarView workoutCalendar;
    
    // Stats views
    private TextView tvStatMonth;
    private TextView tvStatStreak;

    /* ====================================================================== */
    /*  LIFECYCLE                                                             */
    /* ====================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeManagers();
        setupUI();
        loadInitialData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Refresh workout data
        workoutManager.refreshAvailableWorkouts();
        
        // Refresh calendar to show latest workout data with force refresh
        if (workoutCalendar != null) {
            workoutCalendar.forceRefresh();
            // Additional post-delay refresh to ensure proper layout
            workoutCalendar.postDelayed(() -> {
                workoutCalendar.forceRefresh();
            }, 100);
        }
        
        // Update stats
        updateWorkoutStats();
    }

    /* ====================================================================== */
    /*  INITIALIZATION METHODS                                                 */
    /* ====================================================================== */
    
    private void initializeManagers() {
        uiManager = new MainActivityUIManager(this);
        workoutManager = new WorkoutSelectionManager(this);
        
        // Set up event listeners
        uiManager.setEventListener(this);
        workoutManager.setEventListener(this);
    }
    
    private void setupUI() {
        // Bind views and set up UI
        uiManager.bindViews(findViewById(android.R.id.content));
        workoutCalendar = findViewById(R.id.calendar_workout_activity);
        
        // Bind stats views
        tvStatMonth = findViewById(R.id.tv_stat_month);
        tvStatStreak = findViewById(R.id.tv_stat_streak);
    }
    
    private void loadInitialData() {
        // Load initial workout data
        workoutManager.refreshAvailableWorkouts();
    }

    /* ====================================================================== */
    /*  PUBLIC CLICK HANDLERS  (referenced from XML)                          */
    /* ====================================================================== */


    public void onStartWorkoutClicked(View view) {
        String selectedWorkout = workoutManager.getSelectedWorkout();
        if (selectedWorkout == null) return;

        new StartWorkoutSession().startWorkout(this, selectedWorkout);
    }

    public void onSettingsClicked(View view) {
        uiManager.closeWorkoutChooser();
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onLogClicked(View view) {
        String selectedWorkout = workoutManager.getSelectedWorkout();
        if (selectedWorkout == null) return;

        Intent intent = new Intent(this, LogActivity.class);
        intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, selectedWorkout);
        startActivity(intent);
    }

    /* ====================================================================== */
    /*  UI EVENT LISTENER IMPLEMENTATIONS                                     */
    /* ====================================================================== */
    
    @Override
    public void onWorkoutSelected(String workoutName) {
        // Handle workout selection from UI
        workoutManager.selectWorkout(workoutName);
    }
    
    @Override
    public void onChooserToggleRequested() {
        // Handle chooser toggle request - delegate to UI manager with current workout data
        uiManager.toggleWorkoutChooser(workoutManager.getAvailableWorkouts());
    }
    
    /* ====================================================================== */
    /*  DATA EVENT LISTENER IMPLEMENTATIONS                                   */
    /* ====================================================================== */
    
    @Override
    public void onWorkoutsRefreshed(String[] workouts) {
        // Handle updated workout list - ensure valid selection
        workoutManager.ensureValidSelection();
    }
    
    @Override
    public void onWorkoutSelectionChanged(String workout) {
        // Handle workout selection from data manager
        uiManager.updateSelectedWorkout(workout);
    }
    
    @Override
    public void onError(String error) {
        // Handle data errors - could show toast or log
        // For now, silently handle errors
        android.util.Log.w("MainActivity", "Workout selection error: " + error);
    }
    
    /* ====================================================================== */
    /*  STATS CALCULATION                                                     */
    /* ====================================================================== */
    
    /**
     * Calculate and update workout statistics
     * Note: Since WorkoutHistoryInfo doesn't store dates, we show total session counts
     */
    private void updateWorkoutStats() {
        WorkoutWrapper workoutWrapper = new WorkoutWrapper(this);
        try {
            workoutWrapper.open();
            
            // Get all workouts
            List<WorkoutInfo> allWorkouts = workoutWrapper.getAllWorkouts();
            
            // Count total workout sessions across all workout types
            int totalSessions = 0;
            int bestProgress = 0;
            
            for (WorkoutInfo workout : allWorkouts) {
                // Count workout history entries for this workout
                List<com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo> history = 
                    workoutWrapper.getHistoryForWorkout(workout.getId());
                totalSessions += history.size();
                
                // Track highest progress level across all workouts
                if (workout.getProgress() > bestProgress) {
                    bestProgress = workout.getProgress();
                }
            }
            
            // Update UI
            if (tvStatMonth != null) {
                tvStatMonth.setText(String.valueOf(totalSessions));
            }
            
            if (tvStatStreak != null) {
                tvStatStreak.setText(String.valueOf(bestProgress));
            }
            
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error calculating workout stats", e);
        } finally {
            workoutWrapper.close();
        }
    }
}
