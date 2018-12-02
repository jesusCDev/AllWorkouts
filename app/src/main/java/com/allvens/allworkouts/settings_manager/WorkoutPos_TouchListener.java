package com.allvens.allworkouts.settings_manager;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

class TouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            view.setVisibility(View.VISIBLE);
            return false;
        }
    }
}
