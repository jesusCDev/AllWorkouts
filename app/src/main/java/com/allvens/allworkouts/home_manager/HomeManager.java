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
    private final HomeUiManager ui_manager;
    private String[] workouts;
    private String chosen_workout;
    private boolean workout_chooser_open = false;

    public HomeManager(Context context, TextView currentWorkoutText, ImageView ivWorkout,
                       ImageButton changeWorkoutButton, LinearLayoutCompat llWorkoutChooser) {
        this.context = context;

        refreshWorkouts();

        ui_manager     = new HomeUiManager(context, currentWorkoutText, ivWorkout, changeWorkoutButton, llWorkoutChooser);
        chosen_workout = workouts[0];

        ui_manager.updateScreen(workouts[0]);
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

    public boolean checkIfCurrentWorkoutExistNow() {
        for(String workout: workouts) {
            if(workout.equalsIgnoreCase(chosen_workout)) {
                return true;
            }
        }

        return false;
    }

    public void setWorkout_chooser_open(boolean value) {
        workout_chooser_open = value;
    }

    public boolean isWorkoutChooserOpen() {
        return workout_chooser_open;
    }

    public void setWorkout(String workout) {
        chosen_workout = workout;
        workout_chooser_open = false;

        ui_manager.clearWorkoutChanger();
        ui_manager.updateScreen(workout);
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
        ui_manager.setExpandButton();

        Button[] buttons = ui_manager.createWorkoutButtons(workouts);

        for(Button btn : buttons) {
            String workoutName = btn.getText().toString();

            btn.setOnClickListener(updateWorkoutSelection(workoutName));
        }
    }

    public void clearWorkoutChanger() {
        ui_manager.clearWorkoutChanger();
    }

    public void gotoWorkoutScene() {
        new Start_WorkoutSession().start_Workout(context, chosen_workout);
    }

    public void gotoLogScreen() {
        Intent intent = new Intent(context, LogActivity.class);

        intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosen_workout);
        context.startActivity(intent);
    }
}
