package com.allvens.allworkouts.base;

import android.content.Context;

import com.allvens.allworkouts.base.BaseInterfaces.BaseControllerCallback;
import com.allvens.allworkouts.base.BaseInterfaces.BaseLifecycle;
import com.allvens.allworkouts.base.BaseInterfaces.BaseValidator;

/**
 * Base Controller class providing common business logic coordination patterns
 * Handles coordination between UI and data layers, validation, and state management
 */
public abstract class BaseController implements BaseLifecycle, BaseValidator {
    
    protected Context context;
    protected BaseControllerCallback callback;
    protected boolean isInitialized = false;
    protected boolean isValid = true;
    protected String lastValidationError = null;
    
    public BaseController(Context context, BaseControllerCallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    @Override
    public void initialize() {
        if (!isInitialized) {
            try {
                initializeComponents();
                validateInitialState();
                isInitialized = true;
                notifyStateChanged();
            } catch (Exception e) {
                handleError("Failed to initialize controller", e);
            }
        }
    }
    
    @Override
    public void cleanup() {
        try {
            cleanupComponents();
        } catch (Exception e) {
            // Log error but don't throw during cleanup
        }
        callback = null;
        isInitialized = false;
    }
    
    /**
     * Initialize controller components - to be implemented by subclasses
     */
    protected abstract void initializeComponents() throws Exception;
    
    /**
     * Validate initial state - to be implemented by subclasses
     */
    protected abstract void validateInitialState() throws Exception;
    
    /**
     * Cleanup controller resources - to be implemented by subclasses
     */
    protected abstract void cleanupComponents() throws Exception;
    
    @Override
    public boolean isValid() {
        return isValid && isInitialized;
    }
    
    @Override
    public String getValidationError() {
        return lastValidationError;
    }
    
    /**
     * Execute a business operation with validation and error handling
     */
    protected <T> T executeOperation(BusinessOperation<T> operation, String operationName) {
        if (!validateOperation(operationName)) {
            return null;
        }
        
        try {
            T result = operation.execute();
            notifyOperationSuccess(operationName + " completed successfully");
            return result;
        } catch (Exception e) {
            handleError("Failed to execute " + operationName, e);
            return null;
        }
    }
    
    /**
     * Execute a business operation without return value
     */
    protected void executeOperation(VoidBusinessOperation operation, String operationName) {
        if (!validateOperation(operationName)) {
            return;
        }
        
        try {
            operation.execute();
            notifyOperationSuccess(operationName + " completed successfully");
        } catch (Exception e) {
            handleError("Failed to execute " + operationName, e);
        }
    }
    
    /**
     * Validate that an operation can be performed
     */
    protected boolean validateOperation(String operationName) {
        if (!isValid()) {
            String error = "Cannot execute " + operationName + ": " + getValidationError();
            notifyValidationFailed(error);
            return false;
        }
        return true;
    }
    
    /**
     * Handle errors consistently
     */
    protected void handleError(String message, Exception e) {
        String errorMessage = message + ": " + e.getMessage();
        isValid = false;
        lastValidationError = errorMessage;
        
        if (callback != null) {
            callback.onOperationError(errorMessage);
        }
    }
    
    /**
     * Notify callback of successful operations
     */
    protected void notifyOperationSuccess(String message) {
        if (callback != null) {
            callback.onOperationSuccess(message);
        }
    }
    
    /**
     * Notify callback of operation errors
     */
    protected void notifyOperationError(String error) {
        if (callback != null) {
            callback.onOperationError(error);
        }
    }
    
    /**
     * Notify callback of validation failures
     */
    protected void notifyValidationFailed(String reason) {
        if (callback != null) {
            callback.onValidationFailed(reason);
        }
    }
    
    /**
     * Notify callback of state changes
     */
    protected void notifyStateChanged() {
        if (callback != null) {
            callback.onStateChanged();
        }
    }
    
    /**
     * Check if the controller is properly initialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }
    
    /**
     * Get the context
     */
    protected Context getContext() {
        return context;
    }
    
    /**
     * Get the callback
     */
    protected BaseControllerCallback getCallback() {
        return callback;
    }
    
    /**
     * Get the simple class name for logging
     */
    protected String getLogTag() {
        return getClass().getSimpleName();
    }
    
    /**
     * Interface for business operations that return a value
     */
    @FunctionalInterface
    protected interface BusinessOperation<T> {
        T execute() throws Exception;
    }
    
    /**
     * Interface for business operations that don't return a value
     */
    @FunctionalInterface
    protected interface VoidBusinessOperation {
        void execute() throws Exception;
    }
}