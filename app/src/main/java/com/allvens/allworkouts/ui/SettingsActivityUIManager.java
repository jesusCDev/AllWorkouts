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
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.settings_manager.SettingsManager;

import java.io.File;

/**
 * UI Manager for SettingsActivity
 * Handles all view binding, setup, styling, and UI state management
 */
public class SettingsActivityUIManager {
    
    public interface SettingsUICallback {
        void onResetToDefaults();
        void onNotificationTimeClicked(View view);
        void onDayNotificationClicked(View view);
        void onExportBackup();
        void onImportBackup(File backupFile);
        void onShowDocumentation();
    }
    
    private Context context;
    private SettingsUICallback callback;
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
    
    // Notification day buttons
    private Button btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa;
    private TextView tvTimeDisplay;

    public SettingsActivityUIManager(Context context, SettingsUICallback callback) {
        this.context = context;
        this.callback = callback;
        this.settingsManager = new SettingsManager(context);
    }
    
    /**
     * Initialize all views and bind them to the UI manager
     */
    public void initializeViews() {
        // Main settings switches
        sVibrate = ((android.app.Activity) context).findViewById(R.id.s_settings_Vibrate);
        sSound = ((android.app.Activity) context).findViewById(R.id.s_settings_Sound);
        sNotification = ((android.app.Activity) context).findViewById(R.id.s_settings_Notification);
        sMediaControls = ((android.app.Activity) context).findViewById(R.id.s_settings_MediaControls);
        
        // Workout positions container
        llWorkoutPositions = ((android.app.Activity) context).findViewById(R.id.ll_settings_WorkoutPositions);
        
        // Time display
        tvTimeDisplay = ((android.app.Activity) context).findViewById(R.id.tv_settings_Time);
        
        // Notification day buttons
        btnSu = ((android.app.Activity) context).findViewById(R.id.btn_settings_notificationDaySU);
        btnM = ((android.app.Activity) context).findViewById(R.id.btn_settings_notificationDayM);
        btnTu = ((android.app.Activity) context).findViewById(R.id.btn_settings_notificationDayTU);
        btnW = ((android.app.Activity) context).findViewById(R.id.btn_settings_notificationDayW);
        btnTh = ((android.app.Activity) context).findViewById(R.id.btn_settings_notificationDayTH);
        btnF = ((android.app.Activity) context).findViewById(R.id.btn_settings_notificationDayF);
        btnSa = ((android.app.Activity) context).findViewById(R.id.btn_settings_notificationDaySA);
        
        // Backup UI elements
        switchAutoBackup = ((android.app.Activity) context).findViewById(R.id.s_auto_backup);
        tvBackupStatus = ((android.app.Activity) context).findViewById(R.id.tv_backup_status);
    }
    
    /**
     * Setup all views with their initial states and listeners
     */
    public void setupViews() {
        // Setup settings values using the existing SettingsManager
        settingsManager.set_SettingsValues(sVibrate, sSound, sNotification, sMediaControls);
        settingsManager.setUp_WorkoutsAndPositions(llWorkoutPositions);
        settingsManager.setUp_TimeDisplay(tvTimeDisplay);
        settingsManager.setUP_DailyNotificationBtns(btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa);
    }
    
    /**
     * Setup listeners for all interactive elements
     */
    public void setupListeners() {
        // Settings switches listeners (delegate to SettingsManager)
        sVibrate.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.VIBRATE_ON));
        sSound.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SOUND_ON));
        sMediaControls.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.MEDIA_CONTROLS_ON));
        sNotification.setOnCheckedChangeListener(settingsManager.update_NotfiSettings(PreferencesValues.NOTIFICATION_ON));
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
                        callback.onResetToDefaults();
                        showDarkToast("Workout Data Deleted");
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        showDarkToast("Nothing was deleted");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
        builder.setTitle("Export Backup")
                .setMessage("Create a backup of your workout data and settings to Downloads folder?")
                .setPositiveButton("Export", (dialog, which) -> callback.onExportBackup())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show import backup file selection dialog
     */
    public void showImportBackupDialog(File[] backupFiles) {
        if (backupFiles.length == 0) {
            showDarkToast("No backup files found in Downloads folder");
            return;
        }
        
        String[] fileNames = new String[backupFiles.length];
        for (int i = 0; i < backupFiles.length; i++) {
            String fileName = backupFiles[i].getName();
            long lastModified = backupFiles[i].lastModified();
            String date = android.text.format.DateFormat.format("MMM dd, yyyy HH:mm", lastModified).toString();
            fileNames[i] = fileName + "\n" + date;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
        builder.setTitle("Select Backup to Import")
                .setItems(fileNames, (dialog, which) -> {
                    showImportConfirmationDialog(backupFiles[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show confirmation dialog before importing backup
     */
    private void showImportConfirmationDialog(File backupFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
        builder.setTitle("Import Backup")
                .setMessage("This will replace all current workout data and settings with the selected backup. This action cannot be undone.\n\nContinue with import?")
                .setPositiveButton("Import", (dialog, which) -> {
                    callback.onImportBackup(backupFile);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show backup location information dialog
     */
    public void showBackupLocationInfo(String fileName, Runnable onOpenDownloads) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
        builder.setTitle("Backup Created")
                .setMessage("Backup saved to Downloads folder:\n" + fileName + "\n\nYou can share or move this file to backup your workout data.")
                .setPositiveButton("OK", null)
                .setNeutralButton("Open Downloads", (dialog, which) -> {
                    if (onOpenDownloads != null) {
                        onOpenDownloads.run();
                    }
                })
                .show();
    }
    
    /**
     * Show restart recommendation dialog after import
     */
    public void showRestartRecommendation(Runnable onRestart) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
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
        if (backupFiles.length == 0) {
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
     * Show toast message with proper dark theme styling
     */
    public void showDarkToast(String message) {
        View rootView = ((android.app.Activity) context).findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(context.getResources().getColor(R.color.colorAccent));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(context.getResources().getColor(R.color.background_elevated));
            snackbar.show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Show success message for operations
     */
    public void showSuccessMessage(String message) {
        showDarkToast(message);
    }
    
    /**
     * Show error message for failed operations
     */
    public void showErrorMessage(String message) {
        showDarkToast(message);
    }
}