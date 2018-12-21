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
public class WorkoutPos_TouchListener implements View.OnTouchListener {

    private View view;
    private Context context;

    public WorkoutPos_TouchListener(Context context){
        this.context = context;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            this.view = view;

            view.setVisibility(View.INVISIBLE);
            ClipData data = ClipData.newPlainText("", "");

            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    new ConstraintLayout(context));

            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.VISIBLE);

            return true;
        } else {
            return false;
        }
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
