package com.allvens.allworkouts.workout_manager;

import android.content.Context;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.database.Workout_Info;
import com.allvens.allworkouts.database.Workout_Wrapper;
import com.allvens.allworkouts.workout_manager.workouts_mix.PullUps;

public class WorkoutSession_Manager {

    private Context context;
    private Workout workout;
    private Workout_Info workout_info;
    private UI_Manager ui_manager = new UI_Manager();

    private int progress = 0;

    public WorkoutSession_Manager(Context context, String choice){
        this.context = context;

        // get workout info
        Workout_Wrapper wrapper = new Workout_Wrapper(context);
        wrapper.open();
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(choice)){
                this.workout_info = workout;
            }
        }
        wrapper.close();

        // create workout
        switch (workout_info.getWorkout()){
            case Constants.PULL_UPS:
                workout = new PullUps(workout_info.getType());
                break;
            case Constants.PUSH_UPS:
                break;
            case Constants.SIT_UPS:
                break;
            case Constants.SQUATS:
                break;
        }

        // set workout values
        workout.set_Max(workout_info.getMax());
        workout.create_WorkoutValues();
    }

    public void check_Progress(){
        if(progress == 5){
            // todo exit out of activity
        }
    }

    public void update_UI(){

    }
}
