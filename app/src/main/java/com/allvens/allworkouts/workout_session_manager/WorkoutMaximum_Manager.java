package com.allvens.allworkouts.workout_session_manager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.allvens.allworkouts.LogActivity;
import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.R;
import com.allvens.allworkouts.WorkoutSessionActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.SessionUtils;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;

public class WorkoutMaximum_Manager {

    private Context context;

    private int maxValue = 1;
    private int startingMax = 1;
    private String chosenWorkout;
    private int type;
    private String sessionStartWorkout; // The workout that started this session

    private TextView tvCounterView;

    public WorkoutMaximum_Manager(Context context, TextView tvCounterView, String chosenWorkout, int type, String sessionStartWorkout) {
        this.context = context;
        this.tvCounterView = tvCounterView;
        this.chosenWorkout = chosenWorkout;
        this.type = type;
        this.sessionStartWorkout = sessionStartWorkout;
        
        // Save session start to SharedPreferences as a safety net
        if (sessionStartWorkout != null) {
            SessionUtils.saveSessionStart(context, sessionStartWorkout);
        }

        set_lastWMaxWorkoutValue();
    }

    private void set_lastWMaxWorkoutValue(){

        Log.d("Bug", chosenWorkout);

        WorkoutWrapper wrapper = new WorkoutWrapper(context);

        wrapper.open();
        WorkoutInfo workout_info = wrapper.getWorkout(chosenWorkout);
        wrapper.close();

        if(workout_info != null){
            maxValue = workout_info.getMax();
            startingMax = maxValue;
            tvCounterView.setText(Integer.toString(workout_info.getMax()));
        }
    }

    private void update_Counter(){
        tvCounterView.setText(Integer.toString(maxValue));
        // Removed updateStyle() to keep consistent color
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
    
    /**
     * Gets the current max value
     * @return current max value
     */
    public int getCurrentMaxValue() {
        return maxValue;
    }
    
    /**
     * Gets the starting max value (for comparison)
     * @return starting max value
     */
    public int getStartingMaxValue() {
        return startingMax;
    }

    /********** Value Savor **********/

    public void update_WorkoutProgress(){
        WorkoutWrapper wrapper = new WorkoutWrapper(context);
        wrapper.open();

        boolean workoutExist = false;
        for(WorkoutInfo workout: wrapper.getAllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(chosenWorkout)){
                workout.setProgress(1);
                workout.setMax(maxValue);
                wrapper.updateWorkout(workout);
                workoutExist = true;
            }
        }

        if(!workoutExist){
            wrapper.createWorkout(new WorkoutInfo(chosenWorkout, maxValue, type, 1));
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
            // Start the workout session with the configured max value
            intent = new Intent(context, WorkoutSessionActivity.class);
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
            
            // Thread the session start workout through to WorkoutSessionActivity
            if (sessionStartWorkout != null) {
                intent.putExtra(Constants.SESSION_START_WORKOUT_KEY, sessionStartWorkout);
            } else {
                // Fallback to SharedPreferences if extra was lost
                String fallbackStart = SessionUtils.getSessionStart(context);
                if (fallbackStart != null) {
                    intent.putExtra(Constants.SESSION_START_WORKOUT_KEY, fallbackStart);
                }
            }
        }
        context.startActivity(intent);
    }
}
