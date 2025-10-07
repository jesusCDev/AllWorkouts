package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.managers.LogBusinessController;
import com.allvens.allworkouts.managers.LogDataManager;
import com.allvens.allworkouts.ui.LogActivityUIManager;

public class LogActivity extends AppCompatActivity
    implements LogActivityUIManager.LogUICallback,
               LogDataManager.LogDataCallback,
               LogBusinessController.LogControllerCallback {

    // Managers
    private LogActivityUIManager uiManager;
    private LogDataManager dataManager;
    private LogBusinessController businessController;
    private String chosenWorkout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // Get workout name from intent
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish(); // Close activity if no required data provided
            return;
        }
        chosenWorkout = extras.getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        
        // Initialize managers
        uiManager = new LogActivityUIManager(this, this);
        dataManager = new LogDataManager(this, this);
        businessController = new LogBusinessController(this, this);
        
        // Setup UI first
        uiManager.initializeViews(chosenWorkout);
        
        // Initialize data manager
        dataManager.initialize(chosenWorkout);
        
        // Initialize business controller after UI is fully set up
        // Check if LogUIManager is available before proceeding
        if (uiManager.getLogUIManager() != null) {
            businessController.initialize(dataManager, uiManager.getLogUIManager());
        } else {
            // Handle error - LogUIManager not available
            uiManager.showErrorMessage("Failed to initialize logs interface");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (businessController != null) {
            businessController.cleanup();
        }
        if (dataManager != null) {
            dataManager.cleanup();
        }
    }

    /****************************************
     /**** BUTTON ACTIONS (Activity Methods)
     ****************************************/

    public void btnAction_ResetWorkoutToZero(View view) {
        if (businessController.hasWorkout()) {
            uiManager.handleResetWorkoutClick();
        } else {
            uiManager.showWorkoutNotStartedError();
        }
    }

    public void btnAction_EditCurrentMaxValue(View view) {
        if (businessController.hasWorkout()) {
            uiManager.handleEditMaxValueClick();
        } else {
            uiManager.showWorkoutNotStartedError();
        }
    }

    public void btnAction_EditType(View view) {
        if (businessController.hasWorkout()) {
            uiManager.handleEditTypeClick();
        } else {
            uiManager.showWorkoutNotStartedError();
        }
    }
    
    /****************************************
     /**** INTERFACE IMPLEMENTATIONS
     ****************************************/
    
    // LogUICallback implementations
    @Override
    public void onResetWorkout() {
        businessController.handleResetWorkout();
    }
    
    @Override
    public void onEditMaxValue() {
        businessController.handleEditMaxValue();
    }
    
    @Override
    public void onEditType(int selectedType) {
        businessController.handleEditType(selectedType);
    }
    
    @Override
    public void onWorkoutOperationResult(String message) {
        // Could be used for additional feedback if needed
    }
    
    // LogDataCallback implementations
    @Override
    public void onWorkoutLoaded(WorkoutInfo workout) {
        businessController.onWorkoutLoaded(workout);
    }
    
    @Override
    public void onWorkoutNotFound() {
        businessController.onWorkoutNotFound();
    }
    
    @Override
    public void onDataLoaded() {
        // Data loaded successfully
    }
    
    @Override
    public void onDataUpdated() {
        businessController.onDataUpdated();
    }
    
    @Override
    public void onDataError(String error) {
        uiManager.showErrorMessage(error);
    }
    
    @Override
    public void onNavigationRequested(Intent intent, boolean finishCurrent) {
        startActivity(intent);
        if (finishCurrent) {
            finish();
        }
    }
    
    // Wrapper for backwards compatibility with LogDataCallback
    public void onNavigationRequested(Intent intent) {
        onNavigationRequested(intent, false);
    }
    
    // Additional BaseUICallback interface methods
    @Override
    public void onShowMessage(String message) {
        uiManager.showInfoMessage(message);
    }
    
    @Override
    public void onShowError(String error) {
        uiManager.showErrorMessage(error);
    }
    
    // LogControllerCallback implementations
    @Override
    public void onUIUpdateRequested() {
        uiManager.refreshUI();
    }
    
    @Override
    public void onErrorOccurred(String error) {
        uiManager.showErrorMessage(error);
    }
    
    @Override
    public void onInfoMessage(String message) {
        uiManager.showInfoMessage(message);
    }
    
    @Override
    public void onWorkoutNotStarted() {
        uiManager.showWorkoutNotStartedError();
    }
}
