package com.allvens.allworkouts.base;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback;
import com.allvens.allworkouts.base.BaseInterfaces.BaseDataCallback;
import com.allvens.allworkouts.base.BaseInterfaces.BaseControllerCallback;

/**
 * Base Activity class providing common functionality for all activities
 * Implements standard patterns for UI callbacks, data callbacks, and controller callbacks
 * Provides lifecycle management and error handling
 */
public abstract class BaseActivity extends AppCompatActivity 
    implements BaseUICallback, BaseDataCallback, BaseControllerCallback {
    
    private static final String TAG = "BaseActivity";
    
    /**
     * Initialize managers - to be implemented by subclasses
     * This method should set up all managers and their dependencies
     */
    protected abstract void initializeManagers();
    
    /**
     * Setup UI components - to be implemented by subclasses
     * This method should initialize all UI-related setup
     */
    protected abstract void setupUI();
    
    /**
     * Cleanup managers - to be implemented by subclasses
     * This method should cleanup all manager resources
     */
    protected abstract void cleanupManagers();
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupManagers();
    }
    
    // BaseUICallback implementations
    @Override
    public void onShowMessage(String message) {
        showToast(message);
    }
    
    @Override
    public void onShowError(String error) {
        showToast("Error: " + error);
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
        // Default implementation - can be overridden by subclasses
    }
    
    @Override
    public void onDataUpdated() {
        // Default implementation - can be overridden by subclasses
    }
    
    @Override
    public void onDataError(String error) {
        onShowError(error);
    }
    
    // BaseControllerCallback implementations
    @Override
    public void onOperationSuccess(String message) {
        onShowMessage(message);
    }
    
    @Override
    public void onOperationError(String error) {
        onShowError(error);
    }
    
    @Override
    public void onValidationFailed(String reason) {
        onShowError("Validation failed: " + reason);
    }
    
    @Override
    public void onStateChanged() {
        // Default implementation - can be overridden by subclasses
    }
    
    /**
     * Show a toast message
     * Can be overridden by subclasses to use custom toast styling
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Helper method to safely start an activity
     */
    protected void safeStartActivity(Intent intent) {
        try {
            startActivity(intent);
        } catch (Exception e) {
            onShowError("Failed to start activity: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to safely finish the activity
     */
    protected void safeFinish() {
        try {
            finish();
        } catch (Exception e) {
            // Log error but don't show to user as activity is finishing
        }
    }
    
    /**
     * Get the simple class name for logging
     */
    protected String getLogTag() {
        return getClass().getSimpleName();
    }
}