package com.allvens.allworkouts.base;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.base.BaseInterfaces.BaseLifecycle;
import com.allvens.allworkouts.base.BaseInterfaces.BaseUICallback;
import com.allvens.allworkouts.base.BaseInterfaces.BaseViewUpdater;

/**
 * Base UI Manager class providing common UI management patterns
 * Handles common UI operations like view binding, message display, and lifecycle
 */
public abstract class BaseUIManager implements BaseLifecycle, BaseViewUpdater {
    
    protected Context context;
    protected BaseUICallback callback;
    protected boolean isInitialized = false;
    
    public BaseUIManager(Context context, BaseUICallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    @Override
    public void initialize() {
        if (!isInitialized) {
            initializeViews();
            setupViews();
            setupListeners();
            isInitialized = true;
        }
    }
    
    @Override
    public void cleanup() {
        cleanupViews();
        callback = null;
        isInitialized = false;
    }
    
    /**
     * Initialize all views - to be implemented by subclasses
     */
    protected abstract void initializeViews();
    
    /**
     * Setup view states and properties - to be implemented by subclasses
     */
    protected abstract void setupViews();
    
    /**
     * Setup view listeners - to be implemented by subclasses
     */
    protected abstract void setupListeners();
    
    /**
     * Cleanup view resources - to be implemented by subclasses
     */
    protected abstract void cleanupViews();
    
    @Override
    public void refreshView() {
        if (isInitialized) {
            updateViewState();
        }
    }
    
    @Override
    public void updateViewState() {
        // Default implementation - can be overridden by subclasses
    }
    
    /**
     * Show success message with proper theming
     */
    public void showSuccessMessage(String message) {
        showDarkToast(message);
    }
    
    /**
     * Show error message with proper theming
     */
    public void showErrorMessage(String message) {
        showDarkToast("Error: " + message);
    }
    
    /**
     * Show info message with proper theming
     */
    public void showInfoMessage(String message) {
        showDarkToast(message);
    }
    
    /**
     * Show toast message with proper dark theme styling
     * This is the consistent way to show messages across the app
     */
    protected void showDarkToast(String message) {
        // Try to use Snackbar for better theming support
        if (context instanceof Activity) {
            View rootView = ((Activity) context).findViewById(android.R.id.content);
            if (rootView != null) {
                Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
                // Style the snackbar with dark colors
                snackbar.setActionTextColor(context.getResources().getColor(R.color.colorAccent));
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(context.getResources().getColor(R.color.background_elevated));
                snackbar.show();
                return;
            }
        }
        // Fallback to regular toast if snackbar fails
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Check if the UI manager is properly initialized
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
    protected BaseUICallback getCallback() {
        return callback;
    }
    
    /**
     * Helper method to safely notify callback of errors
     */
    protected void notifyError(String error) {
        if (callback != null) {
            callback.onShowError(error);
        }
    }
    
    /**
     * Helper method to safely notify callback of messages
     */
    protected void notifyMessage(String message) {
        if (callback != null) {
            callback.onShowMessage(message);
        }
    }
    
    /**
     * Get the simple class name for logging
     */
    protected String getLogTag() {
        return getClass().getSimpleName();
    }
}