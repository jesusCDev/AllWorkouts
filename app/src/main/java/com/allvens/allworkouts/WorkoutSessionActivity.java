package com.allvens.allworkouts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.base.BaseInterfaces;
import com.allvens.allworkouts.data_manager.WorkoutDurationTracker;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.gesture.SkipWorkoutGestureHandler;
import com.allvens.allworkouts.managers.WorkoutSessionController;
import com.allvens.allworkouts.managers.WorkoutSessionDataManager;
import com.allvens.allworkouts.services.WorkoutForegroundService;
import com.allvens.allworkouts.ui.WorkoutSessionActivityUIManager;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;

public class WorkoutSessionActivity extends AppCompatActivity
    implements WorkoutSessionActivityUIManager.WorkoutSessionUICallback,
               WorkoutSessionDataManager.WorkoutSessionDataCallback,
               WorkoutSessionController.WorkoutSessionControllerCallback,
               BaseInterfaces.BaseUICallback {

    private static final int PERMISSION_REQUEST_NOTIFICATIONS = 1001;

    // Managers
    private WorkoutSessionActivityUIManager uiManager;
    private WorkoutSessionDataManager dataManager;
    private WorkoutSessionController sessionController;
    private SkipWorkoutGestureHandler gestureHandler;
    private WorkoutDurationTracker durationTracker;

    // Flag to track if we're navigating forward (to finish screen) vs exiting
    private boolean navigatingToFinish = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);

        // Initialize managers
        uiManager = new WorkoutSessionActivityUIManager(this, this);
        dataManager = new WorkoutSessionDataManager(this, this);
        sessionController = new WorkoutSessionController(this, this);
        durationTracker = new WorkoutDurationTracker(this);
        
        // Setup UI
        uiManager.initializeViews();
        uiManager.setupMediaControls();
        
        // Start duration tracking
        durationTracker.startTracking();
        
        // Initialize session with data from intent
        String sessionStartWorkout = getIntent().getStringExtra(Constants.SESSION_START_WORKOUT_KEY);
        dataManager.initializeSession(getIntent().getExtras(), sessionStartWorkout);

        // Request notification permission for Android 13+
        requestNotificationPermission();
    }

    /**
     * Request notification permission for Android 13+
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Permission result doesn't affect app functionality, service will still work
        // but notification may not show on Android 13+ without permission
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
        if (durationTracker != null) {
            durationTracker.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiManager.onPause();
        sessionController.pauseSession();
        if (durationTracker != null) {
            durationTracker.onPause();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiManager.onDestroy();
        sessionController.cleanup();
        dataManager.cleanup();
        if (gestureHandler != null) {
            gestureHandler.cleanup();
        }
        // Only stop foreground notification service if we're NOT navigating to finish screen
        if (!navigatingToFinish) {
            WorkoutForegroundService.stop(this);
        }
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
        
        // Set up skip gesture handler
        setupSkipGesture();
    }
    
    /**
     * Set up skip workout gesture (long-press on non-button areas)
     */
    private void setupSkipGesture() {
        gestureHandler = new SkipWorkoutGestureHandler(this, () -> {
            // Gesture detected - show countdown overlay
            uiManager.showSkipCountdown(
                // On countdown complete - skip to next set
                () -> {
                    if (sessionController != null) {
                        sessionController.skipToNextSet();
                    }
                    // Reset gesture state so it can be used again
                    if (gestureHandler != null) {
                        gestureHandler.resetGestureState();
                    }
                },
                // On cancel - reset gesture state
                () -> {
                    android.util.Log.d("WorkoutSession", "Skip cancelled by user");
                    // Reset gesture state so it can be used again
                    if (gestureHandler != null) {
                        gestureHandler.resetGestureState();
                    }
                }
            );
        });

        // Pass gesture handler to UI manager for cooldown control
        uiManager.setGestureHandler(gestureHandler);

        // Attach gesture handler to the workout session root layout
        android.view.View rootLayout = findViewById(R.id.workout_session_root);
        if (rootLayout != null) {
            gestureHandler.attachToView(rootLayout);
            android.util.Log.d("WorkoutSession", "Gesture handler attached to root layout");
        } else {
            android.util.Log.e("WorkoutSession", "Root layout not found!");
        }
    }
    
    @Override
    public void onSessionStarted(String workoutName) {
        // Session has started successfully
        // Start foreground notification service
        WorkoutForegroundService.start(this, workoutName, 0, 1);
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

        // Mark that we're navigating to finish screen (don't stop notification service)
        navigatingToFinish = true;

        // Get duration and pass to data manager for session completion
        long durationSeconds = 0;
        boolean isValidDuration = false;
        if (durationTracker != null && durationTracker.isTracking()) {
            durationSeconds = durationTracker.getActiveDurationSeconds();
            isValidDuration = durationTracker.isValidDuration(dataManager.getWorkoutInfo().getId());
            durationTracker.stopTracking();
        }

        // Get pre-selected difficulty from break slider
        int preSelectedDifficulty = sessionController.getPreSelectedDifficulty();

        dataManager.handleSessionCompletion(durationSeconds, isValidDuration, preSelectedDifficulty);
        android.util.Log.d("WorkoutSession", "Activity.onSessionComplete() - handleSessionCompletion() called with duration: " + durationSeconds + "s, valid: " + isValidDuration + ", difficulty: " + preSelectedDifficulty);
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

    @Override
    public void onTimerTick(int secondsRemaining) {
        // Update foreground notification with timer countdown
        WorkoutForegroundService.updateTimer(this, secondsRemaining);
    }

    @Override
    public void onWorkoutScreenChanged(String workoutName, int repCount, int setNumber) {
        // Update foreground notification with workout info
        WorkoutForegroundService.updateWorkout(this, workoutName, repCount, setNumber);
    }
}
