package com.allvens.allworkouts;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.data_manager.backup.BackupManager;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.settings_manager.Settings_Manager;

public class SettingsActivity extends AppCompatActivity{

    private Settings_Manager settings_manager;
    private BackupManager backupManager;
    
    // Backup UI elements
    private Switch switchAutoBackup;
    private TextView tvBackupStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_settings);

        LinearLayout ll_settings_WorkoutPositions = findViewById(R.id.ll_settings_WorkoutPositions);

        Switch sVibrate       = findViewById(R.id.s_settings_Vibrate);
        Switch sSound         = findViewById(R.id.s_settings_Sound);
        Switch sNotification  = findViewById(R.id.s_settings_Notification);
        Switch sMediaControls = findViewById(R.id.s_settings_MediaControls);

        TextView tvTimeDisplay = findViewById(R.id.tv_settings_Time);

        Button btnSu = findViewById(R.id.btn_settings_notificationDaySU);
        Button btnM  = findViewById(R.id.btn_settings_notificationDayM);
        Button btnTu = findViewById(R.id.btn_settings_notificationDayTU);
        Button btnW  = findViewById(R.id.btn_settings_notificationDayW);
        Button btnTh = findViewById(R.id.btn_settings_notificationDayTH);
        Button btnF  = findViewById(R.id.btn_settings_notificationDayF);
        Button btnSa = findViewById(R.id.btn_settings_notificationDaySA);
        
        // Initialize backup UI elements
        switchAutoBackup = findViewById(R.id.s_auto_backup);
        tvBackupStatus = findViewById(R.id.tv_backup_status);

        settings_manager = new Settings_Manager(this);
        backupManager = new BackupManager(this);

        settings_manager.set_SettingsValues(sVibrate, sSound, sNotification, sMediaControls);
        settings_manager.setUp_WorkoutsAndPositions(ll_settings_WorkoutPositions);
        settings_manager.setUp_TimeDisplay(tvTimeDisplay);
        settings_manager.setUP_DailyNotificationBtns(btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa);

        sVibrate.setOnCheckedChangeListener(settings_manager.update_PrefSettings(Preferences_Values.VIBRATE_ON));
        sSound.setOnCheckedChangeListener(settings_manager.update_PrefSettings(Preferences_Values.SOUND_ON));
        sMediaControls.setOnCheckedChangeListener(settings_manager.update_PrefSettings(Preferences_Values.MEDIA_CONTROLS_ON));

        sNotification.setOnCheckedChangeListener(settings_manager.update_NotfiSettings(Preferences_Values.NOTIFICATION_ON));
        
        // Setup backup functionality
        setupBackupUI();
    }

    /****************************************
     /**** BUTTON ACTIONS
     ****************************************/
    public void btnAction_ShowDocumentation(View view){
        startActivity(new Intent(this, Settings_AppInfo_SelectorActivity.class));
    }

    /********** Settings Switch Value Changes **********/
    public void btnAction_ResetToDefaults(View view){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        WorkoutWrapper wrapper = new WorkoutWrapper(SettingsActivity.this);
                        wrapper.open();
                        wrapper.deleteAllWorkouts();
                        wrapper.deleteAllHistoryWorkouts();
                        wrapper.close();
                        showDarkToast("Workout Data Deleted");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        showDarkToast("Nothing was deleted");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);

        builder.setMessage("All Workouts Will Be Deleted?")
                .setTitle("Reset to Defaults")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    public void btnAction_SetNotificationTime(View view){
        settings_manager.update_NotificationTime(view);
    }

    public void btnAction_setDayNotifications(View view){
        settings_manager.update_DayOfNotification(((Button)view));
    }
    
    /**
     * Show a toast message with proper dark theme styling
     */
    private void showDarkToast(String message) {
        // Try to use Snackbar for better theming support
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
            // Style the snackbar with dark colors
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.background_elevated));
            snackbar.show();
        } else {
            // Fallback to regular toast if snackbar fails
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /****************************************
     /**** BACKUP & RESTORE FUNCTIONALITY
     ****************************************/
    
    /**
     * Setup backup UI components and listeners
     */
    private void setupBackupUI() {
        // Load current auto backup setting
        boolean autoBackupEnabled = backupManager.isAutoBackupEnabled();
        switchAutoBackup.setChecked(autoBackupEnabled);
        
        // Setup auto backup switch listener
        switchAutoBackup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            backupManager.setAutoBackupEnabled(isChecked);
            showDarkToast(isChecked ? "Automatic backups enabled" : "Automatic backups disabled");
        });
        
        // Update backup status display
        updateBackupStatusDisplay();
    }
    
    /**
     * Update the backup status text display
     */
    private void updateBackupStatusDisplay() {
        File[] backupFiles = backupManager.getExistingBackups();
        if (backupFiles.length == 0) {
            tvBackupStatus.setText("No backups yet");
        } else {
            String status = backupFiles.length + " backup" + (backupFiles.length > 1 ? "s" : "") + " available";
            if (backupFiles.length > 0) {
                // Show date of most recent backup
                long lastModified = backupFiles[0].lastModified(); // Assuming sorted by date
                String lastBackupDate = android.text.format.DateFormat.format("MMM dd, yyyy", lastModified).toString();
                status += " (Last: " + lastBackupDate + ")";
            }
            tvBackupStatus.setText(status);
        }
    }
    
    /**
     * Export backup button action
     */
    public void btnAction_ExportBackup(View view) {
        // Show confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);
        builder.setTitle("Export Backup")
                .setMessage("Create a backup of your workout data and settings to Downloads folder?")
                .setPositiveButton("Export", (dialog, which) -> {
                    performBackupExport();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Import backup button action
     */
    public void btnAction_ImportBackup(View view) {
        File[] backupFiles = backupManager.getExistingBackups();
        if (backupFiles.length == 0) {
            showDarkToast("No backup files found in Downloads folder");
            return;
        }
        
        // Show backup file selection dialog
        String[] fileNames = new String[backupFiles.length];
        for (int i = 0; i < backupFiles.length; i++) {
            String fileName = backupFiles[i].getName();
            long lastModified = backupFiles[i].lastModified();
            String date = android.text.format.DateFormat.format("MMM dd, yyyy HH:mm", lastModified).toString();
            fileNames[i] = fileName + "\n" + date;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);
        builder.setTitle("Select Backup to Import")
                .setItems(fileNames, (dialog, which) -> {
                    showImportConfirmation(backupFiles[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Perform the actual backup export
     */
    private void performBackupExport() {
        try {
            String fileName = backupManager.createBackup();
            showDarkToast("Backup created: " + fileName);
            updateBackupStatusDisplay();
            
            // Optionally show the backup file location
            new Handler().postDelayed(() -> {
                showBackupLocationInfo(fileName);
            }, 1500);
            
        } catch (Exception e) {
            showDarkToast("Failed to create backup: " + e.getMessage());
        }
    }
    
    /**
     * Show confirmation dialog before importing
     */
    private void showImportConfirmation(File backupFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);
        builder.setTitle("Import Backup")
                .setMessage("This will replace all current workout data and settings with the selected backup. This action cannot be undone.\n\nContinue with import?")
                .setPositiveButton("Import", (dialog, which) -> {
                    performBackupImport(backupFile);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Perform the actual backup import
     */
    private void performBackupImport(File backupFile) {
        try {
            boolean success = backupManager.importBackup(backupFile.getAbsolutePath());
            if (success) {
                showDarkToast("Backup imported successfully");
                
                // Show restart recommendation
                new Handler().postDelayed(() -> {
                    showRestartRecommendation();
                }, 1000);
            } else {
                showDarkToast("Failed to import backup");
            }
        } catch (Exception e) {
            showDarkToast("Import failed: " + e.getMessage());
        }
    }
    
    /**
     * Show backup file location information
     */
    private void showBackupLocationInfo(String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);
        builder.setTitle("Backup Created")
                .setMessage("Backup saved to Downloads folder:\n" + fileName + "\n\nYou can share or move this file to backup your workout data.")
                .setPositiveButton("OK", null)
                .setNeutralButton("Open Downloads", (dialog, which) -> {
                    openDownloadsFolder();
                })
                .show();
    }
    
    /**
     * Show recommendation to restart app after import
     */
    private void showRestartRecommendation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);
        builder.setTitle("Import Complete")
                .setMessage("Backup has been imported successfully. It's recommended to restart the app to ensure all changes take effect.")
                .setPositiveButton("Restart App", (dialog, which) -> {
                    restartApp();
                })
                .setNegativeButton("Continue", null)
                .show();
    }
    
    /**
     * Open Downloads folder in file manager
     */
    private void openDownloadsFolder() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            intent.setDataAndType(uri, "resource/folder");
            if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                startActivity(intent);
            } else {
                // Fallback - try to open with any file manager
                Intent fallbackIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fallbackIntent.setType("*/*");
                startActivity(Intent.createChooser(fallbackIntent, "Open Downloads"));
            }
        } catch (Exception e) {
            showDarkToast("Could not open Downloads folder");
        }
    }
    
    /**
     * Restart the app
     */
    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
