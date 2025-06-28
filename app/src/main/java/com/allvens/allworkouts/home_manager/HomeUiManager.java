package com.allvens.allworkouts.home_manager;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.assets.Constants;

public class HomeUiManager {

    private final Context context;
    private final TextView current_workout_text;
    private final ImageView iv_workout;
    private final ImageButton change_workout_button;
    private final LinearLayoutCompat ll_workout_chooser;

    public HomeUiManager(Context context, TextView current_workout_text, ImageView iv_workout, ImageButton change_workout_button, LinearLayoutCompat ll_workout_chooser) {
        this.context               = context;
        this.current_workout_text  = current_workout_text;
        this.iv_workout            = iv_workout;
        this.change_workout_button = change_workout_button;
        this.ll_workout_chooser    = ll_workout_chooser;
    }

    public void updateScreen(String chosen_workout) {
        current_workout_text.setText(chosen_workout);

        int resource_path;

        switch(chosen_workout) {
            case Constants.PULL_UPS:
                resource_path = R.drawable.ic_pullup;
                break;
            case Constants.SIT_UPS:
                resource_path = R.drawable.ic_situp;
                break;
            case Constants.PUSH_UPS:
                resource_path = R.drawable.ic_pushup;
                break;
            default:
                resource_path = R.drawable.ic_squat;
                break;
        }

        iv_workout.setImageDrawable(context.getResources().getDrawable(resource_path));
    }

    public Button[] createWorkoutButtons(String[] workout_names) {
        Button[] workout_buttons = new Button[workout_names.length];

        for(int i = 0; i < workout_buttons.length; i++) {
            workout_buttons[i] = createButton(workout_names[i]);
        }

        return workout_buttons;
    }

    private Button createButton(String workout_name) {
        Button button = new Button(context);

        button.getBackground().setAlpha(0);
        ll_workout_chooser.addView(button);
        button.setText(workout_name);
        setButtonStyle(button);

        return button;
    }

    private void setButtonStyle(Button button) {
        if (Build.VERSION.SDK_INT < 23) {
            button.setTextAppearance(context, R.style.btn_home_workoutChoice);
        }
        else {
            button.setTextAppearance(R.style.btn_home_workoutChoice);
        }
    }

    public void clearWorkoutChanger() {
        closeExpandButton();
        ll_workout_chooser.removeAllViews();
    }

    public void setExpandButton() {
        change_workout_button.setImageDrawable(context.getDrawable(R.drawable.ic_expand_more_black_24dp));
    }

    public void closeExpandButton() {
        change_workout_button.setImageDrawable(context.getDrawable(R.drawable.ic_expand_less_black_24dp));
    }
}
