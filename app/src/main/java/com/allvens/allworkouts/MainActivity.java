package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.StartWorkoutSession;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;
import com.allvens.allworkouts.ui.WorkoutCalendarView;

public class MainActivity extends AppCompatActivity {

    /* ---------- view refs ---------- */

    private LinearLayout       changeWorkoutButton;
    private TextView           workoutSelectorText;
    private ImageView          workoutSelectorArrow;
    private LinearLayoutCompat workoutChooser;
    private WorkoutCalendarView workoutCalendar;

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
        
        // Refresh calendar to show latest workout data
        if(workoutCalendar != null) {
            workoutCalendar.refreshCalendar();
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

        new StartWorkoutSession().startWorkout(this, chosenWorkout);
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
        changeWorkoutButton= findViewById(R.id.btn_ChangeWorkouts);
        workoutSelectorText= findViewById(R.id.tv_workout_selector_text);
        workoutSelectorArrow= findViewById(R.id.iv_workout_selector_arrow);
        workoutChooser     = findViewById(R.id.ll_home_WorkoutChooser);
        workoutCalendar    = findViewById(R.id.calendar_workout_activity);

        changeWorkoutButton.setOnClickListener(this::onChangeWorkoutsClicked);
    }

    private void refreshWorkouts() {
        WorkoutBasicsPrefs_Checker prefs = new WorkoutBasicsPrefs_Checker(this);
        WorkoutPosAndStatus[] pos       = prefs.getWorkoutPositions(false);

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
        // Update arrow to show "expanded" state (down arrow)
        workoutSelectorArrow.setImageResource(R.drawable.ic_expand_more_black_24dp);

        // Show the chooser with animation
        workoutChooser.setVisibility(android.view.View.VISIBLE);
        workoutChooser.setAlpha(0f);
        workoutChooser.animate()
            .alpha(1f)
            .setDuration(200)
            .start();

        for(String workoutName : workouts) {
            Button button = createWorkoutButton(workoutName);
            button.setOnClickListener(v -> {
                setWorkout(workoutName);
                // Add haptic feedback for modern feel
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                }
            });
            workoutChooser.addView(button);
        }
    }

    private void clearWorkoutChooser() {
        // Update arrow to show "collapsed" state (up arrow)
        workoutSelectorArrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
        
        // Hide the chooser with animation
        workoutChooser.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction(() -> {
                workoutChooser.setVisibility(android.view.View.GONE);
                workoutChooser.removeAllViews();
            })
            .start();
    }

    private Button createWorkoutButton(String name) {
        Button btn = new Button(this);
        btn.setText(name);
        
        // Apply the complete style programmatically since we're creating dynamically
        styleChooserButton(btn);
        
        // Set proper layout parameters
        LinearLayoutCompat.LayoutParams lp =
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.spacing_2);
        btn.setLayoutParams(lp);
        
        // Add proper content description for accessibility
        btn.setContentDescription(getString(R.string.select_workout_type, name));

        return btn;
    }

    private void styleChooserButton(Button button) {
        // Apply all style attributes programmatically for dynamic buttons
        button.setTextColor(getResources().getColor(R.color.selectedButton));
        button.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_body));
        button.setTypeface(button.getTypeface(), android.graphics.Typeface.BOLD);
        button.setAllCaps(false);
        
        // Set background drawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setBackground(getDrawable(R.drawable.bg_chooser_item));
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.bg_chooser_item));
        }
        
        // Set padding
        int paddingVertical = getResources().getDimensionPixelSize(R.dimen.spacing_3);
        int paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.spacing_2);
        button.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        
        // Set proper touch target size
        int minTouchSize = getResources().getDimensionPixelSize(R.dimen.touch_target_min);
        button.setMinHeight(minTouchSize);
        
        // Remove default button appearance
        button.setStateListAnimator(null);
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
        // Update the workout selector text to show current workout
        workoutSelectorText.setText(workout);
        // Ensure the arrow is properly set (collapsed state)
        workoutSelectorArrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
    }
}
