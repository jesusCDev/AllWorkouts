package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.base.BaseInterfaces;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.managers.WorkoutSessionController;
import com.allvens.allworkouts.managers.WorkoutSessionDataManager;
import com.allvens.allworkouts.ui.WorkoutSessionActivityUIManager;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;

public class WorkoutSessionActivity extends AppCompatActivity
    implements WorkoutSessionActivityUIManager.WorkoutSessionUICallback,
               WorkoutSessionDataManager.WorkoutSessionDataCallback,
               WorkoutSessionController.WorkoutSessionControllerCallback,
               BaseInterfaces.BaseUICallback {

    // Managers
    private WorkoutSessionActivityUIManager uiManager;
    private WorkoutSessionDataManager dataManager;
    private WorkoutSessionController sessionController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);

        // Initialize managers
        uiManager = new WorkoutSessionActivityUIManager(this, this);
        dataManager = new WorkoutSessionDataManager(this, this);
        sessionController = new WorkoutSessionController(this, this);
        
        // Setup UI
        uiManager.initializeViews();
        uiManager.setupMediaControls();
        
        // Initialize session with data from intent
        String sessionStartWorkout = getIntent().getStringExtra(Constants.SESSION_START_WORKOUT_KEY);
        dataManager.initializeSession(getIntent().getExtras(), sessionStartWorkout);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sessionController.stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiManager.onResume();
        sessionController.resumeSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiManager.onPause();
        sessionController.pauseSession();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiManager.onDestroy();
        sessionController.cleanup();
        dataManager.cleanup();
    }

    /****************************************
     /**** BUTTON ACTIONS (Activity Methods)
     ****************************************/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        uiManager.handleSessionExit();
        return true;
    }

    /**
     * Switches Screen between - Workout and Timer
     * @param view
     */
    public void btnActionChangeActivities(View view) {
        uiManager.handleScreenChangeClick();
    }
    
    /****************************************
     /**** INTERFACE IMPLEMENTATIONS
     ****************************************/
    
    // WorkoutSessionUICallback implementations
    @Override
    public void onWorkoutSessionComplete() {
        dataManager.handleSessionCompletion();
    }
    
    @Override
    public void onScreenChange() {
        sessionController.handleScreenChange();
    }
    
    @Override
    public void onSessionExit() {
        sessionController.stopTimer();
        dataManager.handleSessionExit();
    }
    
    // WorkoutSessionDataCallback implementations
    @Override
    public void onWorkoutDataLoaded(Workout workout, WorkoutInfo workoutInfo) {
        // Initialize session controller with workout data and UI elements
        WorkoutSessionActivityUIManager.UIElements uiElements = uiManager.getUIElements();
        sessionController.initializeSession(workout, workoutInfo, uiElements);
    }
    
    @Override
    public void onSessionStarted(String workoutName) {
        // Session has started successfully
    }
    
    public void onSessionError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
    
    public void onNavigationRequested(Intent intent, boolean finishCurrent) {
        android.util.Log.d("WorkoutSession", "Activity.onNavigationRequested() called");
        android.util.Log.d("WorkoutSession", "Intent target: " + intent.getComponent());
        android.util.Log.d("WorkoutSession", "finishCurrent: " + finishCurrent);
        startActivity(intent);
        android.util.Log.d("WorkoutSession", "startActivity() called");
        if (finishCurrent) {
            finish();
            android.util.Log.d("WorkoutSession", "finish() called - activity should close");
        }
    }
    
    // BaseDataCallback implementations
    @Override
    public void onDataLoaded() {
        // Data loaded successfully
    }
    
    @Override
    public void onDataUpdated() {
        // Data updated successfully
    }
    
    @Override
    public void onDataError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
    
    // BaseUICallback implementations
    public void onShowMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    public void onShowError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
    
    // WorkoutSessionControllerCallback implementations
    @Override
    public void onWorkoutComplete() {
        // Individual workout set completed
    }
    
    @Override
    public void onSessionComplete() {
        android.util.Log.d("WorkoutSession", "Activity.onSessionComplete() called");
        dataManager.handleSessionCompletion();
        android.util.Log.d("WorkoutSession", "Activity.onSessionComplete() - handleSessionCompletion() called");
    }
    
    @Override
    public void onTimerStateChanged(boolean isRunning) {
        // Timer state changed - could be used for UI updates if needed
    }
    
    @Override
    public void onProgressChanged(int progress) {
        // Workout progress changed - could be used for analytics
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
