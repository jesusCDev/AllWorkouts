package com.allvens.allworkouts.workout_session_manager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.allvens.allworkouts.LogActivity;
import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.R;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;

public class WorkoutMaximum_Manager {

    private Context context;

    private int maxValue = 1;
    private int startingMax = 1;
    private String chosenWorkout;
    private int type;

    private TextView tvCounterView;

    public WorkoutMaximum_Manager(Context context, TextView tvCounterView, String chosenWorkout, int type) {
        this.context = context;
        this.tvCounterView = tvCounterView;
        this.chosenWorkout = chosenWorkout;
        this.type = type;

        set_lastWMaxWorkoutValue();
    }

    private void set_lastWMaxWorkoutValue(){

        Log.d("Bug", chosenWorkout);

        Workout_Wrapper wrapper = new Workout_Wrapper(context);

        wrapper.open();
        Workout_Info workout_info = wrapper.get_Workout(chosenWorkout);
        wrapper.close();

        if(workout_info != null){
            maxValue = workout_info.getMax();
            startingMax = maxValue;
            tvCounterView.setText(Integer.toString(workout_info.getMax()));
        }
    }

    private void update_Counter(){
        tvCounterView.setText(Integer.toString(maxValue));
        updateStyle();
    }

    private void updateStyle(){
        int style;
        if(startingMax < maxValue){
            style = R.style.tv_WorkoutMax_Positive;
        }else{
            style = R.style.tv_WorkoutMax_Negative;
        }
            setStyle_ForTextView(tvCounterView, style);
    }

    private void setStyle_ForTextView(TextView tv, int style){
        if (Build.VERSION.SDK_INT < 23) {
            tv.setTextAppearance(context, style);
        } else {
            tv.setTextAppearance(style);
        }
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
