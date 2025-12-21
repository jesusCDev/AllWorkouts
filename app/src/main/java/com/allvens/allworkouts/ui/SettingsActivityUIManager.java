package com.allvens.allworkouts.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.base.BaseUIManager;
import com.allvens.allworkouts.base.BaseInterfaces;
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.settings_manager.SettingsManager;

import java.io.File;

/**
 * UI Manager for SettingsActivity
 * Handles all view binding, setup, styling, and UI state management
 */
public class SettingsActivityUIManager extends BaseUIManager {
    
    public interface SettingsUICallback extends BaseInterfaces.BaseUICallback {
        void onResetToDefaults();
        void onNotificationTimeClicked(View view);
        void onDayNotificationClicked(View view);
        void onExportBackup();
        void onImportBackup(File backupFile);
        void onBrowseForBackupFile();
        void onBrowseForBackupFolder();
        void onShowDocumentation();
        void onMediaControlsToggled(boolean enabled);
        boolean isNotificationListenerEnabled();
    }
    
    private SettingsManager settingsManager;
    
    // UI Elements
    private Switch switchAutoBackup;
    private TextView tvBackupStatus;
    private LinearLayout llWorkoutPositions;
    
    // Settings switches
    private Switch sVibrate;
    private Switch sSound;
    private Switch sNotification;
    private Switch sMediaControls;
    private Switch sShowSongTitle;
    private View llShowSongTitleContainer;
    
    // Display settings switches
    private Switch sShowTimeEstimate;
    private Switch sShowStatsCards;
    
    // Notification day buttons
    private Button btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa;
    private TextView tvTimeDisplay;

    public SettingsActivityUIManager(Context context, SettingsUICallback callback) {
        super(context, callback);
        this.settingsManager = new SettingsManager(context);
    }
    
    @Override
    protected void setupViews() {
        refreshSettingsValues();
    }
    
    /**
     * Refresh all settings values from preferences (call after import)
     */
    public void refreshSettingsValues() {
        // Setup settings values using the existing SettingsManager
        settingsManager.set_SettingsValues(sVibrate, sSound, sNotification, sMediaControls, sShowSongTitle);
        settingsManager.setUp_WorkoutsAndPositions(llWorkoutPositions);
        settingsManager.setUp_TimeDisplay(tvTimeDisplay);
        settingsManager.setUP_DailyNotificationBtns(btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa);
        
        // Setup display settings (default to true if not set)
        settingsManager.set_DisplaySettingsValues(sShowTimeEstimate, sShowStatsCards);
        
        // Show/hide song title sub-setting based on media controls state
        updateSongTitleSettingVisibility(sMediaControls.isChecked());
    }
    
    /**
     * Update visibility of song title setting based on media controls state
     */
    private void updateSongTitleSettingVisibility(boolean mediaControlsEnabled) {
        if (llShowSongTitleContainer != null) {
            llShowSongTitleContainer.setVisibility(mediaControlsEnabled ? View.VISIBLE : View.GONE);
        }
    }
    
