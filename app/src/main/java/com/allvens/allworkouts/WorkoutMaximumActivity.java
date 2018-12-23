package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.DebuggingMethods;
import com.allvens.allworkouts.workout_session_manager.WorkoutMaximum_Manager;

public class WorkoutMaximumActivity extends AppCompatActivity {

    private WorkoutMaximum_Manager workoutMax_manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_max);

        TextView tv_max_MaxValue = findViewById(R.id.tv_max_MaxValue);
        TextView tv_max_WorkoutName = findViewById(R.id.tv_max_WorkoutName);

        String chosenWorkout = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        int type = getIntent().getExtras().getInt(Constants.WORKOUT_TYPE_KEY);

        DebuggingMethods.pop("Type: " + type);

        workoutMax_manager = new WorkoutMaximum_Manager(this, tv_max_MaxValue, chosenWorkout, type);
        tv_max_WorkoutName.setText(chosenWorkout);
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
    }

    public void btnAction_max_AddOne(View view) {
        workoutMax_manager.add_OneToMax();
    }

    public void btnAction_max_SubtractOne(View view) {
        workoutMax_manager.subtract_OneFromMax();
    }
}
