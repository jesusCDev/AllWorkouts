package com.allvens.allworkouts.base;

import android.content.Intent;

/**
 * Base interfaces for consistent MVC/MVP architecture
 * Defines common patterns used across all managers and activities
 */
public class BaseInterfaces {
    
    /**
     * Base callback interface for UI operations
     * Common UI interaction patterns used by all UI managers
     */
    public interface BaseUICallback {
        void onShowMessage(String message);
        void onShowError(String error);
        void onNavigationRequested(Intent intent, boolean finishCurrent);
    }
    
    /**
     * Base callback interface for data operations
     * Common data operation patterns used by all data managers
     */
    public interface BaseDataCallback {
        void onDataLoaded();
        void onDataUpdated();
        void onDataError(String error);
    }
    
    /**
     * Base callback interface for business logic operations
     * Common business logic patterns used by all controllers
     */
    public interface BaseControllerCallback {
        void onOperationSuccess(String message);
        void onOperationError(String error);
        void onValidationFailed(String reason);
        void onStateChanged();
    }
    
    /**
     * Base lifecycle interface for components that need cleanup
     */
    public interface BaseLifecycle {
        void initialize();
        void cleanup();
    }
    
    /**
     * Base interface for components that handle view updates
     */
    public interface BaseViewUpdater {
        void refreshView();
        void updateViewState();
    }
    
    /**
     * Base interface for components that validate operations
     */
    public interface BaseValidator {
        boolean isValid();
        String getValidationError();
    }
}