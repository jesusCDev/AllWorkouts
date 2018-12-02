package com.allvens.allworkouts.assets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.allvens.allworkouts.WorkoutMaximumActivity;
import com.allvens.allworkouts.WorkoutSessionActivity;
import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;

public class Start_WorkoutSession {

    public void start_Workout(Context context, String choiceWorkout){
        Intent intent;
        Workout_Wrapper wrapper = new Workout_Wrapper(context);
        wrapper.open();

        if(check_WorkoutExist(wrapper, choiceWorkout)){
            if(check_WorkoutProgress(wrapper, choiceWorkout)){
                intent = new Intent(context, WorkoutSessionActivity.class);
            }else{
                intent = new Intent(context, WorkoutMaximumActivity.class);
            }
            wrapper.close();
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, choiceWorkout);
            context.startActivity(intent);
        }else{
            wrapper.close();
            start_newSession(context, choiceWorkout);
        }
    }

    private boolean check_WorkoutExist(Workout_Wrapper wrapper, String choiceWorkout){
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(choiceWorkout)) {
                return true;
            }
        }
        return false;
    }

    private boolean check_WorkoutProgress(Workout_Wrapper wrapper, String choiceWorkout){
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(choiceWorkout)) {
                return (workout.getProgress() < 8);
            }
        }
        return false;
    }

    private void start_newSession(final Context context, final String choiceWorkout){
        final String[] workoutTypes = {"Simple", "Mix"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick workout type.");
        builder.setItems(workoutTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                create_Workout(which, context, choiceWorkout);
            }
        });
        builder.show();
    }

    private void create_Workout(int workoutType, Context context, String choiceWorkout){
        Intent intent = new Intent(context, WorkoutMaximumActivity.class);

        intent.putExtra(Constants.WORKOUT_TYPE_KEY, workoutType);
        intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, choiceWorkout);

        context.startActivity(intent);
    }
}
