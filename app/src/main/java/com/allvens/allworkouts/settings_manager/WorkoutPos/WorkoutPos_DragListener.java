package com.allvens.allworkouts.settings_manager.WorkoutPos;

import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;

/**
 * Manages Drag functions for Workout Pos Change
 */
public class WorkoutPos_DragListener implements View.OnDragListener {

    private WorkoutPos_TouchListener touchListener;
    private WorkoutBasicsPrefs_Checker workout_basicsPrefs;
    private Context context;

    public WorkoutPos_DragListener(Context context, WorkoutPos_TouchListener touchListener, WorkoutBasicsPrefs_Checker workout_basicsPrefs){
        this.context = context;
        this.workout_basicsPrefs = workout_basicsPrefs;
        this.touchListener = touchListener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {

        View view = (View) event.getLocalState();
        ViewGroup owner = (ViewGroup) view.getParent();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundColor(context.getResources().getColor(R.color.focusAccent));
                update_View(owner, owner.indexOfChild(v));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundColor(Color.TRANSPARENT);
                break;
            case DragEvent.ACTION_DROP:
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundColor(Color.TRANSPARENT);
                workout_basicsPrefs.update_WorkoutsWithViews(owner);
            default:
                break;
        }
        return true;
    }

    /**
     * Moves View To New View Position
     * @param owner
     * @param index
     */
    private void update_View(ViewGroup owner, int index){
        owner.removeView(touchListener.getView());
        owner.addView(touchListener.getView(), index);
    }
}