package com.allvens.allworkouts.ui;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.PreferencesValues;
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
    }
    
    /**
     * Initialize all views and bind them to the UI manager
     */
    public void initializeViews() {
        Activity activity = (Activity) context;
        
        // Main UI elements
        tvWorkoutName = activity.findViewById(R.id.tv_workout_WorkoutName);
        cTimerRepsWorkoutHolder = activity.findViewById(R.id.c_workoutSession_TimerRepsWorkoutHolder);
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
            
            // Initialize media controller
            mediaController = new WorkoutMediaController(context);
            
            // Find media control buttons and track info
            ImageButton btnPrevious = mediaControlsLayout.findViewById(R.id.btn_media_previous);
            ImageButton btnPlayPause = mediaControlsLayout.findViewById(R.id.btn_media_play_pause);
            ImageButton btnNext = mediaControlsLayout.findViewById(R.id.btn_media_next);
            TextView tvTrackInfo = mediaControlsLayout.findViewById(R.id.tv_track_info);
            
            // Set up media control functionality
            if (btnPrevious != null && btnPlayPause != null && btnNext != null && tvTrackInfo != null) {
                mediaController.setupMediaControls(btnPrevious, btnPlayPause, btnNext, tvTrackInfo);
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
    }
    
    /**
     * Handle screen change button click
     */
    public void handleScreenChangeClick() {
        if (callback != null) {
            callback.onScreenChange();
        }
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