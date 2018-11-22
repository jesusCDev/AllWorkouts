package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.allvens.allworkouts.database.Workout_Info;
import com.allvens.allworkouts.database.Workout_Wrapper;

public class WorkoutFinishActivity extends AppCompatActivity{


    private Workout_Wrapper wrapper;
    private int workoutDifficulty = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workout_finish);

        wrapper = new Workout_Wrapper(this);
        wrapper.open();
        update_WorkoutProgress(getIntent().getExtras().get("choiceWorkout").toString(), 2);
    }

    public void update_WorkoutProgress(String choiceWorkout, int progress){
        wrapper.open();
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(choiceWorkout)){
                workout.setMax((workout.getMax() + progress));
                workout.setProgress((workout.getProgress() + 1));
                wrapper.update_Workout(workout);
            }
        }
        wrapper.close();
    }

    public void btnAction_workoutFinish_setDifficulty(View view) {
        switch (((Button)view).getText().toString().toLowerCase()){
            case "hard":
                set_WorkoutDifficulty(1);
                break;
                // todo show that this button is the pressed one
            case "neutral":
                set_WorkoutDifficulty(2);
                break;
            case "easy":
                set_WorkoutDifficulty(3);
                break;
        }
    }

    private void set_WorkoutDifficulty(int difficulty){
        workoutDifficulty = difficulty;
    }

    private void update_Progress(){
        if(workoutDifficulty == 1){
            update_WorkoutProgress(getIntent().getExtras().get("choiceWorkout").toString(), -4);
        }else if(workoutDifficulty == 3){
            update_WorkoutProgress(getIntent().getExtras().get("choiceWorkout").toString(), 2);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        wrapper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wrapper.open();
    }

    public void btnAction_workoutFinish_NextWorkout(View view) {
        update_Progress();
    }

    public void btnAction_workoutFinish_FinishWorkout(View view) {
        update_Progress();
    }
}
