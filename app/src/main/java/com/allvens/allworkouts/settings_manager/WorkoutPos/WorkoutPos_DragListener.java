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
                // Start drag animation on the dragged view - no transparency or scaling
                // Keep the dragged element fully visible and normal size
                break;
                
            case DragEvent.ACTION_DRAG_ENTERED:
                // Highlight the drop target with darker background - keep bottom item normal
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.surface_ink));
                update_View(owner, owner.indexOfChild(v));
                break;
                
            case DragEvent.ACTION_DRAG_EXITED:
                // Remove highlight from drop target - restore to component color
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.surface_variant));
                break;
                
            case DragEvent.ACTION_DROP:
                // Provide feedback that drop was successful
                v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100)
                  .withEndAction(() -> v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100));
                break;
                
            case DragEvent.ACTION_DRAG_ENDED:
                // Restore all views to normal state - same as component color
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.surface_variant));
                
                // Dragged view stays normal - no restoration needed since we don't change it
                
                // Update preferences with new order
                workout_basicsPrefs.updateWorkoutsWithViews(owner);
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
            
            // Add a subtle animation to show the move (no transparency)
            draggedView.animate().scaleX(1.02f).scaleY(1.02f).setDuration(75)
                .withEndAction(() -> draggedView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(75));
        }
    }
}