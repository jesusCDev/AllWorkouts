package com.allvens.allworkouts.data_manager.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

// BuildConfig will be generated at build time, remove this import
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;

import com.allvens.allworkouts.data_manager.BackupScheduler;

/**
 * Comprehensive backup and restore manager for AllWorkouts app
 * Handles manual export/import and automatic backup rotation
 */
public class BackupManager {
    
    private static final String TAG = "BackupManager";
    private static final String BACKUP_FOLDER = "AllWorkouts_Backups";
    private static final String BACKUP_PREFIX = "allworkouts_backup_";
    private static final String BACKUP_EXTENSION = ".json";
    private static final int MAX_AUTO_BACKUPS = 3;
    
    private Context context;
    private WorkoutWrapper workoutWrapper;
    private SettingsPrefsManager prefsManager;
    private BackupScheduler backupScheduler;
    
    private static final String PREF_AUTO_BACKUP_ENABLED = "auto_backup_enabled";
    
    public BackupManager(Context context) {
        this.context = context;
        this.workoutWrapper = new WorkoutWrapper(context);
        this.prefsManager = new SettingsPrefsManager(context);
        this.backupScheduler = new BackupScheduler(context);
    }
    
    /**
     * Creates a complete backup of all app data and exports it to Downloads
     */
    public String createBackup() throws IOException {
        return exportBackup();
    }
    
    /**
     * Creates a complete backup data object
     */
    public BackupData createBackupData() {
        BackupData backup = new BackupData();
        
        // Set metadata
        backup.setAppVersion("1.0"); // TODO: Get from manifest or build config
        backup.setTimestamp(System.currentTimeMillis());
        
        // Backup workout data
        workoutWrapper.open();
        try {
            List<WorkoutInfo> workouts = workoutWrapper.getAllWorkouts();
            backup.setWorkouts(workouts);
            Log.d(TAG, "Backing up " + (workouts != null ? workouts.size() : 0) + " workouts");
            
            // Backup workout histories
            Map<Long, List<WorkoutHistoryInfo>> histories = new HashMap<>();
            if (workouts != null) {
                for (WorkoutInfo workout : workouts) {
                    List<WorkoutHistoryInfo> history = workoutWrapper.getHistoryForWorkout(workout.getId());
                    if (history != null && !history.isEmpty()) {
                        histories.put(workout.getId(), history);
                        Log.d(TAG, "Workout '" + workout.getWorkout() + "' has " + history.size() + " history entries");
                    }
                }
            }
            backup.setWorkoutHistories(histories);
            Log.d(TAG, "Total workout histories: " + histories.size());
        } finally {
            workoutWrapper.close();
        }
        
        // Backup preferences
        backup.setPreferences(getAllPreferences());
        
        // Backup notification settings
        backup.setNotificationSettings(getNotificationSettings());
        
        // Backup workout positions and enabled status
        backup.setWorkoutPositions(getWorkoutPositions());
        backup.setWorkoutEnabled(getWorkoutEnabledStatus());
        
        return backup;
    }
    
    /**
     * Exports backup to Downloads folder with user-friendly filename
     */
    public String exportBackup() throws IOException {
        Log.d(TAG, "Starting backup export...");
        BackupData backup = createBackupData();
        String filename = generateBackupFilename(false);
        File backupFile = new File(getDownloadsDirectory(), filename);
        
        Log.d(TAG, "Writing backup to: " + backupFile.getAbsolutePath());
        writeBackupToFile(backup, backupFile);
        
        Log.i(TAG, "Backup exported successfully to: " + backupFile.getAbsolutePath() + " (" + backupFile.length() + " bytes)");
        return filename;
    }
    
