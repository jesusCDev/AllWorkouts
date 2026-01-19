package com.allvens.allworkouts.ui;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.gesture.SkipWorkoutGestureHandler;
import com.allvens.allworkouts.media.WorkoutMediaController;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;
import com.iambedant.text.OutlineTextView;

/**
 * UI Manager for WorkoutSessionActivity
 * Handles all UI coordination, view binding, and media controls setup
 * This is separate from WorkoutSessionUIManager which handles workout-specific UI updates
 */
public class WorkoutSessionActivityUIManager {
    
    public interface WorkoutSessionUICallback {
        void onWorkoutSessionComplete();
        void onScreenChange();
        void onSessionExit();
    }
    
    private Context context;
    private WorkoutSessionUICallback callback;
    private WorkoutMediaController mediaController;
    private View mediaControlsLayout;
    private View skipOverlayLayout;
    private TextView tvSkipCountdown;
    private android.os.Handler countdownHandler;

    // Cooldown for button presses
    private static final int BUTTON_COOLDOWN_MS = 5000;
    private android.os.Handler cooldownHandler;

    // Reference to gesture handler for cooldown control
    private SkipWorkoutGestureHandler gestureHandler;
    
    // Main UI Elements
    private TextView tvWorkoutName;
    private ConstraintLayout cTimerRepsWorkoutHolder;
    private ImageView ivWorkoutImageHolder;
    private TextView tvTimerHolder;
    private OutlineTextView tvFront;
    private TextView tvBack;
    private TextView[] workoutValues = new TextView[5];
    private Button btnChangeScreens;
    
    public WorkoutSessionActivityUIManager(Context context, WorkoutSessionUICallback callback) {
        this.context = context;
        this.callback = callback;
        this.countdownHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        this.cooldownHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    }
    
    /**
     * Initialize all views and bind them to the UI manager
     */
    public void initializeViews() {
        Activity activity = (Activity) context;
        
        // Main UI elements
        tvWorkoutName = activity.findViewById(R.id.tv_workout_WorkoutName);
        // cTimerRepsWorkoutHolder no longer exists in simplified layout
        cTimerRepsWorkoutHolder = null;
        ivWorkoutImageHolder = activity.findViewById(R.id.iv_workout_workoutImage);
        tvTimerHolder = activity.findViewById(R.id.tv_workout_timer);
        tvFront = activity.findViewById(R.id.otv_workout_repNumber_front);
        tvBack = activity.findViewById(R.id.tv_workout_repNumber_back);
        
        // Workout value displays
        workoutValues[0] = activity.findViewById(R.id.tv_workout_Value1);
        workoutValues[1] = activity.findViewById(R.id.tv_workout_Value2);
        workoutValues[2] = activity.findViewById(R.id.tv_workout_Value3);
        workoutValues[3] = activity.findViewById(R.id.tv_workout_Value4);
        workoutValues[4] = activity.findViewById(R.id.tv_workout_Value5);
        
        // Control button
        btnChangeScreens = activity.findViewById(R.id.btn_workout_CompleteTask);

        // Skip overlay elements
        skipOverlayLayout = activity.findViewById(R.id.skip_workout_overlay);
        if (skipOverlayLayout != null) {
            tvSkipCountdown = skipOverlayLayout.findViewById(R.id.tv_skip_countdown);
        }

        // Reposition complete button if setting is enabled
        positionCompleteButton();
    }

    /**
     * Reposition the complete button to the top of the bottom section if setting is enabled.
     * Moves it above the value pills (progress indicators).
     */
    private void positionCompleteButton() {
        SettingsPrefsManager prefs = new SettingsPrefsManager(context);
        boolean positionTop = prefs.getPrefSetting(PreferencesValues.COMPLETE_BUTTON_TOP, false);

        if (!positionTop) return; // Keep default bottom position

        Activity activity = (Activity) context;
        LinearLayout bottomBlock = activity.findViewById(R.id.bottomBlock);
        View valueHolder = activity.findViewById(R.id.ll_workout_ValueHolder);

        if (bottomBlock == null || btnChangeScreens == null || valueHolder == null) return;

        // Remove the button from its current position
        bottomBlock.removeView(btnChangeScreens);

        // Find the index of the value holder (progress pills)
        int valueHolderIndex = bottomBlock.indexOfChild(valueHolder);

        // Insert the button before the value holder (after media controls if present)
        bottomBlock.addView(btnChangeScreens, valueHolderIndex);

        // Set proper margins for the repositioned button
        int spacingBottom = (int) context.getResources().getDimension(R.dimen.spacing_3);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnChangeScreens.getLayoutParams();
        if (params != null) {
            params.setMargins(0, 0, 0, spacingBottom);
            btnChangeScreens.setLayoutParams(params);
        }
    }
    
