package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.Start_WorkoutSession;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;

public class MainActivity extends AppCompatActivity {

    /* ---------- view refs ---------- */

    private TextView           currentWorkoutText;
    private ImageView          workoutImage;
    private ImageButton        changeWorkoutButton;
    private LinearLayoutCompat workoutChooser;

    /* ---------- state ---------- */

    private String[] workouts      = new String[0];
    private String   chosenWorkout = null;
    private boolean  chooserIsOpen = false;

    /* ====================================================================== */
    /*  LIFECYCLE                                                             */
    /* ====================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindViews();
        refreshWorkouts();

        if(workouts.length > 0) {
            chosenWorkout = workouts[0];

            updateScreen(chosenWorkout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWorkouts();

        if(chosenWorkout == null || !workoutExists(chosenWorkout)) {
            setWorkout(workouts.length > 0 ? workouts[0] : null);
        }
    }

    /* ====================================================================== */
    /*  PUBLIC CLICK HANDLERS  (referenced from XML)                          */
    /* ====================================================================== */

    public void onChangeWorkoutsClicked(View view) {
        if(chooserIsOpen) {
            clearWorkoutChooser();
        }
        else {
            openWorkoutChooser();
        }

        chooserIsOpen = !chooserIsOpen;
    }

    public void onStartWorkoutClicked(View view) {
        if(chosenWorkout == null) return;

        new Start_WorkoutSession().start_Workout(this, chosenWorkout);
    }

    public void onSettingsClicked(View view) {
        clearWorkoutChooser();                    // close the chooser if it’s open

        chooserIsOpen = false;

        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onLogClicked(View view) {
        if(chosenWorkout == null) return;

        Intent intent = new Intent(this, LogActivity.class);

        intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
        startActivity(intent);
    }

    /* ====================================================================== */
    /*  SET-UP / STATE HELPERS                                                */
    /* ====================================================================== */

    private void bindViews() {
        currentWorkoutText = findViewById(R.id.tv_home_CurrentWorkout);
        workoutImage       = findViewById(R.id.iv_home_WorkoutShow);
        changeWorkoutButton= findViewById(R.id.btn_ChangeWorkouts);
        workoutChooser     = findViewById(R.id.ll_home_WorkoutChooser);

        changeWorkoutButton.setOnClickListener(this::onChangeWorkoutsClicked);
    }

    private void refreshWorkouts() {
        WorkoutBasicsPrefs_Checker prefs = new WorkoutBasicsPrefs_Checker(this);
        WorkoutPosAndStatus[] pos       = prefs.get_WorkoutsPos(false);

        workouts = new String[pos.length];
        
        for(int i = 0; i < pos.length; i++) {
            workouts[i] = pos[i].getName();
        }
    }

    private boolean workoutExists(String workoutName) {
        for(String w : workouts) {
            if(w.equalsIgnoreCase(workoutName)) return true;
        }

        return false;
    }

    /* ====================================================================== */
    /*  WORKOUT CHOOSER                                                       */
    /* ====================================================================== */

    private void openWorkoutChooser() {
        changeWorkoutButton.setImageDrawable(getDrawable(R.drawable.ic_expand_more_black_24dp));

        for(String workoutName : workouts) {
            final String name = workoutName;
            Button button     = createWorkoutButton(name);

            button.setOnClickListener(v -> setWorkout(name));
            workoutChooser.addView(button);
        }
    }

    private void clearWorkoutChooser() {
        changeWorkoutButton.setImageDrawable(getDrawable(R.drawable.ic_expand_less_black_24dp));
        workoutChooser.removeAllViews();
    }

    private Button createWorkoutButton(String workoutName) {
        Button button = new Button(this);

        button.getBackground().setAlpha(0);            // transparent background
        button.setText(workoutName);
        styleChooserButton(button);

        return button;
    }

    private void styleChooserButton(Button button) {
        if(Build.VERSION.SDK_INT < 23) {
            button.setTextAppearance(this, R.style.btn_home_workoutChoice);
        }
        else {
            button.setTextAppearance(R.style.btn_home_workoutChoice);
        }
    }

    /* ====================================================================== */
    /*  SINGLE SOURCE OF TRUTH FOR UI UPDATE                                  */
    /* ====================================================================== */

    private void setWorkout(String workout) {
        if(workout == null) return;
        chosenWorkout = workout;
        chooserIsOpen = false;

        clearWorkoutChooser();
        updateScreen(workout);
    }

    private void updateScreen(String workout) {
        currentWorkoutText.setText(workout);

        int drawableRes;

        switch (workout) {
            case Constants.PULL_UPS: drawableRes = R.drawable.ic_pullup; break;
            case Constants.SIT_UPS : drawableRes = R.drawable.ic_situp ; break;
            case Constants.PUSH_UPS: drawableRes = R.drawable.ic_pushup; break;
            default                : drawableRes = R.drawable.ic_squat ; break;
        }

        workoutImage.setImageDrawable(getResources().getDrawable(drawableRes));
    }
}