    /**
     * Creates an automatic backup with rotation
     */
    public void createAutomaticBackup() {
        try {
            BackupData backup = createBackupData();
            String filename = generateBackupFilename(true);
            File backupFile = new File(getBackupDirectory(), filename);
            
            writeBackupToFile(backup, backupFile);
            rotateBackups();
            
            Log.i(TAG, "Automatic backup created: " + backupFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to create automatic backup", e);
        }
    }
    
    /**
     * Imports backup from file and restores all data
     */
    public boolean importBackup(String backupFilePath) {
        try {
            Log.d(TAG, "Starting backup import from: " + backupFilePath);
            File backupFile = new File(backupFilePath);
            
            if (!backupFile.exists()) {
                Log.e(TAG, "Backup file does not exist: " + backupFilePath);
                return false;
            }
            
            Log.d(TAG, "Reading backup file, size: " + backupFile.length() + " bytes");
            BackupData backup = readBackupFromFile(backupFile);
            
            Log.d(TAG, "Backup data loaded - workouts: " + (backup.getWorkouts() != null ? backup.getWorkouts().size() : "null") + 
                      ", histories: " + (backup.getWorkoutHistories() != null ? backup.getWorkoutHistories().size() : "null"));
            
            return restoreBackup(backup);
        } catch (Exception e) {
            Log.e(TAG, "Failed to import backup", e);
            return false;
        }
    }
    
    /**
     * Get existing backup files sorted by date (newest first)
     */
    public File[] getExistingBackups() {
        ArrayList<File> backupsList = new ArrayList<>();
        
        // Check Downloads folder
        File downloadsDir = getDownloadsDirectory();
        if (downloadsDir.exists()) {
            File[] files = downloadsDir.listFiles((dir, name) -> 
                name.startsWith(BACKUP_PREFIX) && name.endsWith(BACKUP_EXTENSION));
            if (files != null) {
                backupsList.addAll(Arrays.asList(files));
            }
        }
        
        // Check automatic backup folder
        File backupDir = getBackupDirectory();
        if (backupDir.exists()) {
            File[] files = backupDir.listFiles((dir, name) -> 
                name.startsWith(BACKUP_PREFIX) && name.endsWith(BACKUP_EXTENSION));
            if (files != null) {
                backupsList.addAll(Arrays.asList(files));
            }
        }
        
        // Sort by date (newest first)
        File[] backups = backupsList.toArray(new File[0]);
        Arrays.sort(backups, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        
        return backups;
    }
    
    /**
     * Check if auto backup is enabled
     */
    public boolean isAutoBackupEnabled() {
        return prefsManager.getPrefSetting(PREF_AUTO_BACKUP_ENABLED);
    }
    
    /**
     * Enable or disable auto backup
     */
    public void setAutoBackupEnabled(boolean enabled) {
        prefsManager.update_PrefSetting(PREF_AUTO_BACKUP_ENABLED, enabled);
        
        if (enabled) {
            // Schedule weekly backups
            backupScheduler.scheduleBackups(BackupScheduler.FREQUENCY_WEEKLY);
        } else {
            // Cancel scheduled backups
            backupScheduler.cancelBackups();
        }
    }
    
    /**
     * Restores all data from backup
     */
    private boolean restoreBackup(BackupData backup) {
        try {
            Log.d(TAG, "Starting restore process...");
            workoutWrapper.open();
            
            // Clear existing data (optional - could be a user choice)
            Log.d(TAG, "Clearing existing data...");
            workoutWrapper.deleteAllWorkouts();
            workoutWrapper.deleteAllHistoryWorkouts();
            
            // Restore workouts
            List<WorkoutInfo> workouts = backup.getWorkouts();
            if (workouts != null && !workouts.isEmpty()) {
                Log.d(TAG, "Restoring " + workouts.size() + " workouts...");
                for (WorkoutInfo workout : workouts) {
                    // Store the original ID to match with history
                    long originalId = workout.getId();
                    
                    Log.d(TAG, "Restoring workout: " + workout.getWorkout() + " (ID: " + originalId + 
                              ", Max: " + workout.getMax() + ", Progress: " + workout.getProgress() + ")");
                    
                    // Create the workout (this will set a new ID)
                    workoutWrapper.createWorkout(workout);
                    
                    // Get the newly created workout to get the new ID
                    WorkoutInfo newWorkout = workoutWrapper.getWorkout(workout.getWorkout());
                    if (newWorkout != null) {
                        long newWorkoutId = newWorkout.getId();
                        Log.d(TAG, "Workout created with new ID: " + newWorkoutId);
                        
                        // Restore workout history with the new workout ID
                        List<WorkoutHistoryInfo> history = backup.getWorkoutHistories().get(originalId);
                        if (history != null && !history.isEmpty()) {
                            Log.d(TAG, "Restoring " + history.size() + " history entries for workout " + workout.getWorkout());
                            for (WorkoutHistoryInfo historyItem : history) {
                                workoutWrapper.createWorkoutHistory(historyItem, newWorkoutId);
                            }
                        } else {
                            Log.d(TAG, "No history found for workout: " + workout.getWorkout());
                        }
                    } else {
                        Log.e(TAG, "Failed to retrieve newly created workout: " + workout.getWorkout());
                    }
                }
            } else {
                Log.w(TAG, "No workouts to restore (workouts list is null or empty)");
            }
            
            workoutWrapper.close();
            
            // Restore preferences
            restorePreferences(backup.getPreferences());
            
            // Restore notification settings
            restoreNotificationSettings(backup.getNotificationSettings());
            
            // Restore workout positions and enabled status
            restoreWorkoutPositions(backup.getWorkoutPositions());
            restoreWorkoutEnabledStatus(backup.getWorkoutEnabled());
            
            Log.i(TAG, "Backup restored successfully");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to restore backup", e);
            return false;
        }
    }
    
    
    // Helper methods
    
    private void writeBackupToFile(BackupData backup, File file) throws IOException {
        file.getParentFile().mkdirs();
        
        try {
            JSONObject json = backupToJson(backup);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.toString(2).getBytes());
            fos.close();
        } catch (JSONException e) {
            throw new IOException("Failed to serialize backup data", e);
        }
    }
    
    private BackupData readBackupFromFile(File file) throws IOException, JSONException {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        
        String jsonString = new String(data);
        JSONObject json = new JSONObject(jsonString);
        return jsonToBackup(json);
    }
    
    private JSONObject backupToJson(BackupData backup) throws JSONException {
        JSONObject json = new JSONObject();
        
        // Metadata
        json.put("version", backup.getVersion());
        json.put("timestamp", backup.getTimestamp());
        json.put("appVersion", backup.getAppVersion());
        
        // Save workout data
        if (backup.getWorkouts() != null) {
            JSONArray workoutsArray = new JSONArray();
            for (WorkoutInfo workout : backup.getWorkouts()) {
                JSONObject workoutJson = new JSONObject();
                workoutJson.put("id", workout.getId());
                workoutJson.put("workout", workout.getWorkout());
                workoutJson.put("type", workout.getType());
                workoutJson.put("max", workout.getMax());
                workoutJson.put("progress", workout.getProgress());
                workoutJson.put("difficultyRating", workout.getDifficultyRating());
                workoutsArray.put(workoutJson);
            }
            json.put("workouts", workoutsArray);
        }
        
        // Save workout history
        if (backup.getWorkoutHistories() != null) {
            JSONObject historiesJson = new JSONObject();
            for (Map.Entry<Long, List<WorkoutHistoryInfo>> entry : backup.getWorkoutHistories().entrySet()) {
                JSONArray historyArray = new JSONArray();
                for (WorkoutHistoryInfo historyItem : entry.getValue()) {
                    JSONObject historyJson = new JSONObject();
                    historyJson.put("id", historyItem.getId());
                    historyJson.put("first", historyItem.getFirst_value());
                    historyJson.put("second", historyItem.getSecond_value());
                    historyJson.put("third", historyItem.getThird_value());
                    historyJson.put("forth", historyItem.getForth_value());
                    historyJson.put("fifth", historyItem.getFifth_value());
                    historyJson.put("max", historyItem.getMax_value());
                    historyArray.put(historyJson);
                }
                historiesJson.put(entry.getKey().toString(), historyArray);
            }
            json.put("workoutHistories", historiesJson);
        }
        
        // Save preferences as a map
        if (backup.getPreferences() != null) {
            JSONObject prefsJson = new JSONObject();
            for (Map.Entry<String, Object> entry : backup.getPreferences().entrySet()) {
                prefsJson.put(entry.getKey(), entry.getValue());
            }
            json.put("preferences", prefsJson);
        }
        
        return json;
    }
    
    private BackupData jsonToBackup(JSONObject json) throws JSONException {
        BackupData backup = new BackupData();
        
        Log.d(TAG, "Parsing JSON backup data...");
        
        // Restore metadata
        backup.setVersion(json.getString("version"));
        backup.setTimestamp(json.getLong("timestamp"));
        backup.setAppVersion(json.getString("appVersion"));
        
        Log.d(TAG, "Backup metadata - Version: " + backup.getVersion() + ", App: " + backup.getAppVersion());
        Log.d(TAG, "JSON contains workouts: " + json.has("workouts"));
        Log.d(TAG, "JSON contains histories: " + json.has("workoutHistories"));
        
        // Restore workout data
        if (json.has("workouts")) {
            JSONArray workoutsArray = json.getJSONArray("workouts");
            List<WorkoutInfo> workouts = new ArrayList<>();
            
            for (int i = 0; i < workoutsArray.length(); i++) {
                JSONObject workoutJson = workoutsArray.getJSONObject(i);
                WorkoutInfo workout = new WorkoutInfo(
                    workoutJson.getString("workout"),
                    workoutJson.getInt("max"),
                    workoutJson.getInt("type"),
                    workoutJson.getInt("progress"),
                    workoutJson.getInt("difficultyRating")
                );
                workout.setId(workoutJson.getLong("id"));
                workouts.add(workout);
            }
            backup.setWorkouts(workouts);
        }
        
        // Restore workout history
        if (json.has("workoutHistories")) {
            JSONObject historiesJson = json.getJSONObject("workoutHistories");
            Map<Long, List<WorkoutHistoryInfo>> histories = new HashMap<>();
            
            java.util.Iterator<String> workoutIds = historiesJson.keys();
            while (workoutIds.hasNext()) {
                String workoutIdStr = workoutIds.next();
                Long workoutId = Long.parseLong(workoutIdStr);
                
                JSONArray historyArray = historiesJson.getJSONArray(workoutIdStr);
                List<WorkoutHistoryInfo> historyList = new ArrayList<>();
                
                for (int i = 0; i < historyArray.length(); i++) {
                    JSONObject historyJson = historyArray.getJSONObject(i);
                    WorkoutHistoryInfo historyItem = new WorkoutHistoryInfo(
                        historyJson.getInt("first"),
                        historyJson.getInt("second"),
                        historyJson.getInt("third"),
                        historyJson.getInt("forth"),
                        historyJson.getInt("fifth"),
                        historyJson.getInt("max")
                    );
                    historyItem.setId(historyJson.getLong("id"));
                    historyList.add(historyItem);
                }
                
                histories.put(workoutId, historyList);
            }
            backup.setWorkoutHistories(histories);
        }
        
        // Restore preferences
        if (json.has("preferences")) {
            JSONObject prefsJson = json.getJSONObject("preferences");
            Map<String, Object> prefs = new HashMap<>();
            
            java.util.Iterator<String> keys = prefsJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                prefs.put(key, prefsJson.get(key));
            }
            backup.setPreferences(prefs);
        }
        
        return backup;
    }
    
