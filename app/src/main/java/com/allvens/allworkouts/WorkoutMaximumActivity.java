package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.DebuggingMethods;
import com.allvens.allworkouts.data_manager.SessionUtils;
import com.allvens.allworkouts.workout_session_manager.WorkoutMaximum_Manager;

public class WorkoutMaximumActivity extends AppCompatActivity {

    private WorkoutMaximum_Manager workoutMax_manager;
    private TextView changeIndicator;
    private int originalMaxValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_max);

        TextView tv_max_MaxValue    = findViewById(R.id.tv_max_MaxValue);
        TextView tv_max_WorkoutName = findViewById(R.id.tv_max_WorkoutName);
        changeIndicator             = findViewById(R.id.tv_change_indicator);
        String chosenWorkout        = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        int type                    = getIntent().getExtras().getInt(Constants.WORKOUT_TYPE_KEY);
        
        // Get session start workout from intent or fallback to SharedPreferences
        String sessionStartWorkout = getIntent().getStringExtra(Constants.SESSION_START_WORKOUT_KEY);
        if (sessionStartWorkout == null) {
            sessionStartWorkout = SessionUtils.getSessionStart(this);
        }
        
        workoutMax_manager = new WorkoutMaximum_Manager(this, tv_max_MaxValue, chosenWorkout, type, sessionStartWorkout);
        
        // Store the original max value for change tracking
        originalMaxValue = workoutMax_manager.getCurrentMaxValue();

        tv_max_WorkoutName.setText(chosenWorkout);
        updateChangeIndicator();
    }

    /****************************************
     /**** BUTTON ACTIONS
     ****************************************/

    public void btnAction_max_CompleteMax(View view) {
        workoutMax_manager.update_WorkoutProgress();
        workoutMax_manager.goTo_NewScreen(getIntent().getExtras().getBoolean(Constants.UPDATING_MAX_IN_SETTINGS));
    }

    public void btnAction_max_AddFive(View view) {
        workoutMax_manager.add_FiveToMax();
        updateChangeIndicator();
    }

    public void btnAction_max_AddOne(View view) {
        workoutMax_manager.add_OneToMax();
        updateChangeIndicator();
    }

    public void btnAction_max_SubtractOne(View view) {
        workoutMax_manager.subtract_OneFromMax();
        updateChangeIndicator();
    }
    
    /**
     * Updates the change indicator to show how much the value has changed from the original
     */
    private void updateChangeIndicator() {
        if (changeIndicator == null) return;
        
        int currentValue = workoutMax_manager.getCurrentMaxValue();
        int difference = currentValue - originalMaxValue;
        
        if (difference == 0) {
            // No change, hide indicator
            changeIndicator.setVisibility(View.GONE);
        } else {
            // Show change
            changeIndicator.setVisibility(View.VISIBLE);
            
            String changeText;
            if (difference > 0) {
                changeText = "+" + difference + " from original";
                // Use bright green for positive changes
                changeIndicator.setTextColor(0xFF4CAF50); // Material green
            } else {
                changeText = difference + " from original";
                // Use bright coral red for negative changes
                changeIndicator.setTextColor(0xFFFF6B6B); // Bright coral red
            }
            
            changeIndicator.setText(changeText);
        }
    }
}
