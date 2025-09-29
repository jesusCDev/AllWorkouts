package com.allvens.allworkouts.managers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.data_manager.backup.BackupManager;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;

import java.io.File;

/**
 * Data Manager for SettingsActivity
 * Handles all data-related operations including database operations,
 * backup/restore operations, and file system interactions
 */
public class SettingsDataManager {
    
    public interface SettingsDataCallback {
        void onOperationSuccess(String message);
        void onOperationError(String message);
        void onBackupStatusChanged(File[] backupFiles);
    }
    
    private Context context;
    private SettingsDataCallback callback;
    private BackupManager backupManager;
    
    public SettingsDataManager(Context context, SettingsDataCallback callback) {
        this.context = context;
        this.callback = callback;
        this.backupManager = new BackupManager(context);
    }
    
    /**
     * Reset workout data to defaults by deleting all workouts and history
     */
    public void resetToDefaults() {
        try {
            WorkoutWrapper wrapper = new WorkoutWrapper(context);
            wrapper.open();
            wrapper.deleteAllWorkouts();
            wrapper.deleteAllHistoryWorkouts();
            wrapper.close();
            callback.onOperationSuccess("Workout data reset to defaults");
        } catch (Exception e) {
            callback.onOperationError("Failed to reset data: " + e.getMessage());
        }
    }
    
    /**
     * Check if auto backup is enabled
     */
    public boolean isAutoBackupEnabled() {
        return backupManager.isAutoBackupEnabled();
    }
    
    /**
     * Set auto backup enabled state
     */
    public void setAutoBackupEnabled(boolean enabled) {
        backupManager.setAutoBackupEnabled(enabled);
        String message = enabled ? "Automatic backups enabled" : "Automatic backups disabled";
        callback.onOperationSuccess(message);
    }
    
    /**
     * Get existing backup files
     */
    public File[] getExistingBackups() {
        return backupManager.getExistingBackups();
    }
    
    /**
     * Create a backup export
     */
    public void performBackupExport() {
        try {
            String fileName = backupManager.createBackup();
            callback.onOperationSuccess("Backup created: " + fileName);
            callback.onBackupStatusChanged(getExistingBackups());
        } catch (Exception e) {
            callback.onOperationError("Failed to create backup: " + e.getMessage());
        }
    }
    
    /**
     * Perform backup import
     */
    public void performBackupImport(File backupFile) {
        try {
            boolean success = backupManager.importBackup(backupFile.getAbsolutePath());
            if (success) {
                callback.onOperationSuccess("Backup imported successfully");
                callback.onBackupStatusChanged(getExistingBackups());
            } else {
                callback.onOperationError("Failed to import backup");
            }
        } catch (Exception e) {
            callback.onOperationError("Import failed: " + e.getMessage());
        }
    }
    
    /**
     * Open Downloads folder in file manager
     */
    public void openDownloadsFolder() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            intent.setDataAndType(uri, "resource/folder");
            
            if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
                context.startActivity(intent);
            } else {
                // Fallback - try to open with any file manager
                Intent fallbackIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fallbackIntent.setType("*/*");
                context.startActivity(Intent.createChooser(fallbackIntent, "Open Downloads"));
            }
        } catch (Exception e) {
            callback.onOperationError("Could not open Downloads folder");
        }
    }
    
    /**
     * Restart the app by launching MainActivity with clear task flags
     */
    public void restartApp() {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            
            // If context is an Activity, finish it
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).finish();
            }
        } catch (Exception e) {
            callback.onOperationError("Failed to restart app: " + e.getMessage());
        }
    }
    
    /**
     * Initialize data manager and update backup status
     */
    public void initialize() {
        // Initial backup status update
        callback.onBackupStatusChanged(getExistingBackups());
    }
    
    /**
     * Get backup manager for direct access if needed
     * Use sparingly - prefer using the methods provided by this manager
     */
    public BackupManager getBackupManager() {
        return backupManager;
    }
}