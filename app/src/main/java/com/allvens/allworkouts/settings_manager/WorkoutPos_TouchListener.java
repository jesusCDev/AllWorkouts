package com.allvens.allworkouts.settings_manager;

import android.content.ClipData;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

class WorkoutPos_TouchListener implements View.OnTouchListener {

    private View view;
    private int indexOfView;
    private Context context;

    public WorkoutPos_TouchListener(Context context){
        this.context = context;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            this.view = view;
            indexOfView = ((ViewGroup)view.getParent()).indexOfChild(view);

//            view.startDrag
//
//            // todo might be able to change this
            view.setVisibility(View.INVISIBLE);
            ClipData data = ClipData.newPlainText("", "");

//            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
//                    view);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    new ConstraintLayout(context));

            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.VISIBLE);

            return true;
        } else {
            return false;
        }
    }

    private void pop(String message){
        Log.d("Bug", message);
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getIndexOfView() {
        return indexOfView;
    }

    public void setIndexOfView(int indexOfView) {
        this.indexOfView = indexOfView;
    }
}
