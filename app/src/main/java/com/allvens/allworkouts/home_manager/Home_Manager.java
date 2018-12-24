package com.allvens.allworkouts.home_manager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.allvens.allworkouts.LogActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.Start_WorkoutSession;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;

public class Home_Manager {

    private Context context;
    private Home_Ui_Manager uiManager;

    private String[] workouts;
    private String chosenWorkout;
    private boolean workoutChooserOpen = false;

    public Home_Manager(Context context, TextView tv_CurrentWorkout, ImageButton btn_ChangeWorkouts, LinearLayoutCompat ll_home_WorkoutChooser){

        this.context = context;
        setUp_WorkoutsPos();
        uiManager = new Home_Ui_Manager(context, tv_CurrentWorkout, btn_ChangeWorkouts, ll_home_WorkoutChooser);

        // todo set this to current workout you just observed in log
        chosenWorkout = workouts[0];
        uiManager.update_Screen(workouts[0]);

    }

    public void setUp_WorkoutsPos(){
        WorkoutBasicsPrefs_Checker workout_basicsPrefs = new WorkoutBasicsPrefs_Checker(context);

        WorkoutPosAndStatus[] chosenWorkout = workout_basicsPrefs.get_WorkoutsPos(false);

        workouts = new String[chosenWorkout.length];
        for(int i = 0; i < chosenWorkout.length; i++){
            workouts[i] = chosenWorkout[i].getName();
        }
    }

    /****************************************
     /**** WORKOUT SWITCHER
     ****************************************/

    /********** Setter/Getter Methods **********/

    public void set_WorkoutChooserOpen(boolean value){
        workoutChooserOpen = value;
    }

    public boolean get_WorkoutChooserOpen(){
        return workoutChooserOpen;
    }

    public void set_Workout(String workout){
        chosenWorkout = workout;
        workoutChooserOpen = false;
        uiManager.clear_WorkoutChanger();
        uiManager.update_Screen(workout);
    }

    /********** Switcher Methods **********/

    public View.OnClickListener update_WorkoutSelection(final String workout){
        return (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                set_Workout(workout);
            }
        });
    }

    public void open_WorkoutChanger(){
        uiManager.set_ExpandButton();

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

    /****************************************
     /**** SCREEN CHANGER
     ****************************************/

    public void goto_WorkoutScene(){
        new Start_WorkoutSession().start_Workout(context, chosenWorkout);
    }

    public void goto_LogScreen(){
        Intent intent = new Intent(context, LogActivity.class);
        intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
        context.startActivity(intent);
    }
}
