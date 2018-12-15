package com.allvens.allworkouts.workout_session_manager;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.allvens.allworkouts.LogActivity;
import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.WorkoutMaximumActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;

public class WorkoutMaximum_Manager {

    private Context context;

    private int maxValue = 1;
    private String chosenWorkout;
    private int type;

    private TextView tvCounterView;

    public WorkoutMaximum_Manager(Context context, TextView tvCounterView, String chosenWorkout, int type) {
        this.context = context;
        this.tvCounterView = tvCounterView;
        this.chosenWorkout = chosenWorkout;
        this.type = type;
    }

    private void update_Counter(){
        tvCounterView.setText(Integer.toString(maxValue));
    }

    /****************************************
     /**** MAX VALUE - METHODS
     ****************************************/

    /********** Value Editors **********/

    public void subtract_OneFromMax() {
        if(maxValue != 1){
            maxValue -= 1;
            update_Counter();
        }
    }

    public void add_OneToMax() {
        maxValue += 1;
        update_Counter();
    }

    public void add_FiveToMax() {
        maxValue += 5;
        update_Counter();
    }

    /********** Value Savor **********/

    public void update_WorkoutProgress(){
        Workout_Wrapper wrapper = new Workout_Wrapper(context);
        wrapper.open();

        boolean workoutExist = false;
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(chosenWorkout)){
                workout.setProgress(1);
                workout.setMax(maxValue);
                wrapper.update_Workout(workout);
                workoutExist = true;
            }
        }

        if(!workoutExist){
            wrapper.create_Workout(new Workout_Info(chosenWorkout, maxValue, type, 1));
        }

        wrapper.close();
    }

    /****************************************
     /**** SCREEN METHODS
     ****************************************/

    public void goTo_NewScreen(boolean updatingMaxInSettings) {
        Intent intent;
        if(updatingMaxInSettings){
            intent = new Intent(context, LogActivity.class);
            intent.putExtra(Constants.UPDATING_MAX_IN_SETTINGS, false);
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
        }else{
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.UPDATING_MAX_IN_SETTINGS, false);
        }
        context.startActivity(intent);
    }
}
