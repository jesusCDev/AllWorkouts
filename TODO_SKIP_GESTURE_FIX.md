# TODO: Fix Skip Gesture Handler

## Issue
The long-press skip gesture works on the first workout but not consistently on subsequent workouts/timer screens.

## Current Implementation
- Gesture handler attached to `workout_session_root` ConstraintLayout
- Long-press detection on non-button areas (1 second hold)
- Should trigger 3-second countdown overlay before skipping to next set

## Symptoms
- ✅ Works on first workout screen
- ❌ Inconsistent on subsequent workout/timer screens
- Possible causes:
  - Touch event handling when views change visibility (workout ↔ timer)
  - View hierarchy changes affecting touch event propagation
  - OnTouchListener might be getting overridden somewhere

## Debugging Steps to Try
1. Add more verbose logging in `SkipWorkoutGestureHandler.attachToView()` to track touch events
2. Check if the OnTouchListener is still attached after screen changes
3. Verify that `workout_session_root` view is not being recreated
4. Consider intercepting touch events at a higher level (Activity.dispatchTouchEvent)
5. Test if the issue is specific to certain screen transitions

## Potential Solutions

### Option 1: Override Activity.dispatchTouchEvent()
Intercept all touch events at the activity level before they're distributed to child views.

```java
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    // Let gesture handler check first
    if (gestureHandler != null && gestureHandler.handleTouchEvent(ev)) {
        return true;
    }
    return super.dispatchTouchEvent(ev);
}
```

### Option 2: Re-attach gesture handler on screen changes
Hook into screen change methods and re-attach the gesture handler after each transition.

### Option 3: Use a dedicated gesture overlay view
Create a transparent FrameLayout overlay that sits above all content specifically for gesture detection.

### Option 4: Investigate view event consumption
Check if child views (especially the timer/workout displays) are consuming touch events and preventing propagation.

## Files Involved
- `SkipWorkoutGestureHandler.java` - Main gesture detection logic
- `WorkoutSessionActivity.java` - Gesture handler setup
- `activity_workout_session.xml` - Layout with `workout_session_root`
- `WorkoutSessionController.java` - Skip logic (working correctly)

## Related Features
- Track info display (working ✅)
- Media controls (working ✅)
- Skip gesture (partial ❌)

## Priority
Medium - Feature works initially but needs consistency fix for better UX

## Notes
- The skip logic itself (`skipToNextSet()`) is working correctly
- The countdown overlay displays properly when triggered
- The issue is purely in the gesture detection reliability
