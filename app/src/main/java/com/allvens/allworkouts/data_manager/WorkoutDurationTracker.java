package com.allvens.allworkouts.data_manager;

import android.content.Context;

import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks workout duration with pause/resume support and outlier detection.
 * 
 * Usage:
 * 1. Call startTracking() when workout session begins
 * 2. Call onPause() when activity pauses
 * 3. Call onResume() when activity resumes
 * 4. Call getActiveDurationSeconds() when session completes
 * 5. Call isValidDuration() to check if duration should be saved
 */
public class WorkoutDurationTracker {
    
    private static final String TAG = "WorkoutDurationTracker";
    
    // Outlier detection thresholds
    private static final double MAX_PAUSE_PERCENTAGE = 0.5; // 50% pause time = outlier
    private static final double MAX_DURATION_MULTIPLIER = 3.0; // 3x median = outlier
    private static final double MIN_DURATION_MULTIPLIER = 0.3; // 0.3x median = outlier
    private static final int MIN_HISTORY_FOR_OUTLIER_DETECTION = 3;
    
    // Tracking state
    private long sessionStartTimeMs = 0;
    private long lastPauseTimeMs = 0;
    private long totalPauseDurationMs = 0;
    private boolean isPaused = false;
    private boolean isTracking = false;
    
    private Context context;
    
    public WorkoutDurationTracker(Context context) {
        this.context = context;
    }
    
    /**
     * Start tracking a new workout session
     */
    public void startTracking() {
        sessionStartTimeMs = System.currentTimeMillis();
        totalPauseDurationMs = 0;
        isPaused = false;
        isTracking = true;
        android.util.Log.d(TAG, "Started tracking at " + sessionStartTimeMs);
    }
    
    /**
     * Called when activity pauses - tracks pause start time
     */
    public void onPause() {
        if (!isTracking || isPaused) return;
        
        lastPauseTimeMs = System.currentTimeMillis();
        isPaused = true;
        android.util.Log.d(TAG, "Paused at " + lastPauseTimeMs);
    }
    
    /**
     * Called when activity resumes - calculates pause duration
     */
    public void onResume() {
        if (!isTracking || !isPaused) return;
        
        long pauseDuration = System.currentTimeMillis() - lastPauseTimeMs;
        totalPauseDurationMs += pauseDuration;
        isPaused = false;
        android.util.Log.d(TAG, "Resumed after " + (pauseDuration / 1000) + " seconds pause. Total pause: " + (totalPauseDurationMs / 1000) + "s");
    }
    
    /**
     * Get the active (non-paused) duration of the workout in seconds
     */
    public long getActiveDurationSeconds() {
        if (!isTracking) return 0;
        
        long totalDurationMs = System.currentTimeMillis() - sessionStartTimeMs;
        
        // If currently paused, add current pause duration
        if (isPaused) {
            long currentPauseDuration = System.currentTimeMillis() - lastPauseTimeMs;
            totalPauseDurationMs += currentPauseDuration;
        }
        
        long activeDurationMs = totalDurationMs - totalPauseDurationMs;
        return Math.max(0, activeDurationMs / 1000);
    }
    
    /**
     * Get the total duration (including pauses) in seconds
     */
    public long getTotalDurationSeconds() {
        if (!isTracking) return 0;
        return (System.currentTimeMillis() - sessionStartTimeMs) / 1000;
    }
    
    /**
     * Get the percentage of time spent paused (0.0 - 1.0)
     */
    public double getPausePercentage() {
        if (!isTracking) return 0;
        
        long totalDurationMs = System.currentTimeMillis() - sessionStartTimeMs;
        if (totalDurationMs <= 0) return 0;
        
        long pauseMs = totalPauseDurationMs;
        if (isPaused) {
            pauseMs += System.currentTimeMillis() - lastPauseTimeMs;
        }
        
        return (double) pauseMs / totalDurationMs;
    }
    
    /**
     * Check if the current session duration is valid (not an outlier)
     * @param workoutId The workout ID to check history for
     * @return true if duration is valid and should be saved
     */
    public boolean isValidDuration(long workoutId) {
        if (!isTracking) return false;
        
        long activeDuration = getActiveDurationSeconds();
        double pausePercentage = getPausePercentage();
        
        android.util.Log.d(TAG, "Validating duration: " + activeDuration + "s, pause%: " + (pausePercentage * 100) + "%");
        
        // Check if too much pause time
        if (pausePercentage > MAX_PAUSE_PERCENTAGE) {
            android.util.Log.d(TAG, "Invalid: Too much pause time (" + (pausePercentage * 100) + "%)");
            return false;
        }
        
        // Check against historical data for statistical outliers
        List<Long> historicalDurations = getHistoricalDurations(workoutId);
        
        if (historicalDurations.size() < MIN_HISTORY_FOR_OUTLIER_DETECTION) {
            // Not enough history to detect outliers, accept this duration
            android.util.Log.d(TAG, "Valid: Not enough history for outlier detection (" + historicalDurations.size() + " entries)");
            return true;
        }
        
        // Calculate median
        long median = calculateMedian(historicalDurations);
        android.util.Log.d(TAG, "Median historical duration: " + median + "s");
        
        // Check if way too long
        if (activeDuration > median * MAX_DURATION_MULTIPLIER) {
            android.util.Log.d(TAG, "Invalid: Duration too long (" + activeDuration + "s > " + (median * MAX_DURATION_MULTIPLIER) + "s)");
            return false;
        }
        
        // Check if way too short
        if (activeDuration < median * MIN_DURATION_MULTIPLIER) {
            android.util.Log.d(TAG, "Invalid: Duration too short (" + activeDuration + "s < " + (median * MIN_DURATION_MULTIPLIER) + "s)");
            return false;
        }
        
        android.util.Log.d(TAG, "Valid: Duration within acceptable range");
        return true;
    }
    
    /**
     * Get historical durations for a workout from the database
     */
    private List<Long> getHistoricalDurations(long workoutId) {
        List<Long> durations = new ArrayList<>();
        WorkoutWrapper wrapper = new WorkoutWrapper(context);
        
        try {
            wrapper.open();
            List<WorkoutHistoryInfo> history = wrapper.getRecentValidDurations(workoutId, 15);
            
            for (WorkoutHistoryInfo info : history) {
                if (info.hasValidDuration()) {
                    durations.add(info.getDurationSeconds());
                }
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error getting historical durations", e);
        } finally {
            wrapper.close();
        }
        
        return durations;
    }
    
    /**
     * Calculate median of a list of values
     */
    private long calculateMedian(List<Long> values) {
        if (values.isEmpty()) return 0;
        
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        
        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2;
        } else {
            return sorted.get(middle);
        }
    }
    
    /**
     * Stop tracking and reset state
     */
    public void stopTracking() {
        isTracking = false;
        isPaused = false;
        android.util.Log.d(TAG, "Stopped tracking. Final active duration: " + getActiveDurationSeconds() + "s");
    }
    
    /**
     * Check if currently tracking
     */
    public boolean isTracking() {
        return isTracking;
    }
}
