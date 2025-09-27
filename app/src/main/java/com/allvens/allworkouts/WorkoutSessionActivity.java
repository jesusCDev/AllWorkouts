package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.data_manager.SessionUtils;
import com.allvens.allworkouts.media.WorkoutMediaController;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;
import com.allvens.allworkouts.workout_session_manager.WorkoutSession_Manager;
import com.iambedant.text.OutlineTextView;

public class WorkoutSessionActivity extends AppCompatActivity {

    private WorkoutSession_Manager manager;
    private WorkoutMediaController mediaController;
    private View mediaControlsLayout;
    private String sessionStartWorkout; // The workout that started this session

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);

        TextView tv_workout_WorkoutName          = findViewById(R.id.tv_workout_WorkoutName);
        ConstraintLayout cTimerRepsWorkoutHolder = findViewById(R.id.c_workoutSession_TimerRepsWorkoutHolder);
        ImageView ivWorkoutImageHolder           = findViewById(R.id.iv_workout_workoutImage);
        TextView tvTimerHolder                   = findViewById(R.id.tv_workout_timer);
        OutlineTextView tvFront                  = findViewById(R.id.otv_workout_repNumber_front);
        TextView tvBack                          = findViewById(R.id.tv_workout_repNumber_back);
        TextView tvValue1                        = findViewById(R.id.tv_workout_Value1);
        TextView tvValue2                        = findViewById(R.id.tv_workout_Value2);
        TextView tvValue3                        = findViewById(R.id.tv_workout_Value3);
        TextView tvValue4                        = findViewById(R.id.tv_workout_Value4);
        TextView tvValue5                        = findViewById(R.id.tv_workout_Value5);
        Button btn_ChangeScreens                 = findViewById(R.id.btn_workout_CompleteTask);
        manager                                  = new WorkoutSession_Manager(this, getIntent().getExtras().get(Constants.CHOSEN_WORKOUT_EXTRA_KEY).toString());
        
        // Get session start workout from intent or fallback to SharedPreferences
        sessionStartWorkout = getIntent().getStringExtra(Constants.SESSION_START_WORKOUT_KEY);
        if (sessionStartWorkout == null) {
            sessionStartWorkout = SessionUtils.getSessionStart(this);
        }
        
        // Save to SharedPreferences as backup
        if (sessionStartWorkout != null) {
            SessionUtils.saveSessionStart(this, sessionStartWorkout);
        }

        manager.setUp_UiManager(tv_workout_WorkoutName, cTimerRepsWorkoutHolder, ivWorkoutImageHolder, tvTimerHolder,
                tvFront, tvBack, tvValue1, tvValue2, tvValue3, tvValue4, tvValue5,
                btn_ChangeScreens);

        manager.set_Timer();
        manager.start_Screen();
        
        // Set up media controls if enabled
        setupMediaControls();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.kill_Timer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh media control state when resuming (with delay to ensure system is ready)
        if (mediaController != null) {
            // Use a handler to delay the refresh slightly
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                mediaController.refreshPlaybackState();
                mediaController.updateTrackInfo();
            }, 200);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up media controller resources
        if (mediaController != null) {
            mediaController.cleanup();
        }
    }

    /****************************************
     /**** BUTTON ACTIONS
     ****************************************/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        manager.kill_Timer();
        startActivity(new Intent(this, MainActivity.class));

        return true;
    }

    /**
     * Switches Screen between - Workout and Timer
     * @param view
     */
    public void btnAction_ChangeActivities(View view){
        if(manager.check_IfFinished()){
            manager.update_Screen();
        }
        else{
            Intent intent = new Intent(this, WorkoutSessionFinishActivity.class);

            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, manager.get_Workout());
            
            // Thread the session start workout to WorkoutSessionFinishActivity
            if (sessionStartWorkout != null) {
                intent.putExtra(Constants.SESSION_START_WORKOUT_KEY, sessionStartWorkout);
            }
            
            this.startActivity(intent);
            finish();
        }

    }
    
    /**
     * Set up media controls based on user preference
     */
    private void setupMediaControls() {
        // Check if media controls are enabled in settings
        SettingsPrefsManager prefsManager = new SettingsPrefsManager(this);
        boolean mediaControlsEnabled = prefsManager.getPrefSetting(Preferences_Values.MEDIA_CONTROLS_ON);
        
        mediaControlsLayout = findViewById(R.id.media_controls);
        
        if (mediaControlsEnabled && mediaControlsLayout != null) {
            // Show media controls
            mediaControlsLayout.setVisibility(View.VISIBLE);
            
            // Initialize media controller
            mediaController = new WorkoutMediaController(this);
            
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
}
