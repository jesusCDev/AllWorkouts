package com.allvens.allworkouts.log_manager;

import android.content.Context;
import android.widget.TextView;

import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.log_manager.log_chart.LogChartManager;
import com.allvens.allworkouts.log_manager.log_chart.LineChartDataEntry;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

public class LogUIManager {

    private Context context;
    private String chosenWorkout;
    private TextView tvCurrentMax;
    private TextView tvType;
    private TextView tvAvgDuration;
    private TextView tvSessionCount;
    private LogChartManager logChart_manager;

    // Workout type constants
    public static final int WORKOUT_TYPE_SIMPLE = 0;
    public static final int WORKOUT_TYPE_MIX = 1;

    // Current workout type (for average duration display)
    private int currentWorkoutType = WORKOUT_TYPE_SIMPLE;

    // Legacy constructor for backward compatibility
    public LogUIManager(Context context, String chosenWorkout, LineChart lcShowWorkoutProgress, TextView tvCurrentMax, TextView tvType) {
        this(context, chosenWorkout, lcShowWorkoutProgress, tvCurrentMax, tvType, null, null);
    }

    public LogUIManager(Context context, String chosenWorkout, LineChart lcShowWorkoutProgress, TextView tvCurrentMax, TextView tvType, TextView tvAvgDuration, TextView tvSessionCount) {
        this.context              = context;
        this.chosenWorkout        = chosenWorkout;
        this.tvCurrentMax         = tvCurrentMax;
        this.tvType               = tvType;
        this.tvAvgDuration        = tvAvgDuration;
        this.tvSessionCount       = tvSessionCount;
        logChart_manager          = new LogChartManager(context, lcShowWorkoutProgress);
    }

    /********** Max - Methods **********/

    public void update_CurrentMax(int max) {
        tvCurrentMax.setText(max + " reps");
    }

    public void update_CurrentType(int type) {
        this.currentWorkoutType = type;
        String sType = "Simple";

        if(type == 1) {
            sType = "Mix";
        }

        tvType.setText(sType);
    }

    /**
     * Update average duration and session count stats
     * For Mix workouts: shows average per workout and total
     * For Simple workouts: shows single workout average
     * @param historyList The workout history list
     */
    public void update_AverageStats(List<WorkoutHistoryInfo> historyList) {
        if (tvAvgDuration == null || tvSessionCount == null) {
            return;
        }

        if (historyList == null || historyList.isEmpty()) {
            tvAvgDuration.setText("--");
            tvSessionCount.setText("0");
            return;
        }

        // Update session count
        tvSessionCount.setText(String.valueOf(historyList.size()));

        // Count sessions with valid duration and calculate average
        long totalDurationSeconds = 0;
        int validDurationCount = 0;

        for (WorkoutHistoryInfo history : historyList) {
            if (history.hasValidDuration()) {
                totalDurationSeconds += history.getDurationSeconds();
                validDurationCount++;
            }
        }

        if (validDurationCount == 0) {
            tvAvgDuration.setText("--");
            return;
        }

        long avgSeconds = totalDurationSeconds / validDurationCount;

        if (currentWorkoutType == WORKOUT_TYPE_MIX) {
            // For Mix mode: show per-workout average and total session average
            // Get the most recent workout's duration for reference
            WorkoutHistoryInfo lastWorkout = getLastValidDurationWorkout(historyList);
            if (lastWorkout != null) {
                String lastDuration = formatDuration(lastWorkout.getDurationSeconds());
                String avgDuration = formatDuration(avgSeconds);
                tvAvgDuration.setText(lastDuration + " (avg: " + avgDuration + ")");
            } else {
                tvAvgDuration.setText(formatDuration(avgSeconds));
            }
        } else {
            // For Simple mode: show single average
            tvAvgDuration.setText(formatDuration(avgSeconds));
        }
    }

    /**
     * Get the most recent workout with valid duration
     */
    private WorkoutHistoryInfo getLastValidDurationWorkout(List<WorkoutHistoryInfo> historyList) {
        // List is typically oldest first, so iterate from end
        for (int i = historyList.size() - 1; i >= 0; i--) {
            WorkoutHistoryInfo history = historyList.get(i);
            if (history.hasValidDuration()) {
                return history;
            }
        }
        return null;
    }

    /**
     * Format duration in seconds to a human-readable string
     */
    private String formatDuration(long seconds) {
        if (seconds <= 0) {
            return "--";
        }

        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        if (minutes > 0) {
            return String.format("%dm %ds", minutes, remainingSeconds);
        } else {
            return String.format("%ds", remainingSeconds);
        }
    }

    public void update_Graph(ArrayList<LineChartDataEntry> totalSets) {
        logChart_manager.reset_Chart();
        logChart_manager.setUp_ChartValues();
        logChart_manager.create_Chart(totalSets);
    }

    public void reset_GraphToZero(){
        logChart_manager.reset_Chart();
    }
}
