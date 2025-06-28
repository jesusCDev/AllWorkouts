package com.allvens.allworkouts.home_manager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.LogActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.Start_WorkoutSession;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;

public class HomeManager {

    private final Context context;
    private final HomeUiManager uiManager;
    private String[] workouts;
    private String chosenWorkout;
    private boolean workoutChooserOpen = false;

    public HomeManager(Context context, TextView currentWorkoutText, ImageView ivWorkout,
                       ImageButton changeWorkoutButton, LinearLayoutCompat llWorkoutChooser) {
        this.context = context;

        refreshWorkouts();

        uiManager     = new HomeUiManager(context, currentWorkoutText, ivWorkout, changeWorkoutButton, llWorkoutChooser);
        chosenWorkout = workouts[0];

        uiManager.updateScreen(workouts[0]);
    }

    public void refreshWorkouts() {
        WorkoutBasicsPrefs_Checker workout_basicsPrefs = new WorkoutBasicsPrefs_Checker(context);
        WorkoutPosAndStatus[] chosenWorkout            = workout_basicsPrefs.get_WorkoutsPos(false);
        workouts                                       = new String[chosenWorkout.length];

        for(int i = 0; i < chosenWorkout.length; i++) {
            workouts[i] = chosenWorkout[i].getName();
        }
    }

    public String getFirstWorkout() {
        return workouts[0];
    }

    public boolean check_IfCurrentWorkoutExistNow() {
        for(String workout: workouts) {
            if(workout.equalsIgnoreCase(chosenWorkout)) {
                return true;
            }
        }

        return false;
    }

    public void setWorkoutChooserOpen(boolean value) {
        workoutChooserOpen = value;
    }

    public boolean getWorkoutChooserOpen() {
        return workoutChooserOpen;
    }

    public void setWorkout(String workout) {
        chosenWorkout      = workout;
        workoutChooserOpen = false;

        uiManager.clearWorkoutChanger();
        uiManager.updateScreen(workout);
    }

    public View.OnClickListener updateWorkoutSelection(final String workout) {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWorkout(workout);
            }
        });
    }

    public void openWorkoutChanger() {
        uiManager.setExpandButton();

        Button[] buttons = uiManager.createWorkoutButtons(workouts);

        for(Button btn: buttons) {
            if(btn.getText().toString().equalsIgnoreCase(Constants.PULL_UPS)) {
                btn.setOnClickListener(updateWorkoutSelection(Constants.PULL_UPS));
            }
            else if(btn.getText().toString().equalsIgnoreCase(Constants.PUSH_UPS)) {
                btn.setOnClickListener(updateWorkoutSelection(Constants.PUSH_UPS));
            }
            else if(btn.getText().toString().equalsIgnoreCase(Constants.SIT_UPS)) {
                btn.setOnClickListener(updateWorkoutSelection(Constants.SIT_UPS));
            }
            else if(btn.getText().toString().equalsIgnoreCase(Constants.SQUATS)) {
                btn.setOnClickListener(updateWorkoutSelection(Constants.SQUATS));
            }
        }
    }

    public void clearWorkoutChanger() {
        uiManager.clearWorkoutChanger();
    }

    public void gotoWorkoutScene() {
        new Start_WorkoutSession().start_Workout(context, chosenWorkout);
    }

    public void gotoLogScreen() {
        Intent intent = new Intent(context, LogActivity.class);

        intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
        context.startActivity(intent);
    }
}
