package com.allvens.allworkouts.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.WorkoutSessionActivity;
import com.allvens.allworkouts.WorkoutSessionFinishActivity;

/**
 * Foreground service that shows workout progress in the notification bar.
 * Displays timer during breaks and workout info during active workouts.
 */
public class WorkoutForegroundService extends Service {

    // Channel and notification IDs
    private static final String CHANNEL_ID = "workout_active_channel";
    private static final int NOTIFICATION_ID = 1001;

    // Action constants
    public static final String ACTION_START = "com.allvens.allworkouts.action.START";
    public static final String ACTION_UPDATE_TIMER = "com.allvens.allworkouts.action.UPDATE_TIMER";
    public static final String ACTION_UPDATE_WORKOUT = "com.allvens.allworkouts.action.UPDATE_WORKOUT";
    public static final String ACTION_UPDATE_FINISH = "com.allvens.allworkouts.action.UPDATE_FINISH";
    public static final String ACTION_STOP = "com.allvens.allworkouts.action.STOP";

    // Extra keys
    public static final String EXTRA_WORKOUT_NAME = "workout_name";
    public static final String EXTRA_REP_COUNT = "rep_count";
    public static final String EXTRA_TIMER_SECONDS = "timer_seconds";
    public static final String EXTRA_NEXT_WORKOUT = "next_workout";
    public static final String EXTRA_SET_NUMBER = "set_number";

    // State
    private NotificationManager notificationManager;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private int currentTimerSeconds = 0;
    private boolean isTimerMode = false;
    private String currentWorkoutName = "Workout";
    private int currentSetNumber = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        timerHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (action == null) {
            action = ACTION_START;
        }