    /**
     * Setup media controls based on user preferences
     */
    public void setupMediaControls() {
        // Check if media controls are enabled in settings
        SettingsPrefsManager prefsManager = new SettingsPrefsManager(context);
        boolean mediaControlsEnabled = prefsManager.getPrefSetting(PreferencesValues.MEDIA_CONTROLS_ON);

        mediaControlsLayout = ((Activity) context).findViewById(R.id.media_controls);

        if (mediaControlsEnabled && mediaControlsLayout != null) {
            // Show media controls
            mediaControlsLayout.setVisibility(View.VISIBLE);

            // Find media control buttons and track title
            ImageButton btnPrevious = mediaControlsLayout.findViewById(R.id.btn_media_previous);
            ImageButton btnPlayPause = mediaControlsLayout.findViewById(R.id.btn_media_play_pause);
            ImageButton btnNext = mediaControlsLayout.findViewById(R.id.btn_media_next);
            TextView tvTrackTitle = mediaControlsLayout.findViewById(R.id.tv_track_title);

            // Apply media background setting
            // Note: mediaControlsLayout IS the container (include ID overrides root element ID)
            boolean showMediaBackground = prefsManager.getPrefSetting(PreferencesValues.SHOW_MEDIA_BACKGROUND, true);
            if (showMediaBackground) {
                mediaControlsLayout.setBackgroundResource(R.drawable.bg_chip_glossy);
            } else {
                // No background - remove it completely and enhance button/text visibility
                mediaControlsLayout.setBackground(null);
                mediaControlsLayout.setElevation(0);

                // Enhance track title visibility with brighter color and shadow
                if (tvTrackTitle != null) {
                    tvTrackTitle.setTextColor(android.support.v4.content.ContextCompat.getColor(context, R.color.bone_100));
                    tvTrackTitle.setShadowLayer(8f, 0f, 2f, 0x99000000);
                }

                // Enhance button visibility with brighter tint
                int brightTint = android.support.v4.content.ContextCompat.getColor(context, R.color.bone_100);
                if (btnPrevious != null) {
                    btnPrevious.setColorFilter(brightTint);
                }
                if (btnNext != null) {
                    btnNext.setColorFilter(brightTint);
                }
            }

            // Initialize media controller
            mediaController = new WorkoutMediaController(context);

            // Set up media control functionality
            if (btnPrevious != null && btnPlayPause != null && btnNext != null && tvTrackTitle != null) {
                mediaController.setupMediaControls(btnPrevious, btnPlayPause, btnNext, tvTrackTitle);
            }
        } else {
            // Hide media controls
            if (mediaControlsLayout != null) {
                mediaControlsLayout.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * Get UI elements for the workout session manager
     */
    public UIElements getUIElements() {
        return new UIElements(
            tvWorkoutName,
            cTimerRepsWorkoutHolder, 
            ivWorkoutImageHolder,
            tvTimerHolder,
            tvFront,
            tvBack,
            workoutValues[0], workoutValues[1], workoutValues[2], workoutValues[3], workoutValues[4],
            btnChangeScreens
        );
    }
    
    /**
     * Set the gesture handler reference for cooldown control
     */
    public void setGestureHandler(SkipWorkoutGestureHandler gestureHandler) {
        this.gestureHandler = gestureHandler;
    }

    /**
     * Handle activity resume - refresh media controls
     */
    public void onResume() {
        // Refresh media control state when resuming (with delay to ensure system is ready)
        if (mediaController != null) {
            // Use a handler to delay the refresh slightly
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                mediaController.refreshPlaybackState();
                mediaController.updateTrackInfo();
            }, 200);
        }
    }
    
    /**
     * Handle activity pause
     */
    public void onPause() {
        // Media controller doesn't need pause handling currently
    }
    
    /**
     * Handle activity destroy - cleanup resources
     */
    public void onDestroy() {
        // Clean up media controller resources
        if (mediaController != null) {
            mediaController.cleanup();
            mediaController = null;
        }

        // Clean up countdown handler
        if (countdownHandler != null) {
            countdownHandler.removeCallbacksAndMessages(null);
        }

        // Clean up cooldown handler
        if (cooldownHandler != null) {
            cooldownHandler.removeCallbacksAndMessages(null);
        }
    }
    
    /**
     * Handle screen change button click
     */
    public void handleScreenChangeClick() {
        if (callback != null) {
            callback.onScreenChange();
            // Apply cooldown to prevent accidental double-clicks
            applyButtonCooldown();
        }
    }

    /**
     * Apply cooldown to the Complete/Next button to prevent accidental clicks
     * Also disables the skip gesture during cooldown
     */
    private void applyButtonCooldown() {
        if (btnChangeScreens == null) return;

        // Disable button and reduce alpha
        btnChangeScreens.setEnabled(false);
        btnChangeScreens.setAlpha(0.5f);

        // Disable gesture handler during cooldown
        if (gestureHandler != null) {
            gestureHandler.setEnabled(false);
        }

        // Re-enable after cooldown
        cooldownHandler.postDelayed(() -> {
            if (btnChangeScreens != null) {
                btnChangeScreens.setEnabled(true);
                btnChangeScreens.setAlpha(1.0f);
            }
            // Re-enable gesture handler
            if (gestureHandler != null) {
                gestureHandler.setEnabled(true);
            }
        }, BUTTON_COOLDOWN_MS);
    }
    
    /**
     * Handle workout session completion
     */
    public void handleSessionComplete() {
        if (callback != null) {
            callback.onWorkoutSessionComplete();
        }
    }
    
    /**
     * Handle session exit (back button or menu)
     */
    public void handleSessionExit() {
        if (callback != null) {
            callback.onSessionExit();
        }
    }
    
    /**
     * Show skip countdown overlay and animate countdown
     * @param onComplete Callback when countdown completes
     * @param onCancel Callback when countdown is cancelled
     */
    public void showSkipCountdown(Runnable onComplete, Runnable onCancel) {
        if (skipOverlayLayout == null) {
            android.util.Log.w("WorkoutSessionUI", "Skip overlay not found");
            return;
        }
        
        // Show overlay
        skipOverlayLayout.setVisibility(View.VISIBLE);
        
        // Set up cancel on click
        skipOverlayLayout.setOnClickListener(v -> {
            hideSkipCountdown();
            if (onCancel != null) {
                onCancel.run();
            }
        });
        
        // Start countdown animation
        animateCountdown(3, onComplete);
    }
    
    /**
     * Animate countdown from given number to 0
     */
    private void animateCountdown(int count, Runnable onComplete) {
        if (tvSkipCountdown == null || skipOverlayLayout == null) {
            return;
        }
        
        if (count <= 0) {
            // Countdown complete
            hideSkipCountdown();
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }
        
        // Update countdown display
        tvSkipCountdown.setText(String.valueOf(count));
        
        // Animate scale
        tvSkipCountdown.setScaleX(1.3f);
        tvSkipCountdown.setScaleY(1.3f);
        tvSkipCountdown.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(300)
            .start();
        
        // Schedule next countdown
        countdownHandler.postDelayed(() -> animateCountdown(count - 1, onComplete), 1000);
    }
    
    /**
     * Hide skip countdown overlay
     */
    public void hideSkipCountdown() {
        if (skipOverlayLayout != null) {
            skipOverlayLayout.setVisibility(View.GONE);
            skipOverlayLayout.setOnClickListener(null);
        }
        
        // Cancel any pending countdown
        if (countdownHandler != null) {
            countdownHandler.removeCallbacksAndMessages(null);
        }
    }
    
    /**
     * Data class to hold UI elements for passing to other managers
     */
    public static class UIElements {
        public final TextView tvWorkoutName;
        public final ConstraintLayout cTimerRepsWorkoutHolder;
        public final ImageView ivWorkoutImageHolder;
        public final TextView tvTimerHolder;
        public final OutlineTextView tvFront;
        public final TextView tvBack;
        public final TextView tvValue1, tvValue2, tvValue3, tvValue4, tvValue5;
        public final Button btnChangeScreens;
        
        public UIElements(TextView tvWorkoutName, ConstraintLayout cTimerRepsWorkoutHolder,
                         ImageView ivWorkoutImageHolder, TextView tvTimerHolder,
                         OutlineTextView tvFront, TextView tvBack,
                         TextView tvValue1, TextView tvValue2, TextView tvValue3,
                         TextView tvValue4, TextView tvValue5, Button btnChangeScreens) {
            this.tvWorkoutName = tvWorkoutName;
            this.cTimerRepsWorkoutHolder = cTimerRepsWorkoutHolder;
            this.ivWorkoutImageHolder = ivWorkoutImageHolder;
            this.tvTimerHolder = tvTimerHolder;
            this.tvFront = tvFront;
            this.tvBack = tvBack;
            this.tvValue1 = tvValue1;
            this.tvValue2 = tvValue2;
            this.tvValue3 = tvValue3;
            this.tvValue4 = tvValue4;
            this.tvValue5 = tvValue5;
            this.btnChangeScreens = btnChangeScreens;
        }
    }
}