    private String generateBackupFilename(boolean isAutomatic) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        
        if (isAutomatic) {
            return BACKUP_PREFIX + "auto_" + timestamp + BACKUP_EXTENSION;
        } else {
            return BACKUP_PREFIX + timestamp + BACKUP_EXTENSION;
        }
    }
    
    private File getDownloadsDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }
    
    private File getBackupDirectory() {
        return new File(context.getExternalFilesDir(null), BACKUP_FOLDER);
    }
    
    private void rotateBackups() {
        File backupDir = getBackupDirectory();
        if (!backupDir.exists()) return;
        
        File[] backups = backupDir.listFiles((dir, name) -> 
            name.startsWith(BACKUP_PREFIX + "auto_") && name.endsWith(BACKUP_EXTENSION));
        
        if (backups != null && backups.length > MAX_AUTO_BACKUPS) {
            Arrays.sort(backups, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
            
            // Delete oldest backups
            for (int i = 0; i < backups.length - MAX_AUTO_BACKUPS; i++) {
                backups[i].delete();
                Log.d(TAG, "Deleted old backup: " + backups[i].getName());
            }
        }
    }
    
    // Preference backup/restore methods
    private Map<String, Object> getAllPreferences() {
        Map<String, Object> prefs = new HashMap<>();
        
        prefs.put(PreferencesValues.VIBRATE_ON, prefsManager.getPrefSetting(PreferencesValues.VIBRATE_ON));
        prefs.put(PreferencesValues.SOUND_ON, prefsManager.getPrefSetting(PreferencesValues.SOUND_ON));
        prefs.put(PreferencesValues.MEDIA_CONTROLS_ON, prefsManager.getPrefSetting(PreferencesValues.MEDIA_CONTROLS_ON));
        prefs.put(PreferencesValues.NOTIFICATION_ON, prefsManager.getPrefSetting(PreferencesValues.NOTIFICATION_ON));
        
        return prefs;
    }
    
    private void restorePreferences(Map<String, Object> preferences) {
        for (Map.Entry<String, Object> entry : preferences.entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                prefsManager.update_PrefSetting(entry.getKey(), (Boolean) entry.getValue());
            }
        }
    }
    
    private BackupData.NotificationSettings getNotificationSettings() {
        BackupData.NotificationSettings settings = new BackupData.NotificationSettings();
        settings.setEnabled(prefsManager.getPrefSetting(PreferencesValues.NOTIFICATION_ON));
        settings.setHour(prefsManager.get_NotifiHour());
        settings.setMinute(prefsManager.get_NotifiMinute());
        
        boolean[] days = new boolean[7];
        for (int i = 0; i < 7; i++) {
            days[i] = prefsManager.get_NotificationDayValue(i);
        }
        settings.setDaysOfWeek(days);
        
        return settings;
    }
    
    private void restoreNotificationSettings(BackupData.NotificationSettings settings) {
        if (settings != null) {
            prefsManager.update_PrefSetting(PreferencesValues.NOTIFICATION_ON, settings.isEnabled());
            prefsManager.update_NotificationTime(settings.getHour(), settings.getMinute());
            
            boolean[] days = settings.getDaysOfWeek();
            for (int i = 0; i < Math.min(days.length, 7); i++) {
                prefsManager.update_NotificationDay(i);
            }
        }
    }
    
    private Map<String, Integer> getWorkoutPositions() {
        // Implementation depends on how workout positions are stored
        // This is a placeholder
        return new HashMap<>();
    }
    
    private void restoreWorkoutPositions(Map<String, Integer> positions) {
        // Implementation for restoring workout positions
    }
    
    private Map<String, Boolean> getWorkoutEnabledStatus() {
        // Implementation depends on how workout enabled status is stored
        // This is a placeholder
        return new HashMap<>();
    }
    
    private void restoreWorkoutEnabledStatus(Map<String, Boolean> enabled) {
        // Implementation for restoring workout enabled status
    }
}