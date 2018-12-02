package com.allvens.allworkouts.settings_manager;

import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;

class WorkoutPos_DragListener implements View.OnDragListener {

//    private WorkoutPos_Animation animator;
    private WorkoutPos_TouchListener touchListener;
    private WorkoutBasicsPrefs_Checker workout_basicsPrefs;


    public WorkoutPos_DragListener(WorkoutPos_TouchListener touchListener, WorkoutBasicsPrefs_Checker workout_basicsPrefs){
        this.workout_basicsPrefs = workout_basicsPrefs;
        this.touchListener = touchListener;
    }

    int enteredValue;
    int exitedValue;

    @Override
    public boolean onDrag(View v, DragEvent event) {

        View view = (View) event.getLocalState();
        ViewGroup owner = (ViewGroup) view.getParent();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundColor(Color.BLUE);
                pop("Entered: " + v.getId());
                pop("Index: " + owner.indexOfChild(v));
                update_View(owner, owner.indexOfChild(v));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundColor(Color.RED);
                break;
            case DragEvent.ACTION_DROP:
                v.setBackgroundColor(Color.YELLOW);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundColor(Color.GREEN);
                workout_basicsPrefs.update_WorkoutsWithViews(owner);
            default:
                break;
        }
        return true;
    }

    private void update_View(ViewGroup owner, int index){
        pop("Ran");
        owner.removeView(touchListener.getView());
        owner.addView(touchListener.getView(), index);
    }

    private static void pop(String messge){
        Log.d("Bug", messge);
    }
}