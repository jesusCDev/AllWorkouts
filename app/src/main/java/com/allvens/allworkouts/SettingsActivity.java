package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

import com.allvens.allworkouts.managers.SettingsDataManager;
import com.allvens.allworkouts.ui.SettingsActivityUIManager;

public class SettingsActivity extends AppCompatActivity 
    implements SettingsActivityUIManager.SettingsUICallback,
               SettingsDataManager.SettingsDataCallback {

    private SettingsActivityUIManager uiManager;
    private SettingsDataManager dataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_settings);

        // Initialize managers
        uiManager = new SettingsActivityUIManager(this, this);
        dataManager = new SettingsDataManager(this, this);
        
        // Setup UI
        uiManager.initializeViews();
        uiManager.setupViews();
        uiManager.setupListeners();
        
        // Initialize data and backup status
        dataManager.initialize();
        setupBackupUI();
    }

    /****************************************
     /**** BUTTON ACTIONS (Activity Methods)
     ****************************************/
    public void btnAction_ShowDocumentation(View view) {
        onShowDocumentation();
    }

    public void btnAction_ResetToDefaults(View view) {
        uiManager.showResetToDefaultsDialog();
    }

    public void btnAction_SetNotificationTime(View view) {
        uiManager.handleNotificationTimeClick(view);
    }

    public void btnAction_setDayNotifications(View view) {
        uiManager.handleDayNotificationClick(view);
    }

    public void btnAction_ExportBackup(View view) {
        uiManager.showExportBackupDialog();
    }

    public void btnAction_ImportBackup(View view) {
        File[] backupFiles = dataManager.getExistingBackups();
        uiManager.showImportBackupDialog(backupFiles);
    }
    
    /****************************************
     /**** INTERFACE IMPLEMENTATIONS
     ****************************************/
    
    // SettingsUICallback implementations
    @Override
    public void onResetToDefaults() {
        dataManager.resetToDefaults();
    }
    
    @Override
    public void onNotificationTimeClicked(View view) {
        // Handled by UI manager directly through SettingsManager
    }
    
    @Override
    public void onDayNotificationClicked(View view) {
        // Handled by UI manager directly through SettingsManager
    }
    
    @Override
    public void onExportBackup() {
        dataManager.performBackupExport();
    }
    
    @Override
    public void onImportBackup(File backupFile) {
        dataManager.performBackupImport(backupFile);
    }
    
    @Override
    public void onShowDocumentation() {
        startActivity(new Intent(this, SettingsAppInfoSelectorActivity.class));
    }
    
    // SettingsDataCallback implementations
    @Override
    public void onOperationSuccess(String message) {
        // Handle specific backup operations with additional UI flows
        if (message.startsWith("Backup created:")) {
            handleBackupExportSuccess(message);
        } else if (message.equals("Backup imported successfully")) {
            handleBackupImportSuccess(message);
        } else {
            // Standard success message handling
            uiManager.showSuccessMessage(message);
        }
    }
    
    @Override
    public void onOperationError(String message) {
        uiManager.showErrorMessage(message);
    }
    
    @Override
    public void onBackupStatusChanged(File[] backupFiles) {
        uiManager.updateBackupStatus(backupFiles);
    }
    
    /****************************************
     /**** BACKUP SETUP
     ****************************************/
    
    /**
     * Setup backup UI components and listeners
     */
    private void setupBackupUI() {
        // Load current auto backup setting
        boolean autoBackupEnabled = dataManager.isAutoBackupEnabled();
        uiManager.updateAutoBackupSwitch(autoBackupEnabled);
        
        // Setup auto backup switch listener
        uiManager.setAutoBackupSwitchListener((buttonView, isChecked) -> {
            dataManager.setAutoBackupEnabled(isChecked);
        });
    }
    
    /****************************************
     /**** ADDITIONAL UI ACTIONS
     ****************************************/
    
    /**
     * Show backup location info after successful export
     * Called with delay from export success callback
     */
    private void showBackupLocationInfo(String fileName) {
        uiManager.showBackupLocationInfo(fileName, () -> {
            dataManager.openDownloadsFolder();
        });
    }
    
    /**
     * Show restart recommendation after successful import
     * Called with delay from import success callback  
     */
    private void showRestartRecommendation() {
        uiManager.showRestartRecommendation(() -> {
            dataManager.restartApp();
        });
    }
    
    /**
     * Handle successful backup export with location info
     */
    private void handleBackupExportSuccess(String message) {
        uiManager.showSuccessMessage(message);
        
        // Show backup location info after delay
        new Handler().postDelayed(() -> {
            // Extract filename from success message  
            String fileName = message.replace("Backup created: ", "");
            showBackupLocationInfo(fileName);
        }, 1500);
    }
    
    /**
     * Handle successful backup import with restart recommendation
     */
    private void handleBackupImportSuccess(String message) {
        uiManager.showSuccessMessage(message);
        
        // Show restart recommendation after delay
        new Handler().postDelayed(() -> {
            showRestartRecommendation();
        }, 1000);
    }
}
