package com.allvens.allworkouts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.Start_WorkoutSession;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout_Generator;

public class WorkoutSessionFinishActivity extends AppCompatActivity{

    private int maxValue;
    private String currentChoiceWorkout;
    private String nextChoiceWorkout;

    private final static int PROG_INC_NEUTRAL = 1;
    private final static int PROG_INC_EASY = 2;
    private final static int PROG_INC_HARD = -2;

    private Workout_Wrapper wrapper;
    private Button lastBtnSelected;
    private Button btnNextWorkout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finish);
        Button btnNextWorkout = findViewById(R.id.btn_WorkoutFinish_NextWorkout);

        currentChoiceWorkout = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);

        lastBtnSelected = findViewById(R.id.btn_workoutFinish_LevelNeutral);
        ((TextView)findViewById(R.id.tv_workoutFinish_WorkoutName)).setText(currentChoiceWorkout);
        wrapper = new Workout_Wrapper(this);
        wrapper.open();
        set_NextWorkout(btnNextWorkout);
        update_WorkoutProgress();
    }

    private void set_NextWorkout(Button btnNextWorkout){
        WorkoutBasicsPrefs_Checker workoutsPos = new WorkoutBasicsPrefs_Checker(this);

        // get current position
        int currentWorkout_Pos = 0;
        for(WorkoutPosAndStatus workout: workoutsPos.get_WorkoutsPos(false)){
            if(workout.getName().equalsIgnoreCase(currentChoiceWorkout)){
                break;
            }
            currentWorkout_Pos++;
        }

        // find workout in next position
        if(currentWorkout_Pos != (workoutsPos.get_WorkoutsPos(false).length - 1)){
            nextChoiceWorkout = workoutsPos.get_WorkoutsPos(false)[(currentWorkout_Pos + 1)].getName();
        }else{
            nextChoiceWorkout = workoutsPos.get_WorkoutsPos(false)[0].getName();
        }
        btnNextWorkout.setText(nextChoiceWorkout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wrapper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wrapper.open();
    }

    /****************************************
     /**** DATABASE MANAGER
     ****************************************/

    /********** History Creator **********/

    private void update_WorkoutProgress(){

        Workout_Generator workoutGenerator = new Workout_Generator(wrapper.get_Workout(currentChoiceWorkout));

        Workout workout = workoutGenerator.get_Workout();
        Workout_Info workout_info = workoutGenerator.get_WorkoutInfo();

        maxValue = workout_info.getMax();
        wrapper.create_WorkoutHistory(new WorkoutHistory_Info(workout.get_WorkoutValue(0),
        workout.get_WorkoutValue(1), workout.get_WorkoutValue(2),
        workout.get_WorkoutValue(3), workout.get_WorkoutValue(4), maxValue), workout_info.getId());

        workout_info.setMax((workout_info.getMax() + PROG_INC_NEUTRAL));
        workout_info.setProgress((workout_info.getProgress() + 1));

        wrapper.update_Workout(workout_info);
    }

    /********** Max Updater **********/

    private void update_WorkoutProgress(int progress){
        Workout_Info workout = wrapper.get_Workout(currentChoiceWorkout);

        int value = maxValue + progress;
        if(value <= 0) value = 1;

        workout.setMax(value);
        wrapper.update_Workout(workout);
    }

    /****************************************
     /**** BUTTON ACTIONS
     ****************************************/

    public void btnAction_setDifficulty(View view) {
        lastBtnSelected.setTextColor(this.getResources().getColor(R.color.unSelectedButton));
        ((Button)view).setTextColor(Color.BLACK);
        lastBtnSelected = ((Button)view);

        switch ((view).getId()){
            case R.id.btn_workoutFinish_LevelHard:
                update_WorkoutProgress(PROG_INC_HARD);
                break;
            case R.id.btn_workoutFinish_LevelNeutral:
                update_WorkoutProgress(PROG_INC_NEUTRAL);
                break;
            case R.id.btn_workoutFinish_LevelEasy:
                update_WorkoutProgress(PROG_INC_EASY);
                break;
        }
    }

    public void btnAction_NextWorkout(View view) {
        new Start_WorkoutSession().start_Workout(this, nextChoiceWorkout);
    }

    public void btnAction_GoHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
