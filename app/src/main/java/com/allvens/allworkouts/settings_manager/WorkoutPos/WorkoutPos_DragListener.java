package com.allvens.allworkouts.settings_manager.WorkoutPos;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
        this.context             = context;
        this.workout_basicsPrefs = workout_basicsPrefs;
        this.touchListener       = touchListener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        View draggedView = (View) event.getLocalState();
        ViewGroup owner = (ViewGroup) draggedView.getParent();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Start drag animation on the dragged view
                draggedView.animate().alpha(0.5f).scaleX(0.95f).scaleY(0.95f).setDuration(150);
                break;
                
            case DragEvent.ACTION_DRAG_ENTERED:
                // Highlight the drop target with a subtle background
                v.animate().alpha(0.7f).setDuration(100);
                // Use the modern elevated background color for better visibility
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.focusAccent));
                update_View(owner, owner.indexOfChild(v));
                break;
                
            case DragEvent.ACTION_DRAG_EXITED:
                // Remove highlight from drop target
                v.animate().alpha(1.0f).setDuration(100);
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.background_elevated));
                break;
                
            case DragEvent.ACTION_DROP:
                // Provide feedback that drop was successful
                v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100)
                  .withEndAction(() -> v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100));
                break;
                
            case DragEvent.ACTION_DRAG_ENDED:
                // Restore all views to normal state
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.background_elevated));
                v.setAlpha(1.0f);
                
                // Restore the dragged view to normal state
                draggedView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200);
                
                // Update preferences with new order
                workout_basicsPrefs.update_WorkoutsWithViews(owner);
                break;
                
            default:
                break;
        }

        return true;
    }

    /**
     * Moves View To New View Position with animation
     * @param owner
     * @param index
     */
    private void update_View(ViewGroup owner, int index){
        View draggedView = touchListener.getView();
        if (draggedView != null && draggedView.getParent() == owner) {
            owner.removeView(draggedView);
            owner.addView(draggedView, index);
            
            // Add a subtle animation to show the move
            draggedView.setAlpha(0.8f);
            draggedView.animate().alpha(1.0f).setDuration(150);
        }
    }
}