package com.allvens.allworkouts.settings_manager.WorkoutPos;

import android.content.ClipData;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Sets up Touch Listener for when an item is being moved
 */
public class WorkoutPosTouchListener implements View.OnTouchListener {
    private View view;
    private Context context;

    public WorkoutPosTouchListener(Context context){
        this.context = context;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            this.view = view;

            // Add visual feedback for drag start - only scaling, no transparency
            view.setScaleX(1.05f);
            view.setScaleY(1.05f);

            ClipData data = ClipData.newPlainText("", "");

            // Use the actual view for the drag shadow instead of empty layout
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, shadowBuilder, view, 0);

            return true;
        }

        return false;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
