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
        
        // Set up timer
        timer = new Timer(workoutSessionUIManager);
        
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
            if (isSessionComplete()) {
                // Session is complete, notify callback
                callback.onSessionComplete();
            } else {
                // Continue with screen update
                updateScreen();
            }
        } catch (Exception e) {
            callback.onError("Error handling screen change: " + e.getMessage());
        }
    }
    
    /**
     * Check if the current workout set is finished and session can continue
     */
    private boolean isSessionComplete() {
        // If timer is not running, increment progress
        if (timer != null && !timer.get_TimerRunning()) {
            workoutSessionUIManager.setProgress(workoutSessionUIManager.getProgress() + 1);
            currentProgress = workoutSessionUIManager.getProgress();
            callback.onProgressChanged(currentProgress);
        }
        
        // Session continues if we haven't completed all 5 sets
        return currentProgress >= 5;
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