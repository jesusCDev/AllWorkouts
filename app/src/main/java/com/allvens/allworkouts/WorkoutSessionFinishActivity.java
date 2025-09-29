package com.allvens.allworkouts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.StartWorkoutSession;
import com.allvens.allworkouts.data_manager.DifficultyRatingManager;
import com.allvens.allworkouts.data_manager.SessionUtils;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefsChecker;
import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.allvens.allworkouts.workout_session_manager.workouts.WorkoutGenerator;

public class WorkoutSessionFinishActivity extends AppCompatActivity{

    private int maxValue;
    private String currentChoiceWorkout;
    private String nextChoiceWorkout;
    private String sessionStartWorkout; // The workout that started this session

    private final static int PROG_INC_NEUTRAL = 1;
    private final static int PROG_INC_EASY    = 2;
    private final static int PROG_INC_HARD    = -2;

    // Keep old constants for backward compatibility with max value adjustments
    // New system will use DifficultyRatingManager for intelligent progression

    private WorkoutWrapper wrapper;
    private Button lastButtonSelected;

    @Override
    protected void onPause() {
        super.onPause();
        wrapper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wrapper.open();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Session state is cleared in goHome() when user actually completes the session
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finish);

        Button nextWorkoutButton = findViewById(R.id.btn_WorkoutFinish_NextWorkout);
        currentChoiceWorkout     = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        lastButtonSelected       = findViewById(R.id.btn_workoutFinish_LevelNeutral);
        
        // Get session start workout from intent or fallback to SharedPreferences
        sessionStartWorkout = getIntent().getStringExtra(Constants.SESSION_START_WORKOUT_KEY);
        if (sessionStartWorkout == null) {
            sessionStartWorkout = SessionUtils.getSessionStart(this);
        }
        
        // If still null, assume current workout started the session (backward compatibility)
        if (sessionStartWorkout == null) {
            sessionStartWorkout = currentChoiceWorkout;
        }

        ((TextView)findViewById(R.id.tv_workoutFinish_WorkoutName)).setText(currentChoiceWorkout);

        wrapper = new WorkoutWrapper(this);

        wrapper.open();
        
        // Debug logging for workout completion
        boolean isReal = isRealWorkoutCompletion();
        System.out.println("[DEBUG] onCreate: isRealWorkout=" + isReal + ", currentWorkout=" + currentChoiceWorkout);
        
        setNextWorkout(nextWorkoutButton);
        
        // Set up progress indicator
        updateProgressIndicator();

        WorkoutGenerator workoutGenerator = new WorkoutGenerator(wrapper.getWorkout(currentChoiceWorkout));
        Workout workout                   = workoutGenerator.getWorkout();
        WorkoutInfo workoutInfo           = workoutGenerator.getWorkoutInfo();
        maxValue                          = workoutInfo.getMax();

        wrapper.createWorkoutHistory(
                new WorkoutHistoryInfo(
                        workout.getWorkoutValue(0),
                        workout.getWorkoutValue(1),
                        workout.getWorkoutValue(2),
                        workout.getWorkoutValue(3),
                        workout.getWorkoutValue(4),
                        maxValue
                ),
                workoutInfo.getId()
        );

