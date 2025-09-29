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
            case Constants.BACK_STRENGTHENING:
                workout = new BackStrengthening(workout_info.getType(), workout_info.getMax());
                break;
            default:
                workout = new Squats(workout_info.getType(), workout_info.getMax());
        }
        
        // Set the dynamic difficulty rating from database
        workout.setDifficultyRating(workout_info.getDifficultyRating());
        return workout;
    }
}
