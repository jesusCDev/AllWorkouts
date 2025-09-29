package com.allvens.allworkouts.data_manager.backup;

import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Complete backup data structure for JSON serialization
 */
public class BackupData {
    
    // Metadata
    private String version = "2.0";
    private long timestamp;
    private String appVersion;
    
    // Workout Data
    private List<WorkoutInfo> workouts;
    private Map<Long, List<WorkoutHistoryInfo>> workoutHistories;
    
    // Settings/Preferences
    private Map<String, Object> preferences;
    
    // Workout positions and ordering
    private Map<String, Integer> workoutPositions;
    private Map<String, Boolean> workoutEnabled;
    
    // Notification settings
    private NotificationSettings notificationSettings;
    
    public BackupData() {
        this.timestamp = System.currentTimeMillis();
        this.preferences = new HashMap<>();
        this.workoutPositions = new HashMap<>();
        this.workoutEnabled = new HashMap<>();
        this.workoutHistories = new HashMap<>();
    }
    
    // Metadata getters/setters
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    
    // Workout data getters/setters
    public List<WorkoutInfo> getWorkouts() { return workouts; }
    public void setWorkouts(List<WorkoutInfo> workouts) { this.workouts = workouts; }
    
    public Map<Long, List<WorkoutHistoryInfo>> getWorkoutHistories() { return workoutHistories; }
    public void setWorkoutHistories(Map<Long, List<WorkoutHistoryInfo>> workoutHistories) { 
        this.workoutHistories = workoutHistories; 
    }
    
    // Preferences getters/setters
    public Map<String, Object> getPreferences() { return preferences; }
    public void setPreferences(Map<String, Object> preferences) { this.preferences = preferences; }
    
    // Workout position getters/setters
    public Map<String, Integer> getWorkoutPositions() { return workoutPositions; }
    public void setWorkoutPositions(Map<String, Integer> workoutPositions) { 
        this.workoutPositions = workoutPositions; 
    }
    
    public Map<String, Boolean> getWorkoutEnabled() { return workoutEnabled; }
    public void setWorkoutEnabled(Map<String, Boolean> workoutEnabled) { 
        this.workoutEnabled = workoutEnabled; 
    }
    
    // Notification settings getters/setters
    public NotificationSettings getNotificationSettings() { return notificationSettings; }
    public void setNotificationSettings(NotificationSettings notificationSettings) { 
        this.notificationSettings = notificationSettings; 
    }
    
    /**
     * Nested class for notification settings
     */
    public static class NotificationSettings {
        private boolean enabled;
        private int hour;
        private int minute;
        private boolean[] daysOfWeek = new boolean[7]; // Sunday to Saturday
        
        public NotificationSettings() {}
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public int getHour() { return hour; }
        public void setHour(int hour) { this.hour = hour; }
        
        public int getMinute() { return minute; }
        public void setMinute(int minute) { this.minute = minute; }
        
        public boolean[] getDaysOfWeek() { return daysOfWeek; }
        public void setDaysOfWeek(boolean[] daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    }
}