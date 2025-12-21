package com.allvens.allworkouts.managers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.base.BaseDataManager;
import com.allvens.allworkouts.base.BaseInterfaces;
import com.allvens.allworkouts.data_manager.backup.BackupManager;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;

import java.io.File;

/**
 * Data Manager for SettingsActivity
 * Handles all data-related operations including database operations,
 * backup/restore operations, and file system interactions
 */
public class SettingsDataManager extends BaseDataManager {
    
    public interface SettingsDataCallback extends BaseInterfaces.BaseDataCallback {
        void onBackupStatusChanged(File[] backupFiles);
    }
    
    private BackupManager backupManager;
    
    public SettingsDataManager(Context context, SettingsDataCallback callback) {
        super(context, callback);
        this.backupManager = new BackupManager(context);
    }
    
    @Override
    protected void initializeDataSources() throws Exception {
        // Data sources are initialized in constructor
    }
    
    @Override
    protected void loadInitialData() throws Exception {
        // Initial data loading is handled in initialize() method
    }
    
    @Override
    protected void cleanupDataSources() throws Exception {
        // No cleanup needed for this data manager
        backupManager = null;
    }
    
    /**
     * Reset workout data to defaults by deleting all workouts and history
     */
    public void resetToDefaults() {
        try {
            WorkoutWrapper wrapper = new WorkoutWrapper(getContext());
            wrapper.open();
            wrapper.deleteAllWorkouts();
            wrapper.deleteAllHistoryWorkouts();
            wrapper.close();
            notifyDataLoaded(); // Notify that data operation completed successfully
        } catch (Exception e) {
            notifyDataError("Failed to reset data: " + e.getMessage());
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
        notifyDataLoaded(); // Operation completed successfully
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
            android.util.Log.d("SettingsDataManager", "Backup created: " + fileName);
            notifyDataLoaded(); // Signal success
            notifyBackupStatusChanged(getExistingBackups());
        } catch (Exception e) {
            android.util.Log.e("SettingsDataManager", "Backup failed", e);
            notifyDataError("Failed to create backup: " + e.getMessage());
        }
    }
    
    /**
     * Perform backup import
     */
    public void performBackupImport(File backupFile) {
        try {
            boolean success = backupManager.importBackup(backupFile.getAbsolutePath());
            if (success) {
                notifyDataLoaded(); // Import completed successfully
                notifyBackupStatusChanged(getExistingBackups());
            } else {
                notifyDataError("Failed to import backup");
            }
        } catch (Exception e) {
            notifyDataError("Import failed: " + e.getMessage());
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
            
            if (intent.resolveActivityInfo(getContext().getPackageManager(), 0) != null) {
                getContext().startActivity(intent);
            } else {
                // Fallback - try to open with any file manager
                Intent fallbackIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fallbackIntent.setType("*/*");
                getContext().startActivity(Intent.createChooser(fallbackIntent, "Open Downloads"));
            }
        } catch (Exception e) {
            notifyDataError("Could not open Downloads folder");
        }
    }
    
    /**
     * Restart the app by launching MainActivity with clear task flags
     */
    public void restartApp() {
        try {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getContext().startActivity(intent);
            
            // If context is an Activity, finish it
            if (getContext() instanceof android.app.Activity) {
                ((android.app.Activity) getContext()).finish();
            }
        } catch (Exception e) {
            notifyDataError("Failed to restart app: " + e.getMessage());
        }
    }
    
    /**
     * Initialize data manager and update backup status
     */
    public void initialize() {
        // Initial backup status update
        notifyBackupStatusChanged(getExistingBackups());
    }
    
    /**
     * Notify callback of backup status changes
     */
    private void notifyBackupStatusChanged(File[] backupFiles) {
        if (getCallback() instanceof SettingsDataCallback) {
            ((SettingsDataCallback) getCallback()).onBackupStatusChanged(backupFiles);
        }
    }
    
    /**
     * Get backup manager for direct access if needed
     * Use sparingly - prefer using the methods provided by this manager
     */
    public BackupManager getBackupManager() {
        return backupManager;
    }
}