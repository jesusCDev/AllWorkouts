package com.allvens.allworkouts.base;

import android.content.Context;

import com.allvens.allworkouts.base.BaseInterfaces.BaseDataCallback;
import com.allvens.allworkouts.base.BaseInterfaces.BaseLifecycle;
import com.allvens.allworkouts.base.BaseInterfaces.BaseValidator;

/**
 * Base Data Manager class providing common data access patterns
 * Handles database operations, error handling, and callback management
 */
public abstract class BaseDataManager implements BaseLifecycle, BaseValidator {
    
    protected Context context;
    protected BaseDataCallback callback;
    protected boolean isInitialized = false;
    protected boolean isValid = true;
    protected String lastError = null;
    
    public BaseDataManager(Context context, BaseDataCallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    @Override
    public void initialize() {
        if (!isInitialized) {
            try {
                initializeDataSources();
                loadInitialData();
                isInitialized = true;
                isValid = true;
                notifyDataLoaded();
            } catch (Exception e) {
                handleError("Failed to initialize data manager", e);
            }
        }
    }
    
    @Override
    public void cleanup() {
        try {
            cleanupDataSources();
        } catch (Exception e) {
            // Log error but don't throw during cleanup
        }
        callback = null;
        isInitialized = false;
    }
    
    /**
     * Initialize data sources (databases, preferences, etc.) - to be implemented by subclasses
     */
    protected abstract void initializeDataSources() throws Exception;
    
    /**
     * Load initial data - to be implemented by subclasses
     */
    protected abstract void loadInitialData() throws Exception;
    
    /**
     * Cleanup data sources - to be implemented by subclasses
     */
    protected abstract void cleanupDataSources() throws Exception;
    
    @Override
    public boolean isValid() {
        return isValid && isInitialized;
    }
    
    @Override
    public String getValidationError() {
        return lastError;
    }
    
    /**
     * Execute a data operation with error handling
     */
    protected <T> T executeDataOperation(DataOperation<T> operation, String operationName) {
        try {
            T result = operation.execute();
            notifyDataUpdated();
            return result;
        } catch (Exception e) {
            handleError("Failed to execute " + operationName, e);
            return null;
        }
    }
    
    /**
     * Execute a data operation without return value
     */
    protected void executeDataOperation(VoidDataOperation operation, String operationName) {
        try {
            operation.execute();
            notifyDataUpdated();
        } catch (Exception e) {
            handleError("Failed to execute " + operationName, e);
        }
    }
    
    /**
     * Handle errors consistently
     */
    protected void handleError(String message, Exception e) {
        lastError = message + ": " + e.getMessage();
        isValid = false;
        
        if (callback != null) {
            callback.onDataError(lastError);
        }
    }
    
    /**
     * Notify callback that data has been loaded
     */
    protected void notifyDataLoaded() {
        if (callback != null) {
            callback.onDataLoaded();
        }
    }
    
    /**
     * Notify callback that data has been updated
     */
    protected void notifyDataUpdated() {
        if (callback != null) {
            callback.onDataUpdated();
        }
    }
    
    /**
     * Notify callback of data errors
     */
    protected void notifyDataError(String error) {
        if (callback != null) {
            callback.onDataError(error);
        }
    }
    
    /**
     * Check if the data manager is properly initialized
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
    protected BaseDataCallback getCallback() {
        return callback;
    }
    
    /**
     * Get the simple class name for logging
     */
    protected String getLogTag() {
        return getClass().getSimpleName();
    }
    
    /**
     * Interface for data operations that return a value
     */
    @FunctionalInterface
    protected interface DataOperation<T> {
        T execute() throws Exception;
    }
    
    /**
     * Interface for data operations that don't return a value
     */
    @FunctionalInterface
    protected interface VoidDataOperation {
        void execute() throws Exception;
    }
}