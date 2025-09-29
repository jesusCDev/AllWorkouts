package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.StartWorkoutSession;
import com.allvens.allworkouts.managers.WorkoutSelectionManager;
import com.allvens.allworkouts.ui.MainActivityUIManager;
import com.allvens.allworkouts.ui.WorkoutCalendarView;

public class MainActivity extends AppCompatActivity 
    implements MainActivityUIManager.UIEventListener, WorkoutSelectionManager.DataEventListener {

    // Manager classes for separation of concerns
    private MainActivityUIManager uiManager;
    private WorkoutSelectionManager workoutManager;
    private WorkoutCalendarView workoutCalendar;

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
        
        // Refresh calendar to show latest workout data
        if (workoutCalendar != null) {
            workoutCalendar.refreshCalendar();
        }
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
}
