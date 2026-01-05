package com.allvens.allworkouts.managers;

import android.content.Context;

import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.ui.WorkoutSessionActivityUIManager;
import com.allvens.allworkouts.workout_session_manager.Timer;
import com.allvens.allworkouts.workout_session_manager.WorkoutSessionUIManager;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;

/**
 * Controller for managing workout session flow and logic
 * Handles workout progression, timer coordination, and session state management
 */
public class WorkoutSessionController {
    
    public interface WorkoutSessionControllerCallback {
        void onWorkoutComplete();
        void onSessionComplete();
        void onTimerStateChanged(boolean isRunning);
        void onProgressChanged(int progress);
        void onError(String error);
    }
    
    private Context context;
    private WorkoutSessionControllerCallback callback;
    
    // Core components
    private Workout workout;
    private WorkoutInfo workoutInfo;
    private WorkoutSessionUIManager workoutSessionUIManager;
    private Timer timer;
    
    // Session state
    private int currentProgress = 0;
    private boolean sessionActive = false;
    
    public WorkoutSessionController(Context context, WorkoutSessionControllerCallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    /**
     * Initialize the session with workout data and UI elements
     */
    public void initializeSession(Workout workout, WorkoutInfo workoutInfo, 
                                 WorkoutSessionActivityUIManager.UIElements uiElements) {
        this.workout = workout;
        this.workoutInfo = workoutInfo;
        this.sessionActive = true;
        this.currentProgress = 0;
        
        android.util.Log.d("WorkoutSession", "Session initialized with workout: " + workoutInfo.getWorkout());
        android.util.Log.d("WorkoutSession", "Starting progress: " + currentProgress + "/5");
        
        // Create the workout session UI manager
        workoutSessionUIManager = new WorkoutSessionUIManager(
            context, workout,
            uiElements.tvWorkoutName,
            uiElements.cTimerRepsWorkoutHolder,
            uiElements.ivWorkoutImageHolder,
            uiElements.tvTimerHolder,
            uiElements.tvFront,
            uiElements.tvBack,
            uiElements.tvValue1, uiElements.tvValue2, uiElements.tvValue3,
            uiElements.tvValue4, uiElements.tvValue5,
            uiElements.btnChangeScreens
        );
        
        // Synchronize progress between controller and UI manager
        workoutSessionUIManager.setProgress(currentProgress);
        
        // Set up timer
        timer = new Timer(workoutSessionUIManager);

        // Set up extra break callback
        workoutSessionUIManager.setExtraBreakCallback(seconds -> {
            if (timer != null && timer.get_TimerRunning()) {
                timer.addExtraTime(seconds);
            }
        });

        // Start with workout screen
        startWorkoutScreen();
    }
    
    /**
     * Start the initial workout screen display
     */
    public void startWorkoutScreen() {
        if (workoutSessionUIManager != null) {
            workoutSessionUIManager.changeScreenToWorkout();
        }
    }
    
    /**
     * Handle screen change request (Complete/Next button pressed)
     */
    public void handleScreenChange() {
        if (!sessionActive) {
            return;
        }
        
        try {
            android.util.Log.d("WorkoutSession", "handleScreenChange called. Current progress: " + currentProgress + "/5");
            
            // Check if we're at the 5th set and timer is not running (completing final set)
            if (currentProgress == 4 && (timer == null || !timer.get_TimerRunning())) {
                android.util.Log.d("WorkoutSession", "Completing final set (5/5)");
                // This is the final set completion - go directly to session complete
                currentProgress = 5;
                callback.onSessionComplete();
                return;
            }
            
            if (isSessionComplete()) {
                // Session is complete, notify callback
                android.util.Log.d("WorkoutSession", "Session marked as complete by isSessionComplete()");
                android.util.Log.d("WorkoutSession", "Calling callback.onSessionComplete()");
                callback.onSessionComplete();
                android.util.Log.d("WorkoutSession", "callback.onSessionComplete() called successfully");
            } else {
                // Continue with screen update
                android.util.Log.d("WorkoutSession", "Continuing with screen update");
                updateScreen();
            }
        } catch (Exception e) {
            android.util.Log.e("WorkoutSession", "Error handling screen change: " + e.getMessage());
            callback.onError("Error handling screen change: " + e.getMessage());
        }
    }
    
    /**
     * Check if the current workout set is finished and session can continue
     */
    private boolean isSessionComplete() {
        // If timer is not running, we're completing a set
        if (timer != null && !timer.get_TimerRunning()) {
            // Only increment progress if we haven't already counted this completion
            // Progress should be 0-4, and we increment to 1-5
            if (currentProgress < 5) {
                currentProgress++;
                // Update the UI manager to match our progress
                workoutSessionUIManager.setProgress(currentProgress);
                callback.onProgressChanged(currentProgress);
                
                // Debug logging to help track progress
                android.util.Log.d("WorkoutSession", "Set completed. Progress: " + currentProgress + "/5");
            }
        }
        
        // Check if we've completed all 5 sets
        boolean sessionComplete = currentProgress >= 5;
        if (sessionComplete) {
            android.util.Log.d("WorkoutSession", "Session complete! Progress: " + currentProgress + "/5");
        }
        return sessionComplete;
    }
    
    /**
     * Update screen based on current state (workout or timer)
     */
    private void updateScreen() {
        if (timer.get_TimerRunning()) {
            // Timer is running, switch to workout screen
            timer.stop_timer();
            workoutSessionUIManager.changeScreenToWorkout();
            callback.onTimerStateChanged(false);
        } else {
            // Switch to timer screen and start countdown
            workoutSessionUIManager.changeScreenToTimer();
            int breakTime = workout.get_BreakTime(workoutSessionUIManager.getProgress());
            timer.create_timer(breakTime);
            timer.start_timer();
            callback.onTimerStateChanged(true);
        }
    }
    
    /**
     * Kill/stop the timer (called on activity destroy or back press)
     */
    public void stopTimer() {
        if (timer != null && timer.get_TimerRunning()) {
            timer.stop_timer();
            callback.onTimerStateChanged(false);
        }
    }
    
    /**
     * Skip to next workout set (triggered by gesture)
     * Follows normal flow: workout → timer → workout
     */
    public void skipToNextSet() {
        if (!sessionActive) {
            android.util.Log.w("WorkoutSession", "Cannot skip - session not active");
            return;
        }
        
        // Check if we're already at the final set
        if (currentProgress >= 5) {
            android.util.Log.w("WorkoutSession", "Cannot skip - already at final set");
            return;
        }
        
        android.util.Log.d("WorkoutSession", "Skipping from progress: " + currentProgress);
        
        // Determine current state
        boolean isTimerRunning = timer != null && timer.get_TimerRunning();
        
        if (isTimerRunning) {
            // Currently on timer screen - skip timer and go to next workout
            android.util.Log.d("WorkoutSession", "Skipping timer, moving to workout");
            timer.stop_timer();
            callback.onTimerStateChanged(false);
            workoutSessionUIManager.changeScreenToWorkout();
        } else {
            // Currently on workout screen - complete this set and go to timer
            android.util.Log.d("WorkoutSession", "Completing set, moving to timer");
            
            // Increment progress (completing current set)
            currentProgress++;
            workoutSessionUIManager.setProgress(currentProgress);
            callback.onProgressChanged(currentProgress);
            
            android.util.Log.d("WorkoutSession", "Progress after skip: " + currentProgress + "/5");
            
            // Check if we've completed all 5 sets
            if (currentProgress >= 5) {
                android.util.Log.d("WorkoutSession", "All sets complete - finishing session");
                callback.onSessionComplete();
            } else {
                // Start timer for break before next set
                android.util.Log.d("WorkoutSession", "Starting break timer");
                workoutSessionUIManager.changeScreenToTimer();
                int breakTime = workout.get_BreakTime(currentProgress);
                timer.create_timer(breakTime);
                timer.start_timer();
                callback.onTimerStateChanged(true);
            }
        }
    }
    
    /**
     * Get current session progress (0-5)
     */
    public int getCurrentProgress() {
        return currentProgress;
    }
    
    /**
     * Get current workout name for the progress step
     */
    public String getCurrentWorkoutName() {
        if (workout != null && currentProgress < 5) {
            return workout.get_WorkoutName(currentProgress);
        }
        return workoutInfo != null ? workoutInfo.getWorkout() : "Workout";
    }
    
    /**
     * Check if timer is currently running
     */
    public boolean isTimerRunning() {
        return timer != null && timer.get_TimerRunning();
    }
    
    /**
     * Check if session is active
     */
    public boolean isSessionActive() {
        return sessionActive;
    }
    
    /**
     * Get the workout object
     */
    public Workout getWorkout() {
        return workout;
    }
    
    /**
     * Get the workout info object
     */
    public WorkoutInfo getWorkoutInfo() {
        return workoutInfo;
    }

    /**
     * Get the pre-selected difficulty from the break slider
     */
    public int getPreSelectedDifficulty() {
        if (workoutSessionUIManager != null) {
            return workoutSessionUIManager.getSelectedDifficulty();
        }
        return com.allvens.allworkouts.data_manager.DifficultyRatingManager.FEEDBACK_JUST_RIGHT;
    }
    
    /**
     * Cleanup session resources
     */
    public void cleanup() {
        sessionActive = false;
        stopTimer();
        workout = null;
        workoutInfo = null;
        workoutSessionUIManager = null;
        timer = null;
    }
    
    /**
     * Force session completion (for external triggers)
     */
    public void forceSessionComplete() {
        sessionActive = false;
        stopTimer();
        callback.onSessionComplete();
    }
    
    /**
     * Pause session (for activity pause)
     */
    public void pauseSession() {
        // Timer continues running in background for now
        // Could be enhanced to pause timer if needed
    }
    
    /**
     * Resume session (for activity resume)
     */
    public void resumeSession() {
        // Session resumes automatically
        // Timer continues from where it left off
    }
}