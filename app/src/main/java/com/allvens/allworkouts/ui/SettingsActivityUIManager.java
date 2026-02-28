package com.allvens.allworkouts.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;
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
        void onNotificationPermissionNeeded();
    }
    
    private SettingsManager settingsManager;
    private SharedPreferences prefs;

    // Collapsible card views
    private View llHeaderWorkouts, llHeaderHomeScreen, llHeaderWorkoutSession;
    private View llHeaderFeedback, llHeaderNotification, llHeaderBackup;
    private View llContentWorkouts, llContentHomeScreen, llContentWorkoutSession;
    private View llContentFeedback, llContentNotification, llContentBackup;
    private ImageView ivChevronWorkouts, ivChevronHomeScreen, ivChevronWorkoutSession;
    private ImageView ivChevronFeedback, ivChevronNotification, ivChevronBackup;

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
    private Switch sShowMediaBackground;
    private View llShowMediaBackgroundContainer;
    
    // Display settings switches
    private Switch sShowTimeEstimate;
    private Switch sShowStatsCards;
    private Switch sShowGoals;
    private Switch sShowCalendarNav;
    
    // Workout session settings switches
    private Switch sShowDifficultySlider;
    private Switch sShowExtraBreak;
    private Switch sCompleteButtonTop;
    private Switch sCombinedRoutine;
    
    // Notification day buttons
    private Button btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa;
    private TextView tvTimeDisplay;

    // Test notification
    private Button btnTestNotification;
    private TextView tvTestCountdown;
    private Handler feedbackHandler;

    public SettingsActivityUIManager(Context context, SettingsUICallback callback) {
        super(context, callback);
        this.settingsManager = new SettingsManager(context);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    @Override
    protected void setupViews() {
        setupCollapsibleCards();
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
        settingsManager.set_DisplaySettingsValues(sShowTimeEstimate, sShowStatsCards, sShowGoals, sShowCalendarNav);

        // Setup workout session settings (default to true if not set, complete button top defaults to false)
        settingsManager.set_WorkoutSessionSettingsValues(sShowDifficultySlider, sShowExtraBreak, sCompleteButtonTop, sCombinedRoutine);

        // Setup media sub-settings
        settingsManager.set_MediaSubSettings(sShowMediaBackground);

        // Show/hide song title sub-setting based on media controls state
        updateSongTitleSettingVisibility(sMediaControls.isChecked());
    }
    
    private void setupCollapsibleCards() {
        setupCollapsibleCard("settings_collapsed_workouts", llHeaderWorkouts, llContentWorkouts, ivChevronWorkouts);
        setupCollapsibleCard("settings_collapsed_home_screen", llHeaderHomeScreen, llContentHomeScreen, ivChevronHomeScreen);
        setupCollapsibleCard("settings_collapsed_workout_session", llHeaderWorkoutSession, llContentWorkoutSession, ivChevronWorkoutSession);
        setupCollapsibleCard("settings_collapsed_feedback", llHeaderFeedback, llContentFeedback, ivChevronFeedback);
        setupCollapsibleCard("settings_collapsed_notification", llHeaderNotification, llContentNotification, ivChevronNotification);
        setupCollapsibleCard("settings_collapsed_backup", llHeaderBackup, llContentBackup, ivChevronBackup);
    }

    private void setupCollapsibleCard(String prefKey, View headerRow, View contentContainer, ImageView chevron) {
        boolean collapsed = prefs.getBoolean(prefKey, false);
        contentContainer.setVisibility(collapsed ? View.GONE : View.VISIBLE);
        chevron.setRotation(collapsed ? 180f : 0f);

        headerRow.setOnClickListener(v -> {
            boolean isVisible = contentContainer.getVisibility() == View.VISIBLE;
            contentContainer.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            chevron.animate().rotation(isVisible ? 180f : 0f).setDuration(200).start();
            prefs.edit().putBoolean(prefKey, isVisible).apply();
        });
    }

    /**
     * Update visibility of media sub-settings based on media controls state
     */
    private void updateSongTitleSettingVisibility(boolean mediaControlsEnabled) {
        if (llShowSongTitleContainer != null) {
            llShowSongTitleContainer.setVisibility(mediaControlsEnabled ? View.VISIBLE : View.GONE);
        }
        if (llShowMediaBackgroundContainer != null) {
            llShowMediaBackgroundContainer.setVisibility(mediaControlsEnabled ? View.VISIBLE : View.GONE);
        }
    }
    
    @Override
    protected void setupListeners() {
        // Settings switches listeners (delegate to SettingsManager)
        sVibrate.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.VIBRATE_ON));
        sSound.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SOUND_ON));

        // Notification toggle with POST_NOTIFICATIONS permission check
        sNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS)
                       != PackageManager.PERMISSION_GRANTED) {
                // Revert without triggering listener again
                sNotification.setOnCheckedChangeListener(null);
                sNotification.setChecked(false);
                setupNotificationSwitchListener();
                notifyNotificationPermissionNeeded();
                return;
            }
            settingsManager.update_NotfiSettings(PreferencesValues.NOTIFICATION_ON)
                    .onCheckedChanged(buttonView, isChecked);
        });
        
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
        sShowGoals.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SHOW_GOALS));
        sShowCalendarNav.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SHOW_CALENDAR_NAV));

        // Workout session settings listeners
        sShowDifficultySlider.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SHOW_DIFFICULTY_SLIDER));
        sShowExtraBreak.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SHOW_EXTRA_BREAK));
        sCompleteButtonTop.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.COMPLETE_BUTTON_TOP));
        sCombinedRoutine.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.COMBINED_ROUTINE_MODE));

        // Media background setting listener
        sShowMediaBackground.setOnCheckedChangeListener(settingsManager.update_PrefSettings(PreferencesValues.SHOW_MEDIA_BACKGROUND));
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
        if (feedbackHandler != null) {
            feedbackHandler.removeCallbacksAndMessages(null);
        }
        feedbackHandler = null;

        // Cleanup view references
        switchAutoBackup = null;
        tvBackupStatus = null;
        llWorkoutPositions = null;
        sVibrate = null;
        sSound = null;
        sNotification = null;
        sMediaControls = null;
        sShowCalendarNav = null;
        sCombinedRoutine = null;
        btnSu = btnM = btnTu = btnW = btnTh = btnF = btnSa = null;
        tvTimeDisplay = null;
        btnTestNotification = null;
        tvTestCountdown = null;

        // Collapsible card views
        llHeaderWorkouts = llHeaderHomeScreen = llHeaderWorkoutSession = null;
        llHeaderFeedback = llHeaderNotification = llHeaderBackup = null;
        llContentWorkouts = llContentHomeScreen = llContentWorkoutSession = null;
        llContentFeedback = llContentNotification = llContentBackup = null;
        ivChevronWorkouts = ivChevronHomeScreen = ivChevronWorkoutSession = null;
        ivChevronFeedback = ivChevronNotification = ivChevronBackup = null;
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
        sShowMediaBackground = ((android.app.Activity) getContext()).findViewById(R.id.s_show_media_background);
        llShowMediaBackgroundContainer = ((android.app.Activity) getContext()).findViewById(R.id.ll_show_media_background_container);
        
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
        sShowGoals = ((android.app.Activity) getContext()).findViewById(R.id.s_show_goals);
        sShowCalendarNav = ((android.app.Activity) getContext()).findViewById(R.id.s_show_calendar_nav);

        // Workout session settings switches
        sShowDifficultySlider = ((android.app.Activity) getContext()).findViewById(R.id.s_show_difficulty_slider);
        sShowExtraBreak = ((android.app.Activity) getContext()).findViewById(R.id.s_show_extra_break);
        sCompleteButtonTop = ((android.app.Activity) getContext()).findViewById(R.id.s_complete_button_top);
        sCombinedRoutine = ((android.app.Activity) getContext()).findViewById(R.id.s_combined_routine);

        // Test notification
        btnTestNotification = ((android.app.Activity) getContext()).findViewById(R.id.btn_test_notification);
        tvTestCountdown = ((android.app.Activity) getContext()).findViewById(R.id.tv_test_countdown);

        // Collapsible card headers
        llHeaderWorkouts = ((android.app.Activity) getContext()).findViewById(R.id.ll_header_workouts);
        llHeaderHomeScreen = ((android.app.Activity) getContext()).findViewById(R.id.ll_header_home_screen);
        llHeaderWorkoutSession = ((android.app.Activity) getContext()).findViewById(R.id.ll_header_workout_session);
        llHeaderFeedback = ((android.app.Activity) getContext()).findViewById(R.id.ll_header_feedback);
        llHeaderNotification = ((android.app.Activity) getContext()).findViewById(R.id.ll_header_notification);
        llHeaderBackup = ((android.app.Activity) getContext()).findViewById(R.id.ll_header_backup);

        // Collapsible card content containers
        llContentWorkouts = ((android.app.Activity) getContext()).findViewById(R.id.ll_content_workouts);
        llContentHomeScreen = ((android.app.Activity) getContext()).findViewById(R.id.ll_content_home_screen);
        llContentWorkoutSession = ((android.app.Activity) getContext()).findViewById(R.id.ll_content_workout_session);
        llContentFeedback = ((android.app.Activity) getContext()).findViewById(R.id.ll_content_feedback);
        llContentNotification = ((android.app.Activity) getContext()).findViewById(R.id.ll_content_notification);
        llContentBackup = ((android.app.Activity) getContext()).findViewById(R.id.ll_content_backup);

        // Collapsible card chevrons
        ivChevronWorkouts = ((android.app.Activity) getContext()).findViewById(R.id.iv_chevron_workouts);
        ivChevronHomeScreen = ((android.app.Activity) getContext()).findViewById(R.id.iv_chevron_home_screen);
        ivChevronWorkoutSession = ((android.app.Activity) getContext()).findViewById(R.id.iv_chevron_workout_session);
        ivChevronFeedback = ((android.app.Activity) getContext()).findViewById(R.id.iv_chevron_feedback);
        ivChevronNotification = ((android.app.Activity) getContext()).findViewById(R.id.iv_chevron_notification);
        ivChevronBackup = ((android.app.Activity) getContext()).findViewById(R.id.iv_chevron_backup);
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
     * Handle test notification button press — fires notification immediately
     */
    public void handleTestNotification() {
        boolean sent = settingsManager.sendTestNotification();
        if (!sent) {
            showInfoMessage("Failed to send test notification");
            return;
        }

        btnTestNotification.setEnabled(false);
        tvTestCountdown.setVisibility(View.VISIBLE);
        tvTestCountdown.setText("Notification sent!");

        if (feedbackHandler != null) {
            feedbackHandler.removeCallbacksAndMessages(null);
        }
        feedbackHandler = new Handler();
        feedbackHandler.postDelayed(() -> {
            if (btnTestNotification != null) btnTestNotification.setEnabled(true);
            if (tvTestCountdown != null) tvTestCountdown.setVisibility(View.GONE);
        }, 3000);
    }

    /**
     * Re-apply the notification switch listener (used after programmatic setChecked)
     */
    private void setupNotificationSwitchListener() {
        sNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS)
                       != PackageManager.PERMISSION_GRANTED) {
                sNotification.setOnCheckedChangeListener(null);
                sNotification.setChecked(false);
                setupNotificationSwitchListener();
                notifyNotificationPermissionNeeded();
                return;
            }
            settingsManager.update_NotfiSettings(PreferencesValues.NOTIFICATION_ON)
                    .onCheckedChanged(buttonView, isChecked);
        });
    }

    /**
     * Enable the notification switch after permission is granted
     */
    public void enableNotificationSwitch() {
        sNotification.setChecked(true);
    }

    private void notifyNotificationPermissionNeeded() {
        if (getCallback() instanceof SettingsUICallback) {
            ((SettingsUICallback) getCallback()).onNotificationPermissionNeeded();
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