    @Override
    protected void setupListeners() {
        // Settings switches listeners (delegate to SettingsManager)
        sVibrate.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.VIBRATE_ON));
        sSound.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SOUND_ON));
        sNotification.setOnCheckedChangeListener(settingsManager.update_NotfiSettings(PreferencesValues.NOTIFICATION_ON));
        
        // Media controls with permission check
        sMediaControls.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the preference
            settingsManager.update_PrefSettings(PreferencesValues.MEDIA_CONTROLS_ON).onCheckedChanged(buttonView, isChecked);
            // Show/hide song title sub-setting
            updateSongTitleSettingVisibility(isChecked);
            // Notify activity to handle permission if enabling
            notifyMediaControlsToggled(isChecked);
        });
        
        // Show song title setting
        sShowSongTitle.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SHOW_SONG_TITLE));
        
        // Display settings listeners
        sShowTimeEstimate.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SHOW_TIME_ESTIMATE));
        sShowStatsCards.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SHOW_STATS_CARDS));
    }
    
    /**
     * Show dialog prompting for notification listener permission
     */
    public void showNotificationListenerPermissionDialog(Runnable onOpenSettings) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builder.setTitle("Permission Required")
                .setMessage("To show the current song name, this app needs Notification Access permission.\n\nAfter granting permission, restart the app for changes to take effect.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    if (onOpenSettings != null) onOpenSettings.run();
                })
                .setNegativeButton("Not Now", null)
                .show();
    }
    
    @Override
    protected void cleanupViews() {
        // Cleanup view references
        switchAutoBackup = null;
        tvBackupStatus = null;
        llWorkoutPositions = null;
        sVibrate = null;
        sSound = null;
        sNotification = null;
        sMediaControls = null;
        btnSu = btnM = btnTu = btnW = btnTh = btnF = btnSa = null;
        tvTimeDisplay = null;
    }
    
    /**
     * Initialize all views and bind them to the UI manager
     */
    @Override
    public void initializeViews() {
        // Main settings switches
        sVibrate = ((android.app.Activity) getContext()).findViewById(R.id.s_settings_Vibrate);
        sSound = ((android.app.Activity) getContext()).findViewById(R.id.s_settings_Sound);
        sNotification = ((android.app.Activity) getContext()).findViewById(R.id.s_settings_Notification);
        sMediaControls = ((android.app.Activity) getContext()).findViewById(R.id.s_settings_MediaControls);
        sShowSongTitle = ((android.app.Activity) getContext()).findViewById(R.id.s_show_song_title);
        llShowSongTitleContainer = ((android.app.Activity) getContext()).findViewById(R.id.ll_show_song_title_container);
        
        // Workout positions container
        llWorkoutPositions = ((android.app.Activity) getContext()).findViewById(R.id.ll_settings_WorkoutPositions);
        
        // Time display
        tvTimeDisplay = ((android.app.Activity) getContext()).findViewById(R.id.tv_settings_Time);
        
        // Notification day buttons
        btnSu = ((android.app.Activity) getContext()).findViewById(R.id.btn_settings_notificationDaySU);
        btnM = ((android.app.Activity) getContext()).findViewById(R.id.btn_settings_notificationDayM);
        btnTu = ((android.app.Activity) getContext()).findViewById(R.id.btn_settings_notificationDayTU);
        btnW = ((android.app.Activity) getContext()).findViewById(R.id.btn_settings_notificationDayW);
        btnTh = ((android.app.Activity) getContext()).findViewById(R.id.btn_settings_notificationDayTH);
        btnF = ((android.app.Activity) getContext()).findViewById(R.id.btn_settings_notificationDayF);
        btnSa = ((android.app.Activity) getContext()).findViewById(R.id.btn_settings_notificationDaySA);
        
        // Backup UI elements
        switchAutoBackup = ((android.app.Activity) getContext()).findViewById(R.id.s_auto_backup);
        tvBackupStatus = ((android.app.Activity) getContext()).findViewById(R.id.tv_backup_status);
        
        // Display settings switches
        sShowTimeEstimate = ((android.app.Activity) getContext()).findViewById(R.id.s_show_time_estimate);
        sShowStatsCards = ((android.app.Activity) getContext()).findViewById(R.id.s_show_stats_cards);
    }
    
    /**
     * Show reset to defaults confirmation dialog
     */
    public void showResetToDefaultsDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        notifyResetToDefaults();
                        showInfoMessage("Workout Data Deleted");
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        showInfoMessage("Nothing was deleted");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builder.setMessage("All Workouts Will Be Deleted?")
                .setTitle("Reset to Defaults")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
    
    /**
     * Show export backup confirmation dialog
     */
    public void showExportBackupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builder.setTitle("Export Backup")
                .setMessage("Create a backup of your workout data?\n\nSaved to: Documents/AllWorkouts_Backups\n(Persists across uninstalls)")
                .setPositiveButton("Export", (dialog, which) -> notifyExportBackup())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show import backup file selection dialog
     */
    public void showImportBackupDialog(File[] backupFiles) {
        // Handle null or empty backup list - show browse options only
        if (backupFiles == null || backupFiles.length == 0) {
            showNoBackupsFoundDialog();
            return;
        }
        
        // Sort by date descending (newest first) - backupFiles should already be sorted but ensure it
        java.util.Arrays.sort(backupFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        
        // Build display items for backups only
        String[] displayItems = new String[backupFiles.length];
        for (int i = 0; i < backupFiles.length; i++) {
            long lastModified = backupFiles[i].lastModified();
            String date = android.text.format.DateFormat.format("EEE, MMM dd yyyy", lastModified).toString();
            String time = android.text.format.DateFormat.format("hh:mm a", lastModified).toString();
            displayItems[i] = "━━━━━━━━━━━━━━━━━━━━\n" + date + "  •  " + time;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builder.setTitle("Select Backup to Import")
                .setItems(displayItems, (dialog, which) -> {
                    showImportConfirmationDialog(backupFiles[which]);
                })
                .setPositiveButton("Select Folder", (dialog, which) -> notifyBrowseForBackupFolder())
                .setNeutralButton("Browse File", (dialog, which) -> notifyBrowseForBackupFile())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show dialog when no backups are found with helpful info
     */
    private void showNoBackupsFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builder.setTitle("No Backups Found")
                .setMessage("No backup files found from this app install.\n\nSelect a backup folder or file from a previous install.")
                .setPositiveButton("Select Folder", (dialog, which) -> {
                    notifyBrowseForBackupFolder();
                })
                .setNeutralButton("Select File", (dialog, which) -> {
                    notifyBrowseForBackupFile();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show confirmation dialog before importing backup
     */
    private void showImportConfirmationDialog(File backupFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builder.setTitle("Import Backup")
                .setMessage("This will replace all current workout data and settings with the selected backup. This action cannot be undone.\n\nContinue with import?")
                .setPositiveButton("Import", (dialog, which) -> {
                    notifyImportBackup(backupFile);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show backup location information dialog
     */
    public void showBackupLocationInfo(String fileName, Runnable onOpenDownloads) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builder.setTitle("Backup Created")
                .setMessage("Backup saved to:\nDocuments/AllWorkouts_Backups/\n" + fileName + "\n\nThis backup will persist even if you uninstall the app.")
                .setPositiveButton("OK", null)
                .show();
    }
    
    /**
     * Show restart recommendation dialog after import
     */
    public void showRestartRecommendation(Runnable onRestart) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        builder.setTitle("Import Complete")
                .setMessage("Backup has been imported successfully. It's recommended to restart the app to ensure all changes take effect.")
                .setPositiveButton("Restart App", (dialog, which) -> {
                    if (onRestart != null) {
                        onRestart.run();
                    }
                })
                .setNegativeButton("Continue", null)
                .show();
    }
    
    /**
     * Update backup status display text
     */
    public void updateBackupStatus(File[] backupFiles) {
        if (backupFiles == null || backupFiles.length == 0) {
            tvBackupStatus.setText("No backups yet");
        } else {
            String status = backupFiles.length + " backup" + (backupFiles.length > 1 ? "s" : "") + " available";
            if (backupFiles.length > 0) {
                long lastModified = backupFiles[0].lastModified();
                String lastBackupDate = android.text.format.DateFormat.format("MMM dd, yyyy", lastModified).toString();
                status += " (Last: " + lastBackupDate + ")";
            }
            tvBackupStatus.setText(status);
        }
    }
    
    /**
     * Update backup status with SAF folder info
     */
    public void updateBackupStatusWithFolder(int count, long newestTime, String folderName) {
        if (count == 0) {
            tvBackupStatus.setText("No backups in " + folderName);
        } else {
            String status = count + " backup" + (count > 1 ? "s" : "") + " in " + folderName;
            if (newestTime > 0) {
                String lastBackupDate = android.text.format.DateFormat.format("MMM dd, yyyy", newestTime).toString();
                status += "\nLast: " + lastBackupDate;
            }
            tvBackupStatus.setText(status);
        }
    }
    
    /**
     * Update auto backup switch state
     */
    public void updateAutoBackupSwitch(boolean enabled) {
        switchAutoBackup.setChecked(enabled);
    }
    
    /**
     * Set auto backup switch listener
     */
    public void setAutoBackupSwitchListener(android.widget.CompoundButton.OnCheckedChangeListener listener) {
        switchAutoBackup.setOnCheckedChangeListener(listener);
    }
    
    /**
     * Handle notification time update through settings manager
     */
    public void handleNotificationTimeClick(View view) {
        settingsManager.update_NotificationTime(view);
    }
    
    /**
     * Handle day notification update through settings manager
     */
    public void handleDayNotificationClick(View view) {
        settingsManager.update_DayOfNotification((Button) view);
    }
    
    /**
     * Notification methods for SettingsUICallback
     */
    private void notifyResetToDefaults() {
        if (getCallback() instanceof SettingsUICallback) {
            ((SettingsUICallback) getCallback()).onResetToDefaults();
        }
    }
    
    private void notifyExportBackup() {
        if (getCallback() instanceof SettingsUICallback) {
            ((SettingsUICallback) getCallback()).onExportBackup();
        }
    }
    
    private void notifyImportBackup(File backupFile) {
        if (getCallback() instanceof SettingsUICallback) {
            ((SettingsUICallback) getCallback()).onImportBackup(backupFile);
        }
    }
    
    private void notifyBrowseForBackupFile() {
        if (getCallback() instanceof SettingsUICallback) {
            ((SettingsUICallback) getCallback()).onBrowseForBackupFile();
        }
    }
    
    private void notifyBrowseForBackupFolder() {
        if (getCallback() instanceof SettingsUICallback) {
            ((SettingsUICallback) getCallback()).onBrowseForBackupFolder();
        }
    }
    
    private void notifyMediaControlsToggled(boolean enabled) {
        if (getCallback() instanceof SettingsUICallback) {
            ((SettingsUICallback) getCallback()).onMediaControlsToggled(enabled);
        }
    }
    
    /**
     * Override to provide custom dark theme styling
     */
    @Override
    protected void showDarkToast(String message) {
        View rootView = ((android.app.Activity) getContext()).findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(getContext().getResources().getColor(R.color.colorAccent));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getContext().getResources().getColor(R.color.background_elevated));
            snackbar.show();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}