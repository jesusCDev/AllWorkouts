package com.allvens.allworkouts.settings_manager;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.Workout_Pos;

public class Settings_Manager {

    private Context context;

    public Settings_Manager(Context context){
        this.context = context;
    }

    public void setUp_WorkoutsAndPositions(LinearLayout llWorkoutsAndPositions) {
        Workout_Pos workout_pos = new Workout_Pos(context);
        for(String[] allWorkoutsAndPositions: workout_pos.get_AllWorkoutsAndPositions(true)) {
            llWorkoutsAndPositions.addView(create_WorkoutPosContainer(allWorkoutsAndPositions[0]));
        }
    }

    private ConstraintLayout create_WorkoutPosContainer(String workoutName){

        ConstraintLayout container = new ConstraintLayout(context);

        Button btnHover = new Button(context);
        btnHover.setText("=");
        btnHover.setId(R.id.btn_Pos_id);
        pop("Id: " + btnHover.getId());
        TextView tvTitle = new TextView(context);
        tvTitle.setId(R.id.tv_Pos_Id);
        tvTitle.setText(workoutName);
//        Switch sTurnOffOn = new Switch(context);

        container.addView(btnHover);
        container.addView(tvTitle);
//        container.addView(sTurnOffOn);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        set.connect(btnHover.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.connect(btnHover.getId(), ConstraintSet.RIGHT, tvTitle.getId(), ConstraintSet.LEFT);
        set.connect(tvTitle.getId(), ConstraintSet.START, btnHover.getId(), ConstraintSet.END);
        set.connect(tvTitle.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
//        set.setHorizontalChainStyle(btnHover.getId(),ConstraintSet.CHAIN_PACKED);
//        set.setHorizontalChainStyle(tvTitle.getId(),ConstraintSet.CHAIN_PACKED);

        set.applyTo(container);

        return container;
    }


    public void pop(String message){
        Log.d("Bug", message);
    }
}
