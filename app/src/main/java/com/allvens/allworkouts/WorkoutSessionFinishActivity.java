package com.allvens.allworkouts;

import android.content.Intent;
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
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.data_manager.SessionUtils;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefsChecker;
import com.allvens.allworkouts.data_manager.backup.BackupManager;
import com.allvens.allworkouts.services.WorkoutForegroundService;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;
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
    private Long durationSeconds; // Duration of the workout session (null if invalid/outlier)

    // Old fixed progression constants (DEPRECATED - kept for reference)
    // private final static int PROG_INC_NEUTRAL = 1;
    // private final static int PROG_INC_EASY    = 2;
    // private final static int PROG_INC_HARD    = -2;

    // New system uses DifficultyRatingManager.calculateNewMax() for percentage-based progression
    // This provides safer, research-based progressive overload

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
        
        // Get duration from intent (only present if valid)
        if (getIntent().hasExtra(Constants.DURATION_SECONDS_KEY)) {
            durationSeconds = getIntent().getLongExtra(Constants.DURATION_SECONDS_KEY, 0);
            if (durationSeconds <= 0) {
                durationSeconds = null;
            }
            android.util.Log.d("WorkoutFinish", "Received duration: " + durationSeconds + "s");
        } else {
            durationSeconds = null;
            android.util.Log.d("WorkoutFinish", "No duration received (outlier or legacy)");
        }

        ((TextView)findViewById(R.id.tv_workoutFinish_WorkoutName)).setText(currentChoiceWorkout);

        wrapper = new WorkoutWrapper(this);

        wrapper.open();

        // Debug logging for workout completion
        boolean isReal = isRealWorkoutCompletion();
        System.out.println("[DEBUG] onCreate: isRealWorkout=" + isReal + ", currentWorkout=" + currentChoiceWorkout);

        setNextWorkout(nextWorkoutButton);

        // Update foreground notification for finish screen
        WorkoutForegroundService.updateFinish(this, nextChoiceWorkout);

        // Set up progress indicator
        updateProgressIndicator();

        // Initialize button colors
        initializeDifficultyButtons();

        // Apply pre-selected difficulty from break slider
        applyPreSelectedDifficulty();

        WorkoutGenerator workoutGenerator = new WorkoutGenerator(wrapper.getWorkout(currentChoiceWorkout));
        Workout workout                   = workoutGenerator.getWorkout();
        WorkoutInfo workoutInfo           = workoutGenerator.getWorkoutInfo();
        maxValue                          = workoutInfo.getMax();

        // Create history entry with duration (null if outlier/invalid)
        WorkoutHistoryInfo historyInfo = new WorkoutHistoryInfo(
                workout.getWorkoutValue(0),
                workout.getWorkoutValue(1),
                workout.getWorkoutValue(2),
                workout.getWorkoutValue(3),
                workout.getWorkoutValue(4),
                maxValue,
                System.currentTimeMillis() / 1000,
                durationSeconds
        );
        wrapper.createWorkoutHistory(historyInfo, workoutInfo.getId());

        android.util.Log.d("WorkoutFinish", "Saved history with duration: " + durationSeconds + "s");

        // NEW PROGRESSION SYSTEM: Don't automatically increase max every workout
        // Instead, max only changes based on explicit user feedback (Easy/Hard buttons)
        // The difficulty rating handles fine-tuning within the current max level
        // Auto-increase only happens if difficulty rating indicates consistent good performance
        int autoIncrease = DifficultyRatingManager.calculateAutoIncrease(
                workoutInfo.getMax(), workoutInfo.getDifficultyRating());
        if (autoIncrease > 0) {
            workoutInfo.setMax(workoutInfo.getMax() + autoIncrease);
            android.util.Log.d("WorkoutFinish", "Auto-increased max by " + autoIncrease +
                    " (rating: " + workoutInfo.getDifficultyRating() + ")");
        }

        workoutInfo.setProgress((workoutInfo.getProgress() + 1));
        wrapper.updateWorkout(workoutInfo);
        
        // Check if tomorrow is a max day for this workout
        showMaxTomorrowIfApplicable(workoutInfo);
    }
    
    /**
     * Check if tomorrow is a max day and show indicator if so
     */
    private void showMaxTomorrowIfApplicable(WorkoutInfo workoutInfo) {
        try {
            // After incrementing progress, check if we're one away from max (progress 7 means tomorrow is 8)
            int newProgress = workoutInfo.getProgress();
            if (newProgress == 7) {
                // Tomorrow will be max day!
                TextView tvMaxTomorrow = findViewById(R.id.tv_max_tomorrow);
                if (tvMaxTomorrow != null) {
                    tvMaxTomorrow.setVisibility(android.view.View.VISIBLE);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("WorkoutFinish", "Error checking max tomorrow: " + e.getMessage());
        }
    }

    private void setNextWorkout(Button nextWorkoutButton) {
        WorkoutPosAndStatus[] workouts = new WorkoutBasicsPrefsChecker(this).getWorkoutPositions(false);
        
        // Convert to list of workout names for easier linear logic
        java.util.List<String> enabledWorkouts = new java.util.ArrayList<>();
        for (WorkoutPosAndStatus workout : workouts) {
            enabledWorkouts.add(workout.getName());
        }
        
        // Use linear logic to determine the next workout
        LinearWorkoutResult result = getNextWorkout(currentChoiceWorkout, enabledWorkouts);
        
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
     * @return LinearWorkoutResult indicating if there's a next workout and what it is
     */
    private LinearWorkoutResult getNextWorkout(String currentWorkout, java.util.List<String> enabledWorkouts) {
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
        String progressTextString = getString(R.string.progress_format, currentPosition, totalWorkouts);
        progressText.setText(progressTextString);
        
        // Debug logging
        System.out.println("[ProgressIndicator] " + progressTextString + " (" + progressPercentage + "%)");
    }
    
    /**
     * Shows the next workout button for non-final workout
     */
    private void showNextWorkoutButton(Button nextWorkoutButton, String nextWorkout) {
        nextChoiceWorkout = nextWorkout;
        nextWorkoutButton.setText(nextWorkout);
        nextWorkoutButton.setVisibility(View.VISIBLE);
    }
    
    /**
     * Changes the next workout button to a "Complete Session" button for session completion
     */
    private void showCompleteSessionButton(Button nextWorkoutButton) {
        nextChoiceWorkout = null;
        
        // Change the Next Workout button to Complete Session button
        nextWorkoutButton.setText(getString(R.string.complete_session));
        nextWorkoutButton.setVisibility(View.VISIBLE);
    }

    private void updateWorkoutProgress(int feedbackType){
        WorkoutInfo workout = wrapper.getWorkout(currentChoiceWorkout);
        int oldMax = workout.getMax();

        // Convert legacy feedback type to new system
        int feedback;
        if (feedbackType > 0) {
            feedback = DifficultyRatingManager.FEEDBACK_TOO_EASY;
        } else if (feedbackType < 0) {
            feedback = DifficultyRatingManager.FEEDBACK_TOO_HARD;
        } else {
            feedback = DifficultyRatingManager.FEEDBACK_JUST_RIGHT;
        }

        // Use new percentage-based progression system
        int newMax = DifficultyRatingManager.calculateNewMax(oldMax, feedback);
        workout.setMax(newMax);

        // Update difficulty rating
        int oldRating = workout.getDifficultyRating();
        int newRating = DifficultyRatingManager.calculateNewRating(oldRating, feedback);
        workout.setDifficultyRating(newRating);

        // Log the changes
        String progressDesc = DifficultyRatingManager.getProgressionDescription(feedback, oldMax, newMax);
        android.util.Log.d("WorkoutFinish", "Progression update: " + progressDesc +
                ", Rating: " + oldRating + " -> " + newRating);

        DifficultyRatingManager.logRatingChange(workout.getWorkout(), oldRating, newRating, feedback);

        wrapper.updateWorkout(workout);
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

        // Use feedback types that map to new system:
        // Positive = Easy, Zero = Neutral, Negative = Hard
        int viewId = view.getId();
        if (viewId == R.id.btn_workoutFinish_LevelHard) {
            updateWorkoutProgress(-1); // Maps to FEEDBACK_TOO_HARD
        } else if (viewId == R.id.btn_workoutFinish_LevelNeutral) {
            updateWorkoutProgress(0);  // Maps to FEEDBACK_JUST_RIGHT
        } else if (viewId == R.id.btn_workoutFinish_LevelEasy) {
            updateWorkoutProgress(1);  // Maps to FEEDBACK_TOO_EASY
        }
    }
    
    /**
     * Initialize all difficulty buttons with their color-coded styling
     */
    private void initializeDifficultyButtons() {
        Button easyBtn = findViewById(R.id.btn_workoutFinish_LevelEasy);
        Button normalBtn = findViewById(R.id.btn_workoutFinish_LevelNeutral);
        Button hardBtn = findViewById(R.id.btn_workoutFinish_LevelHard);

        // Set all buttons to unselected state initially
        setButtonStyle(easyBtn, false);
        setButtonStyle(normalBtn, false);
        setButtonStyle(hardBtn, false);
    }

    private void setButtonStyle(Button button, boolean isSelected) {
        if (button == null) {
            return;
        }

        int buttonId = button.getId();

        if (isSelected) {
            // Selected state - apply difficulty-specific color
            if (buttonId == R.id.btn_workoutFinish_LevelEasy) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(R.color.difficulty_easy)));
            } else if (buttonId == R.id.btn_workoutFinish_LevelHard) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(R.color.difficulty_hard)));
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(R.color.difficulty_normal)));
            }
            button.setTextColor(getResources().getColor(R.color.background_dark));
            button.setElevation(8f);
        } else {
            // Unselected state - secondary style with subtle color hint
            if (buttonId == R.id.btn_workoutFinish_LevelEasy) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(R.color.difficulty_easy_bg)));
                button.setTextColor(getResources().getColor(R.color.difficulty_easy));
            } else if (buttonId == R.id.btn_workoutFinish_LevelHard) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(R.color.difficulty_hard_bg)));
                button.setTextColor(getResources().getColor(R.color.difficulty_hard));
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(R.color.difficulty_normal_bg)));
                button.setTextColor(getResources().getColor(R.color.difficulty_normal));
            }
            button.setElevation(2f);
        }
    }

    /**
     * Apply the pre-selected difficulty from the break slider
     * This selects the appropriate button and applies the workout progress update
     */
    private void applyPreSelectedDifficulty() {
        int preSelectedDifficulty = getIntent().getIntExtra(
                Constants.PRE_SELECTED_DIFFICULTY,
                DifficultyRatingManager.FEEDBACK_JUST_RIGHT);

        android.util.Log.d("WorkoutFinish", "Applying pre-selected difficulty: " + preSelectedDifficulty);

        Button targetButton;
        int feedbackType;

        switch (preSelectedDifficulty) {
            case DifficultyRatingManager.FEEDBACK_TOO_EASY:
                targetButton = findViewById(R.id.btn_workoutFinish_LevelEasy);
                feedbackType = 1;
                break;
            case DifficultyRatingManager.FEEDBACK_TOO_HARD:
                targetButton = findViewById(R.id.btn_workoutFinish_LevelHard);
                feedbackType = -1;
                break;
            case DifficultyRatingManager.FEEDBACK_JUST_RIGHT:
            default:
                targetButton = findViewById(R.id.btn_workoutFinish_LevelNeutral);
                feedbackType = 0;
                break;
        }

        if (targetButton != null) {
            // Reset previous selection
            if (lastButtonSelected != null) {
                setButtonStyle(lastButtonSelected, false);
            }

            // Set new selection
            setButtonStyle(targetButton, true);
            lastButtonSelected = targetButton;

            // Apply the workout progress update
            updateWorkoutProgress(feedbackType);

            android.util.Log.d("WorkoutFinish", "Pre-selected button applied: " + targetButton.getText());
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

        // Stop foreground notification service
        WorkoutForegroundService.stop(this);

        // Trigger auto-backup if enabled
        triggerAutoBackup();

        // Refresh home screen widget
        com.allvens.allworkouts.widget.WorkoutWidgetProvider.requestUpdate(this);

        startActivity(new Intent(this, MainActivity.class));
    }
    
    /**
     * Triggers automatic backup if enabled
     * Runs asynchronously to not block UI
     */
    private void triggerAutoBackup() {
        SettingsPrefsManager prefsManager = new SettingsPrefsManager(this);
        boolean autoBackupEnabled = prefsManager.getPrefSetting(PreferencesValues.AUTO_BACKUP_ENABLED);
        
        if (!autoBackupEnabled) {
            android.util.Log.d("WorkoutFinish", "Auto-backup disabled, skipping");
            return;
        }
        
        // Run backup asynchronously
        new Thread(() -> {
            try {
                BackupManager backupManager = new BackupManager(this);
                backupManager.createAutomaticBackup();
                android.util.Log.d("WorkoutFinish", "Auto-backup completed successfully");
            } catch (Exception e) {
                android.util.Log.e("WorkoutFinish", "Auto-backup failed", e);
            }
        }).start();
    }
}
