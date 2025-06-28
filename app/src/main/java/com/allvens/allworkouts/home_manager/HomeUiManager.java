package com.allvens.allworkouts.home_manager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.assets.Constants;

public class HomeUiManager {

    private Context context;
    private TextView currentWorkoutText;
    private ImageView ivWorkout;
    private ImageButton changeWorkoutButton;
    private LinearLayoutCompat llWorkoutChooser;

    public HomeUiManager(Context context, TextView currentWorkoutText, ImageView ivWorkout, ImageButton changeWorkoutButton, LinearLayoutCompat llWorkoutChooser){
        this.context             = context;
        this.currentWorkoutText  = currentWorkoutText;
        this.ivWorkout           = ivWorkout;
        this.changeWorkoutButton = changeWorkoutButton;
        this.llWorkoutChooser    = llWorkoutChooser;
    }

    public void updateScreen(String chosenWorkout){
        currentWorkoutText.setText(chosenWorkout);

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

        ivWorkout.setImageDrawable(resPath);
    }

    /****************************************
     /**** WORKOUT SWITCHER METHODS
     ****************************************/

    /********** Button Handler **********/

    public Button[] createWorkoutButtons(String[] workoutNames) {
        Button[] workoutButtons = new Button[workoutNames.length];

        for(int i = 0; i < workoutButtons.length; i++){
            workoutButtons[i] = createButton(workoutNames[i]);
        }

        return workoutButtons;
    }

    private Button createButton(String workoutName) {
        Button btn = new Button(context);

        btn.getBackground().setAlpha(0);
        llWorkoutChooser.addView(btn);
        btn.setText(workoutName);
        setButtonStyle(btn);

        return btn;
    }

    private void setButtonStyle(Button btn){
        if (Build.VERSION.SDK_INT < 23) {
            btn.setTextAppearance(context, R.style.btn_home_workoutChoice);
        }
        else {
            btn.setTextAppearance(R.style.btn_home_workoutChoice);
        }
    }

    public void clearWorkoutChanger(){
        closeExpandButton();
        llWorkoutChooser.removeAllViews();
    }

    public void setExpandButton() {
        changeWorkoutButton.setImageDrawable(context.getDrawable(R.drawable.ic_expand_more_black_24dp));
    }

    public void closeExpandButton() {
        changeWorkoutButton.setImageDrawable(context.getDrawable(R.drawable.ic_expand_less_black_24dp));
    }
}
