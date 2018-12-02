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
import com.allvens.allworkouts.data_manager.WorkoutBasics_Prefs;

public class WorkoutPos_Manager {

    private Context context;

    public WorkoutPos_Manager(Context context){
        this.context = context;
    }

    public void setUp_WorkoutsAndPositions(LinearLayout llWorkoutsAndPositions) {
        WorkoutBasics_Prefs workout_basicsPrefs = new WorkoutBasics_Prefs(context);
        WorkoutPos_TouchListener touchListener = new WorkoutPos_TouchListener(context);

        for(WorkoutPosAndStatus allWorkoutsAndPositions: workout_basicsPrefs.get_WorkoutsPos(true)) {
            llWorkoutsAndPositions.addView(create_WorkoutPosContainer(allWorkoutsAndPositions, touchListener, workout_basicsPrefs ));
        }
    }

    private ConstraintLayout create_WorkoutPosContainer(final WorkoutPosAndStatus workout, WorkoutPos_TouchListener touchListener, final WorkoutBasics_Prefs workout_basicsPrefs ){

        ConstraintLayout container = new ConstraintLayout(context);
        container.setId(workout.getResourceID());

        container.setOnTouchListener(touchListener);
        container.setOnDragListener(new WorkoutPos_DragListener(touchListener, workout_basicsPrefs));

//        // todo passing this is wrong because it is only listening to the last object
//        container.setOnTouchListener(touchListener);
//        container.setOnDragListener(new WorkoutPos_DragListener(touchListener));

//        container.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                pop("CALLED");
//
//                int index;
//                        v.setBackgroundColor(Color.RED);
//
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_UP:
//                        pop("MOVED UP");
//                        index = llWorkoutsAndPositions.indexOfChild(v);
//                        llWorkoutsAndPositions.removeView(v);
//                        llWorkoutsAndPositions.addView(v, index--);
//                        break;
//                    case MotionEvent.ACTION_SCROLL:
//                        pop("MOVED DOWN");
//                        index = llWorkoutsAndPositions.indexOfChild(v);
//                        llWorkoutsAndPositions.removeView(v);
//                        llWorkoutsAndPositions.addView(v, index++);
//                        break;
//                }
//
//                return false;
//            }
//        });

        ImageView ivDragHandle = new ImageView(context);
        ivDragHandle.setImageResource(R.drawable.ic_drag_handle_black_24dp);
        ivDragHandle.setId(R.id.btn_Pos_id);

        TextView tvTitle = new TextView(context);
        tvTitle.setId(R.id.tv_Pos_Id);
        tvTitle.setText(workout.getName());

        Switch sTurnOffOn = new Switch(context);
        sTurnOffOn.setId(R.id.s_Pos_Id);
        sTurnOffOn.setChecked(workout.get_TurnOnStatus());
        sTurnOffOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workout_basicsPrefs.update_WorkoutStatusPref(workout.getStatPrefKey(), isChecked);
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

    private void pop(String message){
        Log.d("Bug", message);
    }
}
