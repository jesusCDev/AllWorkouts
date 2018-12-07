package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.workout_session_manager.WorkoutMaxScene_Manager;

public class WorkoutMaximumActivity extends AppCompatActivity {

    private WorkoutMaxScene_Manager workoutMaxScene_manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_max);

        TextView tv_max_MaxValue = findViewById(R.id.tv_max_MaxValue);
        TextView tv_max_WorkoutName = findViewById(R.id.tv_max_WorkoutName);

        String chosenWorkout = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        int type = getIntent().getExtras().getInt(Constants.WORKOUT_TYPE_KEY);
        tv_max_WorkoutName.setText(chosenWorkout);

        workoutMaxScene_manager = new WorkoutMaxScene_Manager(this, chosenWorkout, type);
        workoutMaxScene_manager.set_tvCounterView(tv_max_MaxValue);
    }


    public void btnAction_max_CompleteMax(View view) {
        workoutMaxScene_manager.update_WorkoutProgress();
        startActivity(new Intent(this, MainActivity.class));
    }



    public void btnAction_max_AddFive(View view) {
        workoutMaxScene_manager.add_FiveToMax();
    }

    public void btnAction_max_AddOne(View view) {
        workoutMaxScene_manager.add_OneToMax();
    }

    public void btnAction_max_SubtractOne(View view) {
        workoutMaxScene_manager.subtract_OneFromMax();
    }
}
