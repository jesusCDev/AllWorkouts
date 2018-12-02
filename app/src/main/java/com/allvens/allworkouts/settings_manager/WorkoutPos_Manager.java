package com.allvens.allworkouts.settings_manager;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
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
            llWorkoutsAndPositions.addView(create_WorkoutPosContainer(allWorkoutsAndPositions[0], allWorkoutsAndPositions[1]));
        }
    }

    private ConstraintLayout create_WorkoutPosContainer(String workoutName, String position){

        ConstraintLayout container = new ConstraintLayout(context);

        container.setOnTouchListener(new WorkoutPos_TouchListener());
        container.setOnDragListener(new WorkoutPos_DragListener());

        ImageView ivDragHandle = new ImageView(context);
        ivDragHandle.setImageResource(R.drawable.ic_drag_handle_black_24dp);
        ivDragHandle.setId(R.id.btn_Pos_id);
        TextView tvTitle = new TextView(context);
        tvTitle.setId(R.id.tv_Pos_Id);
        tvTitle.setText(workoutName);
        Switch sTurnOffOn = new Switch(context);
        sTurnOffOn.setId(R.id.s_Pos_Id);
        sTurnOffOn.setChecked(get_WorkPosStatus(position));
        sTurnOffOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                
            }
        });

        container.addView(ivDragHandle);
        container.addView(tvTitle);
        container.addView(sTurnOffOn);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        set.connect(ivDragHandle.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.connect(tvTitle.getId(), ConstraintSet.START, ivDragHandle.getId(), ConstraintSet.END);
        set.connect(sTurnOffOn.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);

        set.applyTo(container);

        return container;
    }

    private boolean get_WorkPosStatus(String pos){
        return (Integer.parseInt(pos) != 0);
    }


    public void pop(String message){
        Log.d("Bug", message);
    }
}