        switch (action) {
            case ACTION_START:
                currentWorkoutName = intent.getStringExtra(EXTRA_WORKOUT_NAME);
                if (currentWorkoutName == null) currentWorkoutName = "Workout";
                int repCount = intent.getIntExtra(EXTRA_REP_COUNT, 0);
                currentSetNumber = intent.getIntExtra(EXTRA_SET_NUMBER, 1);
                startForegroundService(currentWorkoutName, repCount, currentSetNumber);
                break;

            case ACTION_UPDATE_TIMER:
                int seconds = intent.getIntExtra(EXTRA_TIMER_SECONDS, 0);
                updateTimerNotification(seconds);
                break;

            case ACTION_UPDATE_WORKOUT:
                currentWorkoutName = intent.getStringExtra(EXTRA_WORKOUT_NAME);
                if (currentWorkoutName == null) currentWorkoutName = "Workout";
                int reps = intent.getIntExtra(EXTRA_REP_COUNT, 0);
                currentSetNumber = intent.getIntExtra(EXTRA_SET_NUMBER, 1);
                updateWorkoutNotification(currentWorkoutName, reps, currentSetNumber);
                break;

            case ACTION_UPDATE_FINISH:
                String nextWorkout = intent.getStringExtra(EXTRA_NEXT_WORKOUT);
                updateFinishNotification(nextWorkout);
                break;

            case ACTION_STOP:
                stopForegroundService();
                break;
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    /**
     * Create notification channel for Android O+
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Active Workout",
                    NotificationManager.IMPORTANCE_LOW // Low importance for non-intrusive updates
            );
            channel.setDescription("Shows current workout progress");
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Start the foreground service with initial notification
     */
    private void startForegroundService(String workoutName, int repCount, int setNumber) {
        isTimerMode = false;
        Notification notification = buildWorkoutNotification(workoutName, repCount, setNumber);
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Update notification to show timer countdown
     */
    private void updateTimerNotification(int seconds) {
        isTimerMode = true;
        currentTimerSeconds = seconds;
        Notification notification = buildTimerNotification(seconds);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * Update notification to show workout info
     */
    private void updateWorkoutNotification(String workoutName, int repCount, int setNumber) {
        isTimerMode = false;
        stopTimer();
        Notification notification = buildWorkoutNotification(workoutName, repCount, setNumber);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * Update notification for finish screen
     */
    private void updateFinishNotification(String nextWorkout) {
        isTimerMode = false;
        stopTimer();
        Notification notification = buildFinishNotification(nextWorkout);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * Stop the foreground service
     */
    private void stopForegroundService() {
        stopTimer();
        stopForeground(true);
        stopSelf();
    }

    /**
     * Build notification for workout mode
     */
    private Notification buildWorkoutNotification(String workoutName, int repCount, int setNumber) {
        String title = workoutName;
        String text = repCount > 0 ? repCount + " reps - Set " + setNumber + "/5" : "Set " + setNumber + "/5";

        return buildNotification(title, text, getWorkoutPendingIntent());
    }

    /**
     * Build notification for timer mode
     */
    private Notification buildTimerNotification(int seconds) {
        String title = "Break Time";
        String text = formatTime(seconds) + " remaining";

        return buildNotification(title, text, getWorkoutPendingIntent());
    }

    /**
     * Build notification for finish screen
     */
    private Notification buildFinishNotification(String nextWorkout) {
        String title;
        String text;

        if (nextWorkout != null && !nextWorkout.isEmpty()) {
            title = "Up Next: " + nextWorkout;
            text = "Tap to continue";
        } else {
            title = "Session Complete!";
            text = "Great job!";
        }

        return buildNotification(title, text, getFinishPendingIntent());
    }

    /**
     * Build the notification with common settings
     */
    private Notification buildNotification(String title, String text, PendingIntent pendingIntent) {
        // Use app's vermilion color for accent
        int accentColor = ContextCompat.getColor(this, R.color.vermilion);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setColor(accentColor)
                .setColorized(true);

        return builder.build();
    }

    /**
     * Get PendingIntent for workout session activity
     */
    private PendingIntent getWorkoutPendingIntent() {
        Intent intent = new Intent(this, WorkoutSessionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getActivity(this, 0, intent, flags);
    }

    /**
     * Get PendingIntent for finish activity
     */
    private PendingIntent getFinishPendingIntent() {
        Intent intent = new Intent(this, WorkoutSessionFinishActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getActivity(this, 0, intent, flags);
    }

    /**
     * Format seconds to MM:SS
     */
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Stop the timer handler
     */
    private void stopTimer() {
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    // Static helper methods for starting/updating the service

    /**
     * Start the workout notification service
     */
    public static void start(Context context, String workoutName, int repCount, int setNumber) {
        Intent intent = new Intent(context, WorkoutForegroundService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_WORKOUT_NAME, workoutName);
        intent.putExtra(EXTRA_REP_COUNT, repCount);
        intent.putExtra(EXTRA_SET_NUMBER, setNumber);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    /**
     * Update the notification with timer
     */
    public static void updateTimer(Context context, int seconds) {
        Intent intent = new Intent(context, WorkoutForegroundService.class);
        intent.setAction(ACTION_UPDATE_TIMER);
        intent.putExtra(EXTRA_TIMER_SECONDS, seconds);
        context.startService(intent);
    }

    /**
     * Update the notification with workout info
     */
    public static void updateWorkout(Context context, String workoutName, int repCount, int setNumber) {
        Intent intent = new Intent(context, WorkoutForegroundService.class);
        intent.setAction(ACTION_UPDATE_WORKOUT);
        intent.putExtra(EXTRA_WORKOUT_NAME, workoutName);
        intent.putExtra(EXTRA_REP_COUNT, repCount);
        intent.putExtra(EXTRA_SET_NUMBER, setNumber);
        context.startService(intent);
    }

    /**
     * Update the notification for finish screen
     */
    public static void updateFinish(Context context, String nextWorkout) {
        Intent intent = new Intent(context, WorkoutForegroundService.class);
        intent.setAction(ACTION_UPDATE_FINISH);
        intent.putExtra(EXTRA_NEXT_WORKOUT, nextWorkout);
        context.startService(intent);
    }

    /**
     * Stop the workout notification service
     */
    public static void stop(Context context) {
        Intent intent = new Intent(context, WorkoutForegroundService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }
}
