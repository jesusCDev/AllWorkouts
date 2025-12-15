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
    private TextView tvStatCurrentStreak;
    private TextView tvStatMaxStreak;
    private TextView tvTodayCompletion;

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
        tvStatCurrentStreak = findViewById(R.id.tv_stat_current_streak);
        tvStatMaxStreak = findViewById(R.id.tv_stat_max_streak);
        tvTodayCompletion = findViewById(R.id.tv_today_completion);
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
        // Handle chooser toggle request - delegate to UI manager with current workout data and completion status
        String[] availableWorkouts = workoutManager.getAvailableWorkouts();
        java.util.Set<String> completedToday = getCompletedWorkoutsToday();
        uiManager.toggleWorkoutChooser(availableWorkouts, completedToday);
    }
    
    /* ====================================================================== */
    /*  DATA EVENT LISTENER IMPLEMENTATIONS                                   */
    /* ====================================================================== */
    
    @Override
    public void onWorkoutsRefreshed(String[] workouts) {
        // Handle updated workout list - ensure valid selection, preferring uncompleted workouts
        java.util.Set<String> completedToday = getCompletedWorkoutsToday();
        workoutManager.ensureValidSelection(completedToday);
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
     * Calculate and update workout statistics with date-based streaks
     */
    private void updateWorkoutStats() {
        WorkoutWrapper workoutWrapper = new WorkoutWrapper(this);
        try {
            workoutWrapper.open();
            
            // Get all workouts
            List<WorkoutInfo> allWorkouts = workoutWrapper.getAllWorkouts();
            
            // Count total workout sessions across all workout types
            int totalSessions = 0;
            for (WorkoutInfo workout : allWorkouts) {
                List<com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo> history = 
                    workoutWrapper.getHistoryForWorkout(workout.getId());
                totalSessions += history.size();
            }
            
            // Calculate streaks based on actual completion dates
            int[] streaks = calculateStreaks(workoutWrapper);
            int currentStreak = streaks[0];
            int maxStreak = streaks[1];
            
            // Calculate today's completion
            String todayCompletion = calculateTodayCompletion(workoutWrapper, allWorkouts.size());
            
            // Update UI
            if (tvStatMonth != null) {
                tvStatMonth.setText(String.valueOf(totalSessions));
            }
            
            if (tvStatCurrentStreak != null) {
                tvStatCurrentStreak.setText(String.valueOf(currentStreak));
            }
            
            if (tvStatMaxStreak != null) {
                tvStatMaxStreak.setText("• 🔥 " + maxStreak + " best");
            }
            
            if (tvTodayCompletion != null && todayCompletion != null && !todayCompletion.isEmpty()) {
                tvTodayCompletion.setText(todayCompletion);
                tvTodayCompletion.setVisibility(android.view.View.VISIBLE);
            } else if (tvTodayCompletion != null) {
                tvTodayCompletion.setVisibility(android.view.View.GONE);
            }
            
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error calculating workout stats", e);
        } finally {
            workoutWrapper.close();
        }
    }
    
    /**
     * Calculate current and max streak from workout history dates
     * @return int array [currentStreak, maxStreak]
     */
    private int[] calculateStreaks(WorkoutWrapper workoutWrapper) {
        int currentStreak = 0;
        int maxStreak = 0;
        
        try {
            // Get today's date at midnight
            java.util.Calendar today = java.util.Calendar.getInstance();
            today.set(java.util.Calendar.HOUR_OF_DAY, 0);
            today.set(java.util.Calendar.MINUTE, 0);
            today.set(java.util.Calendar.SECOND, 0);
            today.set(java.util.Calendar.MILLISECOND, 0);
            
            // Check backwards from today for current streak
            java.util.Calendar checkDay = (java.util.Calendar) today.clone();
            while (true) {
                long dayStart = checkDay.getTimeInMillis() / 1000;
                long dayEnd = dayStart + (24 * 60 * 60) - 1;
                
                int count = workoutWrapper.getWorkoutCountForDay(dayStart, dayEnd);
                if (count > 0) {
                    currentStreak++;
                    checkDay.add(java.util.Calendar.DAY_OF_MONTH, -1);
                } else {
                    break;
                }
                
                // Prevent infinite loop - limit to 365 days
                if (currentStreak >= 365) break;
            }
            
            // Calculate max streak by checking all dates in history
            // Get earliest workout date
            long earliestDate = Long.MAX_VALUE;
            List<WorkoutInfo> allWorkouts = workoutWrapper.getAllWorkouts();
            for (WorkoutInfo workout : allWorkouts) {
                List<com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo> history = 
                    workoutWrapper.getHistoryForWorkout(workout.getId());
                for (com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo h : history) {
                    if (h.getCompletionDate() > 0 && h.getCompletionDate() < earliestDate) {
                        earliestDate = h.getCompletionDate();
                    }
                }
            }
            
            // Only calculate max streak if we have valid dates
            if (earliestDate != Long.MAX_VALUE) {
                java.util.Calendar scanDay = java.util.Calendar.getInstance();
                scanDay.setTimeInMillis(earliestDate * 1000);
                scanDay.set(java.util.Calendar.HOUR_OF_DAY, 0);
                scanDay.set(java.util.Calendar.MINUTE, 0);
                scanDay.set(java.util.Calendar.SECOND, 0);
                scanDay.set(java.util.Calendar.MILLISECOND, 0);
                
                int tempStreak = 0;
                while (scanDay.getTimeInMillis() <= today.getTimeInMillis()) {
                    long dayStart = scanDay.getTimeInMillis() / 1000;
                    long dayEnd = dayStart + (24 * 60 * 60) - 1;
                    
                    int count = workoutWrapper.getWorkoutCountForDay(dayStart, dayEnd);
                    if (count > 0) {
                        tempStreak++;
                        maxStreak = Math.max(maxStreak, tempStreak);
                    } else {
                        tempStreak = 0;
                    }
                    
                    scanDay.add(java.util.Calendar.DAY_OF_MONTH, 1);
                }
            }
            
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error calculating streaks", e);
        }
        
        return new int[]{currentStreak, maxStreak};
    }
    
    /**
     * Calculate today's workout completion status
     * @return String like "2/4 completed today" or empty if none completed
     */
    private String calculateTodayCompletion(WorkoutWrapper workoutWrapper, int totalWorkouts) {
        try {
            // Get today's date range
            java.util.Calendar today = java.util.Calendar.getInstance();
            today.set(java.util.Calendar.HOUR_OF_DAY, 0);
            today.set(java.util.Calendar.MINUTE, 0);
            today.set(java.util.Calendar.SECOND, 0);
            today.set(java.util.Calendar.MILLISECOND, 0);
            long dayStart = today.getTimeInMillis() / 1000;
            long dayEnd = dayStart + (24 * 60 * 60) - 1;
            
            // Get unique workout types completed today
            int completedToday = workoutWrapper.getUniqueWorkoutIdsForDay(dayStart, dayEnd).size();
            
            if (completedToday > 0) {
                return "✓ " + completedToday + "/" + totalWorkouts + " completed today";
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error calculating today's completion", e);
        }
        
        return "";
    }
    
    /**
     * Get set of workout names completed today
     * @return Set of workout names that were completed today
     */
    private java.util.Set<String> getCompletedWorkoutsToday() {
        java.util.Set<String> completedWorkouts = new java.util.HashSet<>();
        WorkoutWrapper workoutWrapper = new WorkoutWrapper(this);
        
        try {
            workoutWrapper.open();
            
            // Get today's date range
            java.util.Calendar today = java.util.Calendar.getInstance();
            today.set(java.util.Calendar.HOUR_OF_DAY, 0);
            today.set(java.util.Calendar.MINUTE, 0);
            today.set(java.util.Calendar.SECOND, 0);
            today.set(java.util.Calendar.MILLISECOND, 0);
            long dayStart = today.getTimeInMillis() / 1000;
            long dayEnd = dayStart + (24 * 60 * 60) - 1;
            
            // Get unique workout IDs completed today
            java.util.Set<Long> completedIds = workoutWrapper.getUniqueWorkoutIdsForDay(dayStart, dayEnd);
            
            // Convert IDs to workout names
            List<WorkoutInfo> allWorkouts = workoutWrapper.getAllWorkouts();
            for (WorkoutInfo workout : allWorkouts) {
                if (completedIds.contains(workout.getId())) {
                    completedWorkouts.add(workout.getWorkout());
                }
            }
            
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error getting completed workouts", e);
        } finally {
            workoutWrapper.close();
        }
        
        return completedWorkouts;
    }
}
