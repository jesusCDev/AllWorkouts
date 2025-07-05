package com.allvens.allworkouts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.StartWorkoutSession;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.allvens.allworkouts.workout_session_manager.workouts.WorkoutGenerator;

public class WorkoutSessionFinishActivity extends AppCompatActivity{

    private int maxValue;
    private String currentChoiceWorkout;
    private String nextChoiceWorkout;

    private final static int PROG_INC_NEUTRAL = 1;
    private final static int PROG_INC_EASY    = 2;
    private final static int PROG_INC_HARD    = -2;

    private WorkoutWrapper wrapper;
    private Button lastButtonSelected;

    @Override
    protected void onPause() {
        super.onPause();
        wrapper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wrapper.open();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finish);

        Button nextWorkoutButton = findViewById(R.id.btn_WorkoutFinish_NextWorkout);
        currentChoiceWorkout     = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        lastButtonSelected       = findViewById(R.id.btn_workoutFinish_LevelNeutral);

        ((TextView)findViewById(R.id.tv_workoutFinish_WorkoutName)).setText(currentChoiceWorkout);

        wrapper = new WorkoutWrapper(this);

        wrapper.open();
        setNextWorkout(nextWorkoutButton);

        WorkoutGenerator workoutGenerator = new WorkoutGenerator(wrapper.get_Workout(currentChoiceWorkout));
        Workout workout                   = workoutGenerator.get_Workout();
        WorkoutInfo workoutInfo           = workoutGenerator.get_WorkoutInfo();
        maxValue                          = workoutInfo.getMax();

        wrapper.createWorkoutHistory(
                new WorkoutHistory_Info(
                        workout.getWorkoutValue(0),
                        workout.getWorkoutValue(1),
                        workout.getWorkoutValue(2),
                        workout.getWorkoutValue(3),
                        workout.getWorkoutValue(4),
                        maxValue
                ),
                workoutInfo.getId()
        );

        workoutInfo.setMax((workoutInfo.getMax() + PROG_INC_NEUTRAL));
        workoutInfo.setProgress((workoutInfo.getProgress() + 1));
        wrapper.update_Workout(workoutInfo);
    }

    private void setNextWorkout(Button nextWorkoutButton) {
        WorkoutPosAndStatus[] workouts = new WorkoutBasicsPrefs_Checker(this).get_WorkoutsPos(false);

        /* locate current workout in the ordered array */
        int current_workout_index = 0;

        for(WorkoutPosAndStatus w : workouts) {
            if(w.getName().equalsIgnoreCase(currentChoiceWorkout)) break;
            current_workout_index++;
        }

        /* if there IS a next item → label it; otherwise hide the button */
        if(current_workout_index < workouts.length - 1) {
            nextChoiceWorkout = workouts[current_workout_index + 1].getName();

            nextWorkoutButton.setText(nextChoiceWorkout);
            nextWorkoutButton.setVisibility(View.VISIBLE);
        }
        else {
            nextChoiceWorkout = null;                 // no next workout

            nextWorkoutButton.setVisibility(View.GONE);  // hide the button
        }
    }

    private void updateWorkoutProgress(int progress){
        WorkoutInfo workout = wrapper.get_Workout(currentChoiceWorkout);
        int value           = maxValue + progress;

        if(value <= 0) {
            value = 1;
        }

        workout.setMax(value);
        wrapper.update_Workout(workout);
    }

    public void setDifficulty(View view) {
        lastButtonSelected.setTextColor(this.getResources().getColor(R.color.unSelectedButton));
        ((Button)view).setTextColor(Color.BLACK);

        lastButtonSelected = ((Button)view);

        switch ((view).getId()){
            case R.id.btn_workoutFinish_LevelHard:
                updateWorkoutProgress(PROG_INC_HARD);
                break;

            case R.id.btn_workoutFinish_LevelNeutral:
                updateWorkoutProgress(PROG_INC_NEUTRAL);
                break;

            case R.id.btn_workoutFinish_LevelEasy:
                updateWorkoutProgress(PROG_INC_EASY);
                break;
        }
    }

    public void startNextWorkout(View view) {
        if(nextChoiceWorkout != null) {
            new StartWorkoutSession().startWorkout(this, nextChoiceWorkout);
        }
    }

    public void goHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
