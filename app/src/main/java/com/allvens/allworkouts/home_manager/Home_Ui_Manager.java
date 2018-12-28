package com.allvens.allworkouts.home_manager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;

public class Home_Ui_Manager {

    private Context context;
    private TextView tv_CurrentWorkout;
    private ImageView iv_Workout;
    private ImageButton btn_ChangeWorkouts;
    private LinearLayoutCompat ll_home_WorkoutChooser;

    public Home_Ui_Manager(Context context, TextView tv_CurrentWorkout, ImageView iv_Workout, ImageButton btn_ChangeWorkouts, LinearLayoutCompat ll_home_WorkoutChooser){
        this.context = context;
        this.tv_CurrentWorkout = tv_CurrentWorkout;
        this.iv_Workout = iv_Workout;
        this.btn_ChangeWorkouts = btn_ChangeWorkouts;
        this.ll_home_WorkoutChooser = ll_home_WorkoutChooser;
    }

    public void update_Screen(String chosenWorkout){
        tv_CurrentWorkout.setText(chosenWorkout);

        Drawable resPath = null;
        switch (chosenWorkout){
            case Constants.PULL_UPS:
                resPath = context.getResources().getDrawable(R.drawable.ic_pullup);
                break;
            case Constants.SIT_UPS:
                resPath = context.getResources().getDrawable(R.drawable.ic_situp);
                break;
            case Constants.PUSH_UPS:
                resPath = context.getResources().getDrawable(R.drawable.ic_pushup);
                break;
            default:
                resPath = context.getResources().getDrawable(R.drawable.ic_squat);
                break;
        }

        iv_Workout.setImageDrawable(resPath);
    }

    /****************************************
     /**** WORKOUT SWITCHER METHODS
     ****************************************/

    /********** Button Handler **********/

    public Button[] create_WorkoutButtons(String[] workoutNames) {
        Button[] workoutButtons = new Button[workoutNames.length];
        for(int i = 0; i < workoutButtons.length; i++){
            workoutButtons[i] = create_Button(workoutNames[i]);
        }
        return workoutButtons;
    }

    private Button create_Button(String workoutName) {
        Button btn = new Button(context);
        btn.getBackground().setAlpha(0);
        ll_home_WorkoutChooser.addView(btn);

        btn.setText(workoutName);
        set_BtnChoiceStyle(btn);
        return btn;
    }

    private void set_BtnChoiceStyle(Button btn){
        if (Build.VERSION.SDK_INT < 23) {
            btn.setTextAppearance(context, R.style.btn_home_workoutChoice);
        } else {
            btn.setTextAppearance(R.style.btn_home_workoutChoice);
        }
    }

    /********** Expand/Collapse Methods **********/

    public void clear_WorkoutChanger(){
        set_CloseExpandButton();
        ll_home_WorkoutChooser.removeAllViews();
    }

    public void set_ExpandButton() {
        btn_ChangeWorkouts.setImageDrawable(context.getDrawable(R.drawable.ic_expand_more_black_24dp));
    }

    public void set_CloseExpandButton() {
        btn_ChangeWorkouts.setImageDrawable(context.getDrawable(R.drawable.ic_expand_less_black_24dp));
    }
}