        workoutInfo.setMax((workoutInfo.getMax() + PROG_INC_NEUTRAL));
        workoutInfo.setProgress((workoutInfo.getProgress() + 1));
        wrapper.updateWorkout(workoutInfo);
    }

    private void setNextWorkout(Button nextWorkoutButton) {
        WorkoutPosAndStatus[] workouts = new WorkoutBasicsPrefsChecker(this).getWorkoutPositions(false);
        
        // Convert to list of workout names for easier linear logic
        java.util.List<String> enabledWorkouts = new java.util.ArrayList<>();
        for (WorkoutPosAndStatus workout : workouts) {
            enabledWorkouts.add(workout.getName());
        }
        
        // Use linear logic to determine the next workout
        LinearWorkoutResult result = getNextWorkout(currentChoiceWorkout, enabledWorkouts, sessionStartWorkout);
        
        if (result.hasNext) {
            // Show next workout button
            showNextWorkoutButton(nextWorkoutButton, result.nextWorkout);
        } else {
            // Show complete session button
            showCompleteSessionButton(nextWorkoutButton);
        }
        
        // Debug logging to help understand workout sequence
        System.out.println("[WorkoutFinish] Current: " + currentChoiceWorkout + 
                          ", Next: " + (result.hasNext ? result.nextWorkout : "Complete Session") +
                          ", Total enabled: " + enabledWorkouts.size());
    }
    
    /**
     * Helper class to store the result of linear workout calculation
     */
    private static class LinearWorkoutResult {
        boolean hasNext;
        String nextWorkout;
        
        LinearWorkoutResult(boolean hasNext, String nextWorkout) {
            this.hasNext = hasNext;
            this.nextWorkout = nextWorkout;
        }
    }
    
    /**
     * Determines the next workout using simple linear progression
     * 
     * @param currentWorkout Current workout name
     * @param enabledWorkouts List of enabled workout names in order
     * @param sessionStartWorkout The workout that started this session (not used in linear mode)
     * @return LinearWorkoutResult indicating if there's a next workout and what it is
     */
    private LinearWorkoutResult getNextWorkout(String currentWorkout, java.util.List<String> enabledWorkouts, String sessionStartWorkout) {
        // Handle edge cases
        if (enabledWorkouts.isEmpty()) {
            return new LinearWorkoutResult(false, null);
        }
        
        if (enabledWorkouts.size() == 1) {
            // Only one workout enabled, always complete after finishing it
            return new LinearWorkoutResult(false, null);
        }
        
        // Find current index
        int currentIndex = enabledWorkouts.indexOf(currentWorkout);
        
        if (currentIndex == -1) {
            // Current workout not found in enabled workouts (shouldn't happen)
            return new LinearWorkoutResult(false, null);
        }
        
        // Linear progression: if we're at the last workout, we're done
        boolean isLastWorkout = currentIndex == enabledWorkouts.size() - 1;
        
        if (isLastWorkout) {
            // No more workouts, session complete
            System.out.println("[LinearLogic] At last workout (" + currentWorkout + "), session complete");
            return new LinearWorkoutResult(false, null);
        } else {
            // Move to next workout in sequence
            String nextWorkout = enabledWorkouts.get(currentIndex + 1);
            System.out.println("[LinearLogic] Current: " + currentWorkout + ", Next: " + nextWorkout + 
                              " (" + (currentIndex + 1) + "/" + enabledWorkouts.size() + ")");
            return new LinearWorkoutResult(true, nextWorkout);
        }
    }
    
    /**
     * Determines if this is a real workout completion or just max value setting
     * We detect this by checking if there's workout history data being saved
     */
    private boolean isRealWorkoutCompletion() {
        // If we have valid workout data with sets, this is a real workout completion
        // If we're just setting max values, we won't have workout set data
        try {
            WorkoutGenerator workoutGenerator = new WorkoutGenerator(wrapper.getWorkout(currentChoiceWorkout));
            Workout workout = workoutGenerator.getWorkout();
            
            // Check if we have valid workout values (would be 0 if just setting max)
            int totalReps = 0;
            for (int i = 0; i < 5; i++) {
                totalReps += workout.getWorkoutValue(i);
            }
            
            // If total reps > 0, this is a real workout completion
            boolean isRealWorkout = totalReps > 0;
            System.out.println("[CycleTracker] Checking workout type: totalReps=" + totalReps + ", isReal=" + isRealWorkout);
            return isRealWorkout;
            
        } catch (Exception e) {
            // If there's any error, assume it's a real workout to be safe
            System.out.println("[CycleTracker] Error checking workout type, assuming real workout");
            return true;
        }
    }
    
    
    
    /**
     * Updates the progress indicator with current session progress using linear progression
     */
    private void updateProgressIndicator() {
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        TextView progressText = findViewById(R.id.tv_progress_text);
        
        if (progressBar == null || progressText == null) {
            return; // Progress indicator not available in this layout
        }
        
        // Get list of enabled workouts
        WorkoutPosAndStatus[] workouts = new WorkoutBasicsPrefsChecker(this).getWorkoutPositions(false);
        int totalWorkouts = workouts.length;
        
        // For linear progression, calculate position based on current workout index
        java.util.List<String> enabledWorkouts = new java.util.ArrayList<>();
        for (WorkoutPosAndStatus workout : workouts) {
            enabledWorkouts.add(workout.getName());
        }
        
        int currentIndex = enabledWorkouts.indexOf(currentChoiceWorkout);
        if (currentIndex == -1) {
            currentIndex = 0; // fallback
        }
        
        // Current workout position (1-based)
        int currentPosition = currentIndex + 1;
        
        System.out.println("[DEBUG] updateProgressIndicator: currentWorkout=" + currentChoiceWorkout + 
                          ", position=" + currentPosition + ", totalWorkouts=" + totalWorkouts);
        
        // Calculate progress percentage (0-100)
        int progressPercentage = totalWorkouts > 0 ? (currentPosition * 100) / totalWorkouts : 0;
        
        // Ensure we don't exceed 100%
        progressPercentage = Math.min(progressPercentage, 100);
        
        // Update progress bar
        progressBar.setProgress(progressPercentage);
        
        // Update progress text
        String progressTextString = currentPosition + " of " + totalWorkouts + " workouts completed";
        progressText.setText(progressTextString);
        
        // Debug logging
        System.out.println("[ProgressIndicator] " + progressTextString + " (" + progressPercentage + "%)");
    }
    
    /**
     * Shows the next workout button and configures Done button for non-final workout
     */
    private void showNextWorkoutButton(Button nextWorkoutButton, String nextWorkout) {
        nextChoiceWorkout = nextWorkout;
        nextWorkoutButton.setText(nextWorkout);
        nextWorkoutButton.setVisibility(View.VISIBLE);
        
        // Configure Done button as secondary action (stays in top-left)
        Button doneButton = findViewById(R.id.button2);
        if(doneButton != null) {
            doneButton.setText("←");
            doneButton.setBackgroundResource(R.drawable.bg_button_secondary);
            doneButton.setTextColor(this.getResources().getColor(R.color.selectedButton));
            doneButton.setElevation(2f);
        }
    }
    
    /**
     * Changes the next workout button to a "Complete Session" button for session completion
     */
    private void showCompleteSessionButton(Button nextWorkoutButton) {
        nextChoiceWorkout = null;
        
        // Change the Next Workout button to Complete Session button
        nextWorkoutButton.setText("✅ Complete Session");
        nextWorkoutButton.setVisibility(View.VISIBLE);
        
        // Keep the Done button (back arrow) as normal secondary action
        Button doneButton = findViewById(R.id.button2);
        if(doneButton != null) {
            doneButton.setText("←");
            doneButton.setBackgroundResource(R.drawable.bg_button_secondary);
            doneButton.setTextColor(this.getResources().getColor(R.color.selectedButton));
            doneButton.setElevation(2f);
        }
    }

    private void updateWorkoutProgress(int progress){
        WorkoutInfo workout = wrapper.getWorkout(currentChoiceWorkout);
        
        // Update max value using old system for compatibility
        int value = maxValue + progress;
        if(value <= 0) {
            value = 1;
        }
        workout.setMax(value);
        
        // Update difficulty rating using new Elo-like system
        updateDifficultyRating(workout, progress);
        
        wrapper.updateWorkout(workout);
    }
    
    /**
     * Updates difficulty rating based on user feedback using Elo-like algorithm
     */
    private void updateDifficultyRating(WorkoutInfo workout, int progressFeedback) {
        int currentRating = workout.getDifficultyRating();
        int feedback;
        
        // Convert old progress values to new feedback system
        if (progressFeedback == PROG_INC_EASY) {
            feedback = DifficultyRatingManager.FEEDBACK_TOO_EASY;
        } else if (progressFeedback == PROG_INC_HARD) {
            feedback = DifficultyRatingManager.FEEDBACK_TOO_HARD;
        } else {
            feedback = DifficultyRatingManager.FEEDBACK_JUST_RIGHT;
        }
        
        int newRating = DifficultyRatingManager.calculateNewRating(currentRating, feedback);
        workout.setDifficultyRating(newRating);
        
        // Log the rating change for debugging
        DifficultyRatingManager.logRatingChange(
            workout.getWorkout(), 
            currentRating, 
            newRating, 
            feedback
        );
    }

    public void setDifficulty(View view) {
        // Reset previous selection to secondary style
        if (lastButtonSelected != null) {
            setButtonStyle(lastButtonSelected, false);
        }
        
        // Set current selection to primary style
        Button currentButton = (Button) view;
        setButtonStyle(currentButton, true);
        lastButtonSelected = currentButton;

        switch((view).getId()){
            case R.id.btn_workoutFinish_LevelHard:
                updateWorkoutProgress(PROG_INC_HARD);
                break;

            case R.id.btn_workoutFinish_LevelNeutral:
                updateWorkoutProgress(PROG_INC_NEUTRAL);
                break;

            case R.id.btn_workoutFinish_LevelEasy:
                updateWorkoutProgress(PROG_INC_EASY);
                break;
        }
    }
    
    private void setButtonStyle(Button button, boolean isSelected) {
        if (isSelected) {
            // Selected state - primary style
            button.setBackgroundResource(R.drawable.bg_button_primary);
            button.setTextColor(this.getResources().getColor(R.color.background_dark));
            button.setElevation(8f);
        } else {
            // Unselected state - secondary style  
            button.setBackgroundResource(R.drawable.bg_button_secondary);
            button.setTextColor(this.getResources().getColor(R.color.selectedButton));
            button.setElevation(2f);
        }
    }

    public void startNextWorkout(View view) {
        if(nextChoiceWorkout != null) {
            // Start the next workout
            new StartWorkoutSession().startWorkout(this, nextChoiceWorkout);
        } else {
            // This is the Complete Session button - go home
            goHome(view);
        }
    }

    public void goHome(View view) {
        // Clear session state when user goes home
        SessionUtils.clearSession(this);
        startActivity(new Intent(this, MainActivity.class));
    }
}
