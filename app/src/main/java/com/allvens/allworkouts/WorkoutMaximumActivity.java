package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.allvens.allworkouts.database.Workout_Info;
import com.allvens.allworkouts.database.Workout_Wrapper;

public class WorkoutMaximumActivity extends AppCompatActivity {

    private TextView tv_max_MaxValue;
    private TextView tv_max_WorkoutName;
    private int maxValue = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_max);

        tv_max_MaxValue = findViewById(R.id.tv_max_MaxValue);
        tv_max_WorkoutName = findViewById(R.id.tv_max_WorkoutName);
        tv_max_WorkoutName.setText(getIntent().getExtras().get("chosenWorkout").toString());


    }


    public void btnAction_max_CompleteMax(View view) {
        update_WorkoutProgress();
        startActivity(new Intent(this, HomeActivity.class));
    }

    public void update_WorkoutProgress(){
        Workout_Wrapper wrapper = new Workout_Wrapper(this);
        wrapper.open();

        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(tv_max_WorkoutName.getText().toString())){
                workout.setProgress(1);
                workout.setMax(maxValue);
                wrapper.update_Workout(workout);
            }
        }
        wrapper.close();
    }

    public void btnAction_max_AddFive(View view) {
        maxValue += 5;
        update_Counter();
    }

    public void btnAction_max_AddOne(View view) {
        maxValue += 1;
        update_Counter();
    }

    public void btnAction_max_SubtractOne(View view) {
        if(maxValue != 1){
            maxValue -= 1;
            update_Counter();
        }
    }

    private void update_Counter(){
        tv_max_MaxValue.setText(Integer.toString(maxValue));
    }
}