package com.allvens.allworkouts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;

import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.content.ContentResolver;

import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;
import com.allvens.allworkouts.managers.SettingsDataManager;
import com.allvens.allworkouts.ui.SettingsActivityUIManager;

public class SettingsActivity extends AppCompatActivity 
    implements SettingsActivityUIManager.SettingsUICallback,
               SettingsDataManager.SettingsDataCallback {

    private SettingsActivityUIManager uiManager;
    private SettingsDataManager dataManager;
    private SettingsPrefsManager prefsManager;
    private File[] cachedBackupFiles; // Cache backup files for consistency
    
    private static final int PICK_BACKUP_FILE = 1001;
    private static final int PICK_BACKUP_FOLDER = 1002;
    private static final int PICK_BACKUP_FOLDER_FOR_IMPORT = 1003;
    private static final int REQUEST_NOTIFICATION_PERMISSION_TEST = 2001;
    private static final int REQUEST_NOTIFICATION_PERMISSION_TOGGLE = 2002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_settings);

        // Initialize managers
        uiManager = new SettingsActivityUIManager(this, this);
        dataManager = new SettingsDataManager(this, this);
        prefsManager = new SettingsPrefsManager(this);
        
        // Setup UI
        uiManager.initialize(); // This calls initializeViews, setupViews, and setupListeners internally
        
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

    public void btnAction_TestNotification(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                   != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION_TEST);
            return;
        }
        uiManager.handleTestNotification();
    }

    public void btnAction_ExportBackup(View view) {
        uiManager.showExportBackupDialog();
    }

    public void btnAction_ImportBackup(View view) {
        // Check if we have a saved backup folder with valid permission
        if (hasValidBackupFolderUri()) {
            // Use the saved folder automatically
            String uriString = prefsManager.getPrefSettingString(PreferencesValues.BACKUP_FOLDER_URI);
            Uri folderUri = Uri.parse(uriString);
            showBackupsFromUri(folderUri);
        } else {
            // No saved folder - show standard import dialog
            File[] backupFiles = dataManager.getExistingBackups();
            uiManager.showImportBackupDialog(backupFiles);
        }
    }
    
    public void btnAction_AlignMax(View view) {
        showAlignMaxDialog();
    }

    public void btnAction_ResetToMaxDay(View view) {
        showResetToMaxDayDialog();
    }
    
    /**
     * Show dialog to align all max workouts to same day
     */
    private void showAlignMaxDialog() {
        final String[] options = {"Tomorrow", "2 days", "3 days", "4 days", "5 days", "6 days", "7 days"};
        final int[] daysFromToday = {1, 2, 3, 4, 5, 6, 7};
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.DarkAlertDialog);
        builder.setTitle("Align Max Workouts")
               .setMessage("Set all workouts to reach MAX on the same day:")
               .setItems(options, (dialog, which) -> {
                   int days = daysFromToday[which];
                   // Use WorkoutSelectionManager to align
                   com.allvens.allworkouts.managers.WorkoutSelectionManager workoutManager = 
                       new com.allvens.allworkouts.managers.WorkoutSelectionManager(this);
                   if (workoutManager.alignMaxWorkouts(days)) {
                       String message = "All workouts aligned to reach MAX in " + days + " day" + (days > 1 ? "s" : "") + "!";
                       android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show();
                   } else {
                       android.widget.Toast.makeText(this, "Failed to align workouts", android.widget.Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    /**
     * Show confirmation dialog to reset all workouts to max day
     */
    private void showResetToMaxDayDialog() {
        new android.app.AlertDialog.Builder(this, R.style.DarkAlertDialog)
                .setTitle("Reset to Max Day")
                .setMessage("This will set all workouts to max day. Your next session for each workout will ask you to set a new max.\n\nThis is useful after a long break to recalibrate to your current level.")
                .setPositiveButton("Reset", (dialog, which) -> {
                    com.allvens.allworkouts.managers.WorkoutSelectionManager workoutManager =
                            new com.allvens.allworkouts.managers.WorkoutSelectionManager(this);
                    if (workoutManager.resetToMaxDay()) {
                        android.widget.Toast.makeText(this,
                                "All workouts set to max day!", android.widget.Toast.LENGTH_LONG).show();
                    } else {
                        android.widget.Toast.makeText(this,
                                "Failed to reset workouts", android.widget.Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
    public void onBrowseForBackupFile() {
        // Use SAF to let user pick a specific backup file
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        // Also allow any file type in case JSON isn't recognized
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/json", "*/*"});
        startActivityForResult(intent, PICK_BACKUP_FILE);
    }
    
    @Override
    public void onBrowseForBackupFolder() {
        // Use SAF to let user pick a backup folder for import
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        
        // Try to start in Documents folder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri documentsUri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Documents");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentsUri);
        }
        
        startActivityForResult(intent, PICK_BACKUP_FOLDER_FOR_IMPORT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_BACKUP_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                importBackupFromUri(uri);
            }
        } else if (requestCode == PICK_BACKUP_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                handleFolderSelectedForAutoBackup(uri);
            }
        } else if (requestCode == PICK_BACKUP_FOLDER_FOR_IMPORT && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                handleFolderSelectedForImport(uri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION_TEST) {
            if (granted) {
                uiManager.handleTestNotification();
            } else {
                uiManager.showInfoMessage("Notification permission is required");
            }
        } else if (requestCode == REQUEST_NOTIFICATION_PERMISSION_TOGGLE) {
            if (granted) {
                uiManager.enableNotificationSwitch();
            } else {
                uiManager.showInfoMessage("Notification permission is required");
            }
        }
    }

    /**
     * Handle folder selection for auto-backup setup
     */
    private void handleFolderSelectedForAutoBackup(Uri folderUri) {
        // Take persistent permission
        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        getContentResolver().takePersistableUriPermission(folderUri, takeFlags);
        
        // Save the URI
        prefsManager.update_PrefSetting(PreferencesValues.BACKUP_FOLDER_URI, folderUri.toString());
        
        // Enable auto backup now that we have folder access
        dataManager.setAutoBackupEnabled(true);
        uiManager.updateAutoBackupSwitch(true);
        
        android.widget.Toast.makeText(this, "Backup folder configured! Auto-backup enabled.", android.widget.Toast.LENGTH_LONG).show();
        
        // Check for existing backups in the selected folder
        checkForBackupsInUri(folderUri);
    }
    
    /**
     * Handle folder selection for import (shows backup list immediately)
     */
    private void handleFolderSelectedForImport(Uri folderUri) {
        // Take persistent permission (for future use)
        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        try {
            getContentResolver().takePersistableUriPermission(folderUri, takeFlags);
            // Save URI for future use
            prefsManager.update_PrefSetting(PreferencesValues.BACKUP_FOLDER_URI, folderUri.toString());
        } catch (Exception e) {
            android.util.Log.w("SettingsActivity", "Could not persist folder permission: " + e.getMessage());
        }
        
        // Show backups from this folder
        showBackupsFromUri(folderUri);
    }
    
    /**
     * Show list of backups from a folder URI
     */
    private void showBackupsFromUri(Uri folderUri) {
        try {
            Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                folderUri, DocumentsContract.getTreeDocumentId(folderUri));
            
            android.database.Cursor cursor = getContentResolver().query(
                childrenUri,
                new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME, 
                             DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                             DocumentsContract.Document.COLUMN_LAST_MODIFIED},
                null, null, null);
            
            if (cursor != null) {
                java.util.ArrayList<String> docIds = new java.util.ArrayList<>();
                java.util.ArrayList<Long> times = new java.util.ArrayList<>();
                
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    if (name.startsWith("allworkouts_backup_") && name.endsWith(".json")) {
                        docIds.add(cursor.getString(1));
                        times.add(cursor.getLong(2));
                    }
                }
                cursor.close();
                
                // Get folder display name for title
                String folderName = getFolderDisplayName(folderUri);
                
                // Handle empty folder
                if (docIds.isEmpty()) {
                    new android.support.v7.app.AlertDialog.Builder(this, R.style.DarkAlertDialog)
                        .setTitle("No Backups Found")
                        .setMessage("No backup files in:\n" + folderName + "\n\nSelect a different folder or browse for a file.")
                        .setPositiveButton("Change Folder", (dialog, which) -> onBrowseForBackupFolder())
                        .setNeutralButton("Browse File", (dialog, which) -> onBrowseForBackupFile())
                        .setNegativeButton("Cancel", null)
                        .show();
                    return;
                }
                
                // Sort by date descending (newest first)
                Integer[] indices = new Integer[docIds.size()];
                for (int i = 0; i < indices.length; i++) indices[i] = i;
                java.util.Arrays.sort(indices, (a, b) -> Long.compare(times.get(b), times.get(a)));
                
                // Build sorted display items
                int backupCount = docIds.size();
                String[] displayItems = new String[backupCount];
                final String[] sortedDocIds = new String[backupCount];
                
                for (int i = 0; i < backupCount; i++) {
                    int idx = indices[i];
                    sortedDocIds[i] = docIds.get(idx);
                    long time = times.get(idx);
                    String date = android.text.format.DateFormat.format("EEE, MMM dd yyyy", time).toString();
                    String timeStr = android.text.format.DateFormat.format("hh:mm a", time).toString();
                    displayItems[i] = "━━━━━━━━━━━━━━━━━━━━\n" + date + "  •  " + timeStr;
                }
                
                // Show selection dialog with folder path and buttons at bottom
                new android.support.v7.app.AlertDialog.Builder(this, R.style.DarkAlertDialog)
                    .setTitle(backupCount + " Backups in " + folderName)
                    .setItems(displayItems, (dialog, which) -> {
                        Uri fileUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, sortedDocIds[which]);
                        importBackupFromUri(fileUri);
                    })
                    .setPositiveButton("Change Folder", (dialog, which) -> onBrowseForBackupFolder())
                    .setNeutralButton("Browse File", (dialog, which) -> onBrowseForBackupFile())
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        } catch (Exception e) {
            // Folder permission may have been revoked - show standard dialog
            android.util.Log.e("SettingsActivity", "Error reading folder: " + e.getMessage());
            File[] backupFiles = dataManager.getExistingBackups();
            uiManager.showImportBackupDialog(backupFiles);
        }
    }
    
    /**
     * Get display name for a folder URI
     */
    private String getFolderDisplayName(Uri folderUri) {
        try {
            String docId = DocumentsContract.getTreeDocumentId(folderUri);
            // Format: "primary:Documents/AllWorkouts_Backups" -> "AllWorkouts_Backups"
            if (docId.contains("/")) {
                return docId.substring(docId.lastIndexOf("/") + 1);
            } else if (docId.contains(":")) {
                return docId.substring(docId.lastIndexOf(":") + 1);
            }
            return docId;
        } catch (Exception e) {
            return "Selected Folder";
        }
    }
    
    /**
     * Check for existing backup files in the selected folder
     */
    private void checkForBackupsInUri(Uri folderUri) {
        try {
            Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                folderUri, DocumentsContract.getTreeDocumentId(folderUri));
            
            android.database.Cursor cursor = getContentResolver().query(
                childrenUri,
                new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_DOCUMENT_ID},
                null, null, null);
            
            if (cursor != null) {
                int backupCount = 0;
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    if (name.startsWith("allworkouts_backup_") && name.endsWith(".json")) {
                        backupCount++;
                    }
                }
                cursor.close();
                
                if (backupCount > 0) {
                    // Show prompt to restore
                    showRestoreFromFolderPrompt(folderUri, backupCount);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("SettingsActivity", "Error checking backups: " + e.getMessage());
        }
    }
    
    /**
     * Show prompt asking if user wants to restore from found backups
     */
    private void showRestoreFromFolderPrompt(Uri folderUri, int backupCount) {
        new android.support.v7.app.AlertDialog.Builder(this)
            .setTitle("Backups Found")
            .setMessage("Found " + backupCount + " backup file(s) in this folder. Would you like to restore from the most recent one?")
            .setPositiveButton("Restore", (dialog, which) -> {
                importMostRecentFromUri(folderUri);
            })
            .setNegativeButton("Not Now", null)
            .show();
    }
    
    /**
     * Import the most recent backup from a folder URI
     */
    private void importMostRecentFromUri(Uri folderUri) {
        try {
            Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                folderUri, DocumentsContract.getTreeDocumentId(folderUri));
            
            android.database.Cursor cursor = getContentResolver().query(
                childrenUri,
                new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME, 
                             DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                             DocumentsContract.Document.COLUMN_LAST_MODIFIED},
                null, null, null);
            
            if (cursor != null) {
                String newestDocId = null;
                long newestTime = 0;
                
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String docId = cursor.getString(1);
                    long modified = cursor.getLong(2);
                    
                    if (name.startsWith("allworkouts_backup_") && name.endsWith(".json")) {
                        if (modified > newestTime) {
                            newestTime = modified;
                            newestDocId = docId;
                        }
                    }
                }
                cursor.close();
                
                if (newestDocId != null) {
                    Uri fileUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, newestDocId);
                    importBackupFromUri(fileUri);
                }
            }
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Failed to import: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Import backup from a content URI (SAF)
     */
    private void importBackupFromUri(Uri uri) {
        try {
            // Copy the file to a temp location we can access
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                android.widget.Toast.makeText(this, "Could not open file", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create temp file
            File tempFile = new File(getCacheDir(), "temp_backup.json");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            // Import from temp file
            dataManager.performBackupImport(tempFile);
            
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Failed to import: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onShowDocumentation() {
        startActivity(new Intent(this, SettingsAppInfoSelectorActivity.class));
    }
    
    @Override
    public void onNotificationPermissionNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION_TOGGLE);
        }
    }

    @Override
    public void onMediaControlsToggled(boolean enabled) {
        if (enabled && !isNotificationListenerEnabled()) {
            // Show permission dialog
            uiManager.showNotificationListenerPermissionDialog(() -> {
                openNotificationListenerSettings();
            });
        }
    }
    
    @Override
    public boolean isNotificationListenerEnabled() {
        String pkgName = getPackageName();
        String flat = android.provider.Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(pkgName);
    }
    
    // SettingsDataCallback implementations
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
    
    public void onOperationError(String message) {
        uiManager.showErrorMessage(message);
    }
    
    @Override
    public void onBackupStatusChanged(File[] backupFiles) {
        cachedBackupFiles = backupFiles; // Cache for import button
        
        // If we have a valid SAF folder, count files from there instead
        if (hasValidBackupFolderUri()) {
            updateBackupStatusFromSafFolder();
        } else {
            uiManager.updateBackupStatus(backupFiles);
        }
    }
    
    /**
     * Update backup status by counting files in SAF folder
     */
    private void updateBackupStatusFromSafFolder() {
        try {
            String uriString = prefsManager.getPrefSettingString(PreferencesValues.BACKUP_FOLDER_URI);
            Uri folderUri = Uri.parse(uriString);
            
            Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                folderUri, DocumentsContract.getTreeDocumentId(folderUri));
            
            android.database.Cursor cursor = getContentResolver().query(
                childrenUri,
                new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME, 
                             DocumentsContract.Document.COLUMN_LAST_MODIFIED},
                null, null, null);
            
            if (cursor != null) {
                int count = 0;
                long newestTime = 0;
                
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    if (name.startsWith("allworkouts_backup_") && name.endsWith(".json")) {
                        count++;
                        long time = cursor.getLong(1);
                        if (time > newestTime) newestTime = time;
                    }
                }
                cursor.close();
                
                String folderName = getFolderDisplayName(folderUri);
                uiManager.updateBackupStatusWithFolder(count, newestTime, folderName);
            }
        } catch (Exception e) {
            android.util.Log.e("SettingsActivity", "Error counting SAF backups: " + e.getMessage());
            // Fall back to standard file list
            uiManager.updateBackupStatus(cachedBackupFiles);
        }
    }
    
    // BaseUICallback implementations
    @Override
    public void onShowMessage(String message) {
        uiManager.showInfoMessage(message);
    }
    
    @Override
    public void onShowError(String error) {
        uiManager.showErrorMessage(error);
    }
    
    @Override
    public void onNavigationRequested(Intent intent, boolean finishCurrent) {
        startActivity(intent);
        if (finishCurrent) {
            finish();
        }
    }
    
    // BaseDataCallback implementations
    @Override
    public void onDataLoaded() {
        // Data loaded successfully - refresh UI if needed
    }
    
    @Override
    public void onDataUpdated() {
        // Data updated successfully - refresh UI if needed
    }
    
    @Override
    public void onDataError(String error) {
        onShowError(error);
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
            if (isChecked) {
                // Check if we have a valid folder URI
                if (!hasValidBackupFolderUri()) {
                    // Need to select folder first
                    promptForBackupFolder();
                    // Don't enable yet - wait for folder selection
                    uiManager.updateAutoBackupSwitch(false);
                    return;
                }
            }
            dataManager.setAutoBackupEnabled(isChecked);
        });
    }
    
    /**
     * Check if we have a valid persisted backup folder URI
     */
    private boolean hasValidBackupFolderUri() {
        String uriString = prefsManager.getPrefSettingString(PreferencesValues.BACKUP_FOLDER_URI);
        if (uriString == null || uriString.isEmpty()) {
            return false;
        }
        
        // Check if permission is still valid
        Uri uri = Uri.parse(uriString);
        for (android.content.UriPermission perm : getContentResolver().getPersistedUriPermissions()) {
            if (perm.getUri().equals(uri) && perm.isReadPermission() && perm.isWritePermission()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Prompt user to select backup folder
     */
    private void promptForBackupFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        
        // Try to start in Documents folder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Hint to start in Documents
            Uri documentsUri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Documents");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentsUri);
        }
        
        startActivityForResult(intent, PICK_BACKUP_FOLDER);
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
        
        // Refresh settings UI to show imported values
        uiManager.refreshSettingsValues();
        setupBackupUI(); // Refresh backup status
        
        // Show restart recommendation after delay
        new Handler().postDelayed(() -> {
            showRestartRecommendation();
        }, 1000);
    }
    
    /**
     * Open notification listener settings (required for media controls)
     */
    public void openNotificationListenerSettings() {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Could not open notification settings", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
