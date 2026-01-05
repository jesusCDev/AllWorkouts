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

    private static final String TAG = "SkipGesture";
    private static final int LONG_PRESS_DURATION = 1000; // 1 second for long press

    private Context context;
    private SkipGestureCallback callback;

    // Long-press tracking
    private boolean isLongPressing = false;
    private boolean isGestureActive = false;
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
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // If a gesture just completed, reset state
                    if (isGestureActive) {
                        resetGestureState();
                    }

                    // Check if touch is on a clickable child (button)
                    if (isTouchOnClickableView(v, event)) {
                        return false; // Let the button handle it
                    }

                    // Start long press detection for non-button areas
                    startLongPressDetection(v);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
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
    }
    
    /**
     * Check if touch is on an actual button (not just any clickable view)
     * We only want to ignore touches on actual UI buttons, not overlays or containers
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

                // Only ignore touches on ACTUAL buttons, not any clickable view
                // This prevents FrameLayouts and other containers from blocking gestures
                boolean isActualButton = child instanceof android.widget.Button ||
                                        child instanceof android.widget.ImageButton ||
                                        child instanceof android.widget.ToggleButton;

                if (isActualButton) {
                    return true;
                }

                // Recursively check if child has actual button children
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
            isLongPressing = true;

            // Provide haptic feedback
            if (view != null) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }

            // Reset gesture state BEFORE calling callback to prevent
            // getting stuck if overlay intercepts touch events
            isGestureActive = false;
            isLongPressing = false;

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
