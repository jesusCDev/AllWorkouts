package com.allvens.allworkouts.assets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.WorkoutMaximumActivity;
import com.allvens.allworkouts.WorkoutSessionActivity;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;

/**
 * Methods for starting workouts depending on their progress.
 */
public class StartWorkoutSession {

    public void startWorkout(Context context, String choiceWorkout){
        Intent intent;

        WorkoutWrapper wrapper = new WorkoutWrapper(context);

        wrapper.open();

        if(check_WorkoutExist(wrapper, choiceWorkout)){
            if(check_WorkoutProgress(wrapper, choiceWorkout)){
                intent = new Intent(context, WorkoutSessionActivity.class);
            }
            else{
                intent = new Intent(context, WorkoutMaximumActivity.class);

                intent.putExtra(Constants.UPDATING_MAX_IN_SETTINGS, false);
            }

            wrapper.close();
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, choiceWorkout);
            context.startActivity(intent);
        }
        else{
            wrapper.close();
            start_newSession(context, choiceWorkout);
        }
    }

    /****************************************
     /**** PROGRESS CHECKERS
     ****************************************/

    private boolean check_WorkoutExist(WorkoutWrapper wrapper, String choiceWorkout){
        for(WorkoutInfo workout: wrapper.getAllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(choiceWorkout)) {
                return true;
            }
        }

        return false;
    }

    private boolean check_WorkoutProgress(WorkoutWrapper wrapper, String choiceWorkout){
        for(WorkoutInfo workout: wrapper.getAllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(choiceWorkout)) {
                return (workout.getProgress() < 8);
            }
        }

        return false;
    }

    /****************************************
     /**** NEW WORKOUT
     ****************************************/

    private void start_newSession(final Context context, final String choiceWorkout){
        // todo change this!! to resources from variables
        final String[] workoutTypes = {context.getResources().getString(R.string.simple_workouts), context.getResources().getString(R.string.mix_workouts)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.pick_workout_type));
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

        intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, choiceWorkout);
        intent.putExtra(Constants.WORKOUT_TYPE_KEY, workoutType);
        intent.putExtra(Constants.UPDATING_MAX_IN_SETTINGS, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        context.startActivity(intent);
    }
}
