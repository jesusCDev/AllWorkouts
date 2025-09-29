package com.allvens.allworkouts.data_manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.allvens.allworkouts.data_manager.backup.BackupManager;

/**
 * Handles automatic backup scheduling using AlarmManager
 */
public class BackupScheduler {
    
    private static final String TAG = "BackupScheduler";
    private static final String PREF_LAST_BACKUP_TIME = "last_backup_time";
    private static final String PREF_BACKUP_FREQUENCY = "backup_frequency";
    private static final int BACKUP_REQUEST_CODE = 1001;
    
    // Backup frequencies in milliseconds
    public static final long FREQUENCY_DAILY = 24 * 60 * 60 * 1000L;
    public static final long FREQUENCY_WEEKLY = 7 * 24 * 60 * 60 * 1000L;
    
    private Context context;
    private AlarmManager alarmManager;
    private SharedPreferences prefs;
    
    public BackupScheduler(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    /**
     * Schedule automatic backups
     * @param frequency Backup frequency in milliseconds (use FREQUENCY_DAILY or FREQUENCY_WEEKLY)
     */
    public void scheduleBackups(long frequency) {
        prefs.edit().putLong(PREF_BACKUP_FREQUENCY, frequency).apply();
        
        Intent intent = new Intent(context, AutoBackupReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                BACKUP_REQUEST_CODE, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        
        // Schedule the first backup after the specified frequency
        long triggerTime = SystemClock.elapsedRealtime() + frequency;
        
        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                frequency,
                pendingIntent
        );
        
        Log.d(TAG, "Backup scheduled with frequency: " + (frequency / (60 * 60 * 1000)) + " hours");
    }
    
    /**
     * Cancel scheduled backups
     */
    public void cancelBackups() {
        Intent intent = new Intent(context, AutoBackupReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                BACKUP_REQUEST_CODE, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Backup schedule cancelled");
    }
    
    /**
     * Check if it's time for a backup based on the last backup time and frequency
     */
    public boolean isBackupDue() {
        long lastBackupTime = prefs.getLong(PREF_LAST_BACKUP_TIME, 0);
        long frequency = prefs.getLong(PREF_BACKUP_FREQUENCY, FREQUENCY_WEEKLY);
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastBackupTime) >= frequency;
    }
    
    /**
     * Update the last backup time to current time
     */
    public void updateLastBackupTime() {
        prefs.edit().putLong(PREF_LAST_BACKUP_TIME, System.currentTimeMillis()).apply();
    }
    
    /**
     * Get the configured backup frequency
     */
    public long getBackupFrequency() {
        return prefs.getLong(PREF_BACKUP_FREQUENCY, FREQUENCY_WEEKLY);
    }
    
    /**
     * BroadcastReceiver that handles the automatic backup trigger
     */
    public static class AutoBackupReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "AutoBackupReceiver triggered");
            
            try {
                BackupManager backupManager = new BackupManager(context);
                BackupScheduler scheduler = new BackupScheduler(context);
                
                // Only proceed if auto backup is enabled
                if (!backupManager.isAutoBackupEnabled()) {
                    Log.d(TAG, "Auto backup is disabled, skipping");
                    return;
                }
                
                // Check if backup is actually due (additional safety check)
                if (!scheduler.isBackupDue()) {
                    Log.d(TAG, "Backup not due yet, skipping");
                    return;
                }
                
                // Perform the backup
                String fileName = backupManager.createBackup();
                scheduler.updateLastBackupTime();
                
                Log.d(TAG, "Automatic backup created: " + fileName);
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to create automatic backup", e);
            }
        }
    }
}