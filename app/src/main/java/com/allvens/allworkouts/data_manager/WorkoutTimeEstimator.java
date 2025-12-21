package com.allvens.allworkouts.data_manager;

import android.content.Context;

import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.allvens.allworkouts.workout_session_manager.workouts.WorkoutGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Calculates estimated workout duration based on historical data.
 * 
 * Features:
 * - Uses median of recent valid workout durations
 * - Scales estimate based on current rep count vs historical average
 * - Provides cold start defaults when no history exists
 * - Can estimate total session time across all enabled workouts
 */
public class WorkoutTimeEstimator {
    
    private static final String TAG = "WorkoutTimeEstimator";
    
    // Constants for cold start estimation
    private static final int SECONDS_PER_REP = 2; // Average time per rep
    private static final int DEFAULT_BREAK_SECONDS = 60; // Default break between sets
    private static final int NUM_SETS = 5;
    private static final int NUM_BREAKS = 4; // 4 breaks between 5 sets
    
    // Number of historical entries to use for estimation
    private static final int HISTORY_LIMIT = 15;
    
    private Context context;
    
    public WorkoutTimeEstimator(Context context) {
        this.context = context;
    }
    
    /**
     * Estimate duration for a single workout in seconds
     * @param workoutName Name of the workout (e.g., "Pull Ups")
     * @return Estimated duration in seconds, or 0 if unable to estimate
     */
    public long estimateWorkoutDuration(String workoutName) {
        WorkoutWrapper wrapper = new WorkoutWrapper(context);
        
        try {
            wrapper.open();
            
            WorkoutInfo workoutInfo = wrapper.getWorkout(workoutName);
            if (workoutInfo == null) {
                android.util.Log.w(TAG, "Workout not found: " + workoutName);
                return 0;
            }
            
            // Get historical durations
            List<WorkoutHistoryInfo> history = wrapper.getRecentValidDurations(workoutInfo.getId(), HISTORY_LIMIT);
            
            if (history.isEmpty()) {
                // Cold start: estimate from workout parameters
                return estimateColdStart(workoutInfo, wrapper);
            }
            
            // Calculate median duration and median reps from history
            List<Long> durations = new ArrayList<>();
            List<Integer> repCounts = new ArrayList<>();
            
            for (WorkoutHistoryInfo h : history) {
                if (h.hasValidDuration()) {
                    durations.add(h.getDurationSeconds());
                    repCounts.add(h.get_TotalReps());
                }
            }
            
            if (durations.isEmpty()) {
                return estimateColdStart(workoutInfo, wrapper);
            }
            
            long medianDuration = calculateMedianLong(durations);
            int medianReps = calculateMedianInt(repCounts);
            
            // Get current workout's expected total reps
            WorkoutGenerator generator = new WorkoutGenerator(workoutInfo);
            Workout workout = generator.getWorkout();
            int currentTotalReps = 0;
            for (int i = 0; i < NUM_SETS; i++) {
                currentTotalReps += workout.getWorkoutValue(i);
            }
            
            // Scale duration based on rep count ratio
            if (medianReps > 0 && currentTotalReps > 0) {
                double scaleFactor = (double) currentTotalReps / medianReps;
                long scaledDuration = (long) (medianDuration * scaleFactor);
                android.util.Log.d(TAG, "Estimated " + workoutName + ": " + scaledDuration + "s (scaled from " + medianDuration + "s by factor " + scaleFactor + ")");
                return scaledDuration;
            }
            
            return medianDuration;
            
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error estimating duration for " + workoutName, e);
            return 0;
        } finally {
            wrapper.close();
        }
    }
    
    /**
     * Estimate total session duration for all enabled workouts
     * @param enabledWorkouts Array of enabled workout names
     * @return Total estimated duration in seconds
     */
    public long estimateTotalSessionDuration(String[] enabledWorkouts) {
        if (enabledWorkouts == null || enabledWorkouts.length == 0) {
            return 0;
        }
        
        long totalSeconds = 0;
        for (String workout : enabledWorkouts) {
            totalSeconds += estimateWorkoutDuration(workout);
        }
        
        android.util.Log.d(TAG, "Total session estimate: " + totalSeconds + "s for " + enabledWorkouts.length + " workouts");
        return totalSeconds;
    }
    
    /**
     * Get formatted duration string (e.g., "~15 min")
     */
    public String formatDuration(long seconds) {
        if (seconds <= 0) {
            return "";
        }
        
        long minutes = (seconds + 30) / 60; // Round to nearest minute
        if (minutes < 1) {
            return "~1 min";
        } else if (minutes < 60) {
            return "~" + minutes + " min";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                return "~" + hours + " hr";
            }
            return "~" + hours + " hr " + remainingMinutes + " min";
        }
    }
    
    /**
     * Get formatted completion time string (e.g., "Done by 2:30 PM")
     */
    public String formatCompletionTime(long durationSeconds) {
        if (durationSeconds <= 0) {
            return "";
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, (int) durationSeconds);
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return "Done by " + timeFormat.format(calendar.getTime());
    }
    
    /**
     * Get combined estimate string (e.g., "~15 min • Done by 2:30 PM")
     */
    public String getEstimateString(long durationSeconds) {
        if (durationSeconds <= 0) {
            return "";
        }
        
        String duration = formatDuration(durationSeconds);
        String completion = formatCompletionTime(durationSeconds);
        
        if (duration.isEmpty() || completion.isEmpty()) {
            return duration.isEmpty() ? completion : duration;
        }
        
        return duration + " • " + completion;
    }
    
    /**
     * Cold start estimation when no history exists
     * Estimates based on break times and rep count
     */
    private long estimateColdStart(WorkoutInfo workoutInfo, WorkoutWrapper wrapper) {
        try {
            WorkoutGenerator generator = new WorkoutGenerator(workoutInfo);
            Workout workout = generator.getWorkout();
            
            // Calculate total reps
            int totalReps = 0;
            for (int i = 0; i < NUM_SETS; i++) {
                totalReps += workout.getWorkoutValue(i);
            }
            
            // Calculate total break time
            int totalBreakSeconds = 0;
            for (int i = 0; i < NUM_BREAKS; i++) {
                // Break time is stored in milliseconds in the Workout class
                int breakTimeMs = workout.get_BreakTime(i + 1); // Progress-based index
                totalBreakSeconds += breakTimeMs / 1000;
            }
            
            // Estimate: rep time + break time
            long estimatedRepTime = totalReps * SECONDS_PER_REP;
            long totalEstimate = estimatedRepTime + totalBreakSeconds;
            
            android.util.Log.d(TAG, "Cold start estimate for " + workoutInfo.getWorkout() + ": " + totalEstimate + "s (" + totalReps + " reps + " + totalBreakSeconds + "s breaks)");
            
            return totalEstimate;
            
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error in cold start estimation", e);
            // Fallback: 5 minutes per workout
            return 5 * 60;
        }
    }
    
    /**
     * Calculate median of Long list
     */
    private long calculateMedianLong(List<Long> values) {
        if (values.isEmpty()) return 0;
        
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        
        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2;
        }
        return sorted.get(middle);
    }
    
    /**
     * Calculate median of Integer list
     */
    private int calculateMedianInt(List<Integer> values) {
        if (values.isEmpty()) return 0;
        
        List<Integer> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        
        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2;
        }
        return sorted.get(middle);
    }
}
