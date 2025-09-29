package com.allvens.allworkouts.ui;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.R;

/**
 * Manages all UI operations for MainActivity including:
 * - View binding and initialization
 * - Workout chooser animations and state
 * - Dynamic button creation and styling
 * - UI state updates and visual feedback
 */
public class MainActivityUIManager {

    private final Context context;
    
    // Core UI components
    private LinearLayout changeWorkoutButton;
    private TextView workoutSelectorText;
    private ImageView workoutSelectorArrow;
    private LinearLayoutCompat workoutChooser;
    
    // UI state
    private boolean chooserIsOpen = false;
    
    // Callback interface for UI events
    public interface UIEventListener {
        void onWorkoutSelected(String workoutName);
        void onChooserToggleRequested();
    }
    
    private UIEventListener eventListener;

    public MainActivityUIManager(Context context) {
        this.context = context;
    }
    
    /**
     * Initialize and bind all views
     */
    public void bindViews(View rootView) {
        changeWorkoutButton = rootView.findViewById(R.id.btn_ChangeWorkouts);
        workoutSelectorText = rootView.findViewById(R.id.tv_workout_selector_text);
        workoutSelectorArrow = rootView.findViewById(R.id.iv_workout_selector_arrow);
        workoutChooser = rootView.findViewById(R.id.ll_home_WorkoutChooser);
        
        // Set up click listener for workout chooser toggle
        changeWorkoutButton.setOnClickListener(this::onChangeWorkoutClicked);
    }
    
    /**
     * Set the event listener for UI interactions
     */
    public void setEventListener(UIEventListener listener) {
        this.eventListener = listener;
    }
    
    /**
     * Update the displayed workout name
     */
    public void updateSelectedWorkout(String workoutName) {
        if (workoutSelectorText != null && workoutName != null) {
            workoutSelectorText.setText(workoutName);
            // Ensure arrow shows collapsed state when workout is updated
            workoutSelectorArrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
        }
    }
    
    /**
     * Toggle the workout chooser open/closed state
     */
    public void toggleWorkoutChooser(String[] availableWorkouts) {
        if (chooserIsOpen) {
            closeWorkoutChooser();
        } else {
            openWorkoutChooser(availableWorkouts);
        }
        
        chooserIsOpen = !chooserIsOpen;
    }
    
    /**
     * Force close the workout chooser (used when navigating away)
     */
    public void closeWorkoutChooser() {
        if (!chooserIsOpen) return;
        
        // Update arrow to collapsed state
        workoutSelectorArrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
        
        // Animate chooser closed
        workoutChooser.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction(() -> {
                workoutChooser.setVisibility(View.GONE);
                workoutChooser.removeAllViews();
            })
            .start();
            
        chooserIsOpen = false;
    }
    
    /**
     * Get current chooser state
     */
    public boolean isChooserOpen() {
        return chooserIsOpen;
    }
    
    // Private helper methods
    
    private void onChangeWorkoutClicked(View view) {
        // Notify parent that chooser toggle was requested
        if (eventListener != null) {
            eventListener.onChooserToggleRequested();
        }
    }
    
    private void openWorkoutChooser(String[] workouts) {
        // Update arrow to expanded state
        workoutSelectorArrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
        
        // Show chooser with animation
        workoutChooser.setVisibility(View.VISIBLE);
        workoutChooser.setAlpha(0f);
        workoutChooser.animate()
            .alpha(1f)
            .setDuration(200)
            .start();
        
        // Create buttons for each workout
        for (String workoutName : workouts) {
            Button button = createWorkoutButton(workoutName);
            button.setOnClickListener(v -> {
                // Notify listener of workout selection
                if (eventListener != null) {
                    eventListener.onWorkoutSelected(workoutName);
                }
                
                // Add haptic feedback
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                }
                
                // Close chooser after selection
                closeWorkoutChooser();
            });
            
            workoutChooser.addView(button);
        }
    }
    
    private Button createWorkoutButton(String name) {
        Button button = new Button(context);
        button.setText(name);
        
        // Apply styling
        styleChooserButton(button);
        
        // Set layout parameters
        LinearLayoutCompat.LayoutParams layoutParams = 
                new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = context.getResources().getDimensionPixelSize(R.dimen.spacing_2);
        button.setLayoutParams(layoutParams);
        
        // Accessibility
        button.setContentDescription(
                context.getString(R.string.select_workout_type, name));
        
        return button;
    }
    
    private void styleChooserButton(Button button) {
        // Apply all style attributes programmatically for dynamic buttons
        button.setTextColor(context.getResources().getColor(R.color.selectedButton));
        button.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 
                context.getResources().getDimensionPixelSize(R.dimen.text_size_body));
        button.setTypeface(button.getTypeface(), android.graphics.Typeface.BOLD);
        button.setAllCaps(false);
        
        // Set background drawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setBackground(context.getDrawable(R.drawable.bg_chooser_item));
        } else {
            button.setBackground(context.getResources().getDrawable(R.drawable.bg_chooser_item));
        }
        
        // Set padding
        int paddingVertical = context.getResources().getDimensionPixelSize(R.dimen.spacing_3);
        int paddingHorizontal = context.getResources().getDimensionPixelSize(R.dimen.spacing_2);
        button.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        
        // Set proper touch target size
        int minTouchSize = context.getResources().getDimensionPixelSize(R.dimen.touch_target_min);
        button.setMinHeight(minTouchSize);
        
        // Remove default button appearance
        button.setStateListAnimator(null);
    }
}