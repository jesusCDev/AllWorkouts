package com.allvens.allworkouts.workout_session_manager.workouts;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;

public class WorkoutGenerator {

    private WorkoutInfo workout_info;

    public WorkoutGenerator(WorkoutInfo workout_info){
        this.workout_info = workout_info;
    }

    public WorkoutInfo get_WorkoutInfo() {
        return workout_info;
    }

    public Workout get_Workout(){
        switch (workout_info.getWorkout()){
            case Constants.PULL_UPS:
                return new PullUps(workout_info.getType(), workout_info.getMax());
            case Constants.PUSH_UPS:
                return new PushUps(workout_info.getType(), workout_info.getMax());
            case Constants.SIT_UPS:
                return new SitUps(workout_info.getType(), workout_info.getMax());
            default:
                return new Squats(workout_info.getType(), workout_info.getMax());
        }
    }
}
