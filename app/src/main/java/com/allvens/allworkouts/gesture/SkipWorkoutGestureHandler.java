package com.allvens.allworkouts.gesture;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

/**
 * Handles long-press gesture on non-button areas to skip to next workout set
 * Provides haptic feedback and triggers countdown overlay before skipping
 */
public class SkipWorkoutGestureHandler {
    
    public interface SkipGestureCallback {
        void onSkipGestureDetected();
    }
    
    private static final String TAG = "SkipGestureHandler";
    private static final int LONG_PRESS_DURATION = 1000; // 1 second for long press
    
    private Context context;
    private SkipGestureCallback callback;
    
    // Long-press tracking
    private boolean isLongPressing = false;
    private boolean isGestureActive = false; // Tracks if gesture detection is currently active
    private Handler longPressHandler;
    private Runnable longPressRunnable;
    private View currentView;
    
    public SkipWorkoutGestureHandler(Context context, SkipGestureCallback callback) {
        this.context = context;
        this.callback = callback;
        this.longPressHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Called when skip gesture is detected
     */
    private void onSkipGestureDetected() {
        // Notify callback
        if (callback != null) {
            callback.onSkipGestureDetected();
        }
    }
    
    /**
     * Attach gesture detection to a view
     */
    public void attachToView(View view) {
        this.currentView = view;
        view.setOnTouchListener((v, event) -> {
            // Handle long press detection
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // If a gesture just completed, reset state
                    if (isGestureActive) {
                        android.util.Log.d(TAG, "Previous gesture still active - resetting");
                        resetGestureState();
                    }
                    
                    // Check if touch is on a clickable child (button)
                    if (isTouchOnClickableView(v, event)) {
                        android.util.Log.d(TAG, "Touch on button - ignoring");
                        return false; // Let the button handle it
                    }
                    
                    // Start long press detection for non-button areas
                    android.util.Log.d(TAG, "Long-press detection started");
                    startLongPressDetection(v);
                    return true; // Consume the event
                    
                case MotionEvent.ACTION_MOVE:
                    // Check if moved too far - cancel gesture
                    // (This prevents accidental triggers while scrolling)
                    break;
                    
                case MotionEvent.ACTION_UP:
                    android.util.Log.d(TAG, "Touch released - cancelling long-press");
                    cancelLongPressDetection();
                    return true;
                    
                case MotionEvent.ACTION_CANCEL:
                    android.util.Log.d(TAG, "Touch cancelled");
                    cancelLongPressDetection();
                    return true;
            }
            
            return false;
        });
    }
    
    /**
     * Reset gesture state - call this after skip is complete or cancelled
     */
    public void resetGestureState() {
        isLongPressing = false;
        isGestureActive = false;
        if (longPressRunnable != null && longPressHandler != null) {
            longPressHandler.removeCallbacks(longPressRunnable);
        }
        android.util.Log.d(TAG, "Gesture state reset");
    }
    
    /**
     * Check if touch is on a clickable view (button, etc.)
     */
    private boolean isTouchOnClickableView(View parent, MotionEvent event) {
        if (!(parent instanceof android.view.ViewGroup)) {
            return false;
        }
        
        android.view.ViewGroup viewGroup = (android.view.ViewGroup) parent;
        float x = event.getRawX();
        float y = event.getRawY();
        
        // Recursively check all child views
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            
            // Get child's position on screen
            int[] location = new int[2];
            child.getLocationOnScreen(location);
            
            // Check if touch is within child bounds
            if (x >= location[0] && x <= location[0] + child.getWidth() &&
                y >= location[1] && y <= location[1] + child.getHeight()) {
                
                // Check if child is clickable (button, etc.)
                if (child.isClickable() || child instanceof android.widget.Button) {
                    android.util.Log.d(TAG, "Touch on clickable view: " + child.getClass().getSimpleName());
                    return true;
                }
                
                // Recursively check if child has clickable children
                if (child instanceof android.view.ViewGroup) {
                    if (isTouchOnClickableView(child, event)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Start long press detection
     */
    private void startLongPressDetection(View view) {
        isLongPressing = false;
        isGestureActive = true;
        
        longPressRunnable = () -> {
            // Long press threshold reached
            isLongPressing = true;
            android.util.Log.d(TAG, "Long-press detected!");
            
            // Provide haptic feedback
            if (view != null) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
            
            // Trigger skip gesture
            onSkipGestureDetected();
        };
        
        longPressHandler.postDelayed(longPressRunnable, LONG_PRESS_DURATION);
    }
    
    /**
     * Cancel long press detection
     */
    private void cancelLongPressDetection() {
        if (longPressRunnable != null && longPressHandler != null) {
            longPressHandler.removeCallbacks(longPressRunnable);
        }
        isLongPressing = false;
        isGestureActive = false;
    }
    
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (longPressHandler != null && longPressRunnable != null) {
            longPressHandler.removeCallbacks(longPressRunnable);
        }
        
        longPressHandler = null;
        longPressRunnable = null;
        callback = null;
        currentView = null;
    }
}
