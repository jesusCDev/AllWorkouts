package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.Start_WorkoutSession;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.settings_manager.WorkoutPosAndStatus;
import com.allvens.allworkouts.workout_session.workouts.PullUps;
import com.allvens.allworkouts.workout_session.workouts.PushUps;
import com.allvens.allworkouts.workout_session.workouts.SitUps;
import com.allvens.allworkouts.workout_session.workouts.Squats;
import com.allvens.allworkouts.workout_session.workouts.Workout;

public class WorkoutSessionFinishActivity extends AppCompatActivity{


    private int maxValue;
    private String choiceWorkout;

    private final static int PROG_INC_NEUTRAL = 1;
    private final static int PROG_INC_EASY = 2;
    private final static int PROG_INC_HARD = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finish);

        choiceWorkout = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        update_WorkoutProgress();
    }

    private void update_WorkoutProgress(){
        Workout_Wrapper wrapper = new Workout_Wrapper(this);
        wrapper.open();

        for(Workout_Info workout_info: wrapper.get_AllWorkouts()){
            if(workout_info.getWorkout().equalsIgnoreCase(choiceWorkout)){

                Workout workout;
                switch (workout_info.getWorkout()){
                    case Constants.PULL_UPS:
                        workout = new PullUps(workout_info.getType(), workout_info.getMax());
                        break;
                    case Constants.PUSH_UPS:
                        workout = new PushUps(workout_info.getType(), workout_info.getMax());
                        break;
                    case Constants.SIT_UPS:
                        workout = new SitUps(workout_info.getType(), workout_info.getMax());
                        break;
                    default:
                        workout = new Squats(workout_info.getType(), workout_info.getMax());
                        break;
                }

                maxValue = workout_info.getMax();
                wrapper.create_WorkoutHistory(new WorkoutHistory_Info(workout.get_WorkoutValue(0),
                        workout.get_WorkoutValue(1), workout.get_WorkoutValue(2),
                        workout.get_WorkoutValue(3), workout.get_WorkoutValue(4), maxValue), workout_info.getId());

                workout_info.setMax((workout_info.getMax() + PROG_INC_NEUTRAL));
                workout_info.setProgress((workout_info.getProgress() + 1));
                wrapper.update_Workout(workout_info);
            }
        }

        wrapper.close();
    }

    private void change_WorkoutProgress(int progress){
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
        WorkoutBasicsPrefs_Checker workoutsPos = new WorkoutBasicsPrefs_Checker(this);

        int pos = 0;
        for(WorkoutPosAndStatus workout: workoutsPos.get_WorkoutsPos(false)){
            if(workout.getName().equalsIgnoreCase(choiceWorkout)){
                pos = workout.getPosition();
                break;
            }
        }

        if(pos < (workoutsPos.get_WorkoutsPos(false).length - 1)){
            choiceWorkout = workoutsPos.get_WorkoutsPos(false)[(pos +1)].getName();
        }else{
            choiceWorkout = workoutsPos.get_WorkoutsPos(false)[0].getName();
        }
        new Start_WorkoutSession().start_Workout(this, choiceWorkout);
    }



    public void btnAction_workoutFinish_FinishWorkout(View view) {
        startActivity(new Intent(this, HomeActivity.class));
    }
}
