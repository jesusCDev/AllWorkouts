package com.allvens.allworkouts.home_manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allvens.allworkouts.WorkoutMaximumActivity;
import com.allvens.allworkouts.WorkoutSessionActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.database.Workout_Info;
import com.allvens.allworkouts.database.Workout_Wrapper;

public class HomeManager {

    private Context context;
    private Home_Ui_Manager uiManager;
    private String[] workouts = {Constants.PULL_UPS, Constants.PUSH_UPS, Constants.SIT_UPS, Constants.SQUATS}; // todo check this out
    private String chosenWorkout;
    private boolean workoutChooserOpen = false;

    public boolean get_WorkoutChooserOpen(){
        return workoutChooserOpen;
    }

    public void set_WorkoutChooserOpen(boolean value){
        workoutChooserOpen = value;
    }

    public HomeManager(Context context, TextView tv_CurrentWorkout, Button btn_ChangeWorkouts, LinearLayoutCompat ll_home_WorkoutChooser){
        this.context = context;
        // todo get data for workout sessions

        // todo set default value

        // todo setup screen
        uiManager = new Home_Ui_Manager(context, tv_CurrentWorkout, btn_ChangeWorkouts, ll_home_WorkoutChooser);
        // todo fix this
        chosenWorkout = workouts[1];
        uiManager.update_Screen(workouts[1]);
    }

    public View.OnClickListener update_WorkoutSelection(final String workout){
        return (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chosenWorkout = workout;
                workoutChooserOpen = !workoutChooserOpen;
                uiManager.clear_WorkoutChanger();
                uiManager.update_Screen(workout);
            }
        });
    }

    public void open_WorkoutChanger(){

        Button[] buttons = uiManager.create_WorkoutButtons(workouts);
        for(Button btn: buttons){
            if(btn.getText().toString().equalsIgnoreCase(Constants.PULL_UPS)){
                btn.setOnClickListener(update_WorkoutSelection(Constants.PULL_UPS));
            }else if(btn.getText().toString().equalsIgnoreCase(Constants.PUSH_UPS)){
                btn.setOnClickListener(update_WorkoutSelection(Constants.PUSH_UPS));
            }else if(btn.getText().toString().equalsIgnoreCase(Constants.SIT_UPS)){
                btn.setOnClickListener(update_WorkoutSelection(Constants.SIT_UPS));
            }else if(btn.getText().toString().equalsIgnoreCase(Constants.SQUATS)){
                btn.setOnClickListener(update_WorkoutSelection(Constants.SQUATS));
            }
        }
    }

    public void clear_WorkoutChanger(){
        uiManager.clear_WorkoutChanger();
    }

    public void start_Workout(){
        Intent intent;
        Workout_Wrapper wrapper = new Workout_Wrapper(context);
        wrapper.open();

        if(check_WorkoutExist(wrapper)){
            if(check_WorkoutProgress(wrapper)){
                intent = new Intent(context, WorkoutSessionActivity.class);
            }else{
                intent = new Intent(context, WorkoutMaximumActivity.class);
            }
            wrapper.close();
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
            context.startActivity(intent);
        }else{
            wrapper.close();
            start_newSession();
        }
    }

    private boolean check_WorkoutExist(Workout_Wrapper wrapper){
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(chosenWorkout)) {
                return true;
            }
        }
        return false;
    }

    private boolean check_WorkoutProgress(Workout_Wrapper wrapper){
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(chosenWorkout)) {
                return (workout.getProgress() < 8);
            }
        }
        return false;
    }

    private void start_newSession(){
        final String[] workoutTypes = {"Simple", "Mix"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick workout type.");
        builder.setItems(workoutTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                create_Workout(which);
            }
        });
        builder.show();
    }

    private void create_Workout(int workoutType){
        Intent intent = new Intent(context, WorkoutMaximumActivity.class);
        intent.putExtra(Constants.WORKOUT_TYPE_KEY, workoutType);
        intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
        context.startActivity(intent);
    }
}
