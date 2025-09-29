package com.allvens.allworkouts.workout_session_manager.workouts;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;

public class WorkoutGenerator {

    private WorkoutInfo workoutInfo;

    public WorkoutGenerator(WorkoutInfo workoutInfo){
        this.workoutInfo = workoutInfo;
    }

    public WorkoutInfo getWorkoutInfo() {
        return workoutInfo;
    }

    public Workout getWorkout(){
        Workout workout;
        switch (workoutInfo.getWorkout()){
            case Constants.PULL_UPS:
                workout = new PullUps(workoutInfo.getType(), workoutInfo.getMax());
                break;
            case Constants.PUSH_UPS:
                workout = new PushUps(workoutInfo.getType(), workoutInfo.getMax());
                break;
            case Constants.SIT_UPS:
                workout = new SitUps(workoutInfo.getType(), workoutInfo.getMax());
                break;
            case Constants.BACK_STRENGTHENING:
                workout = new BackStrengthening(workoutInfo.getType(), workoutInfo.getMax());
                break;
            default:
                workout = new Squats(workoutInfo.getType(), workoutInfo.getMax());
        }
        
        // Set the dynamic difficulty rating from database
        workout.setDifficultyRating(workoutInfo.getDifficultyRating());
        return workout;
    }
}
