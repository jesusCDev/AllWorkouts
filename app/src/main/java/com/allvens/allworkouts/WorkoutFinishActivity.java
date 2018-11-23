package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.database.Workout_Info;
import com.allvens.allworkouts.database.Workout_Wrapper;

public class WorkoutFinishActivity extends AppCompatActivity{


    private int maxValue;
    private String choiceWorkout;

    private final static int PROG_INC_NEUTRAL = 1;
    private final static int PROG_INC_EASY = 2;
    private final static int PROG_INC_HARD = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finish);

        choiceWorkout = getIntent().getExtras().get(Constants.CHOOSEN_WORKOUT_EXTRA_KEY).toString();
        update_WorkoutProgress();
    }

    public void update_WorkoutProgress(){
        Workout_Wrapper wrapper = new Workout_Wrapper(this);
        wrapper.open();

        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(choiceWorkout)){
                maxValue = workout.getMax();
                workout.setMax((workout.getMax() + PROG_INC_NEUTRAL));
                workout.setProgress((workout.getProgress() + 1));
                wrapper.update_Workout(workout);
            }
        }

        wrapper.close();
    }

    public void change_WorkoutProgress(int progress){
        Workout_Wrapper wrapper = new Workout_Wrapper(this);
        wrapper.open();

        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(choiceWorkout)){
                workout.setMax((maxValue + progress));
                wrapper.update_Workout(workout);
            }
        }

        wrapper.close();
    }

    public void btnAction_workoutFinish_setDifficulty(View view) {
        switch ((view).getId()){
            case R.id.btn_workoutFinish_LevelHard:
                change_WorkoutProgress(PROG_INC_HARD);
                break;
            case R.id.btn_workoutFinish_LevelNeutral:
                change_WorkoutProgress(PROG_INC_NEUTRAL);
                break;
            case R.id.btn_workoutFinish_LevelEasy:
                change_WorkoutProgress(PROG_INC_EASY);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void btnAction_workoutFinish_NextWorkout(View view) {
    }

    public void btnAction_workoutFinish_FinishWorkout(View view) {
        startActivity(new Intent(this, HomeActivity.class));
    }
}
