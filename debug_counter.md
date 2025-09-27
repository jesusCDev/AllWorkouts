# Debug Counter Utility

## Testing Counter Persistence

To test if the counter is working properly, you can use ADB commands:

### View current counter state:
```bash
adb shell run-as com.allvens.allworkouts cat /data/data/com.allvens.allworkouts/shared_prefs/workout_cycle_counter.xml
```

### View session state:
```bash
adb shell run-as com.allvens.allworkouts cat /data/data/com.allvens.allworkouts/shared_prefs/workout_session_prefs.xml
```

### Clear counter for testing:
```bash
adb shell run-as com.allvens.allworkouts rm /data/data/com.allvens.allworkouts/shared_prefs/workout_cycle_counter.xml
```

### Clear session for testing:
```bash
adb shell run-as com.allvens.allworkouts rm /data/data/com.allvens.allworkouts/shared_prefs/workout_session_prefs.xml
```

## Expected Flow:
1. Start workout session → counter initialized to 0
2. Complete first workout → counter = 1, progress shows "1 of X"
3. Complete second workout → counter = 2, progress shows "2 of X"
4. Continue until counter = total workouts → session complete

## Common Issues:
- Counter being cleared in `SessionUtils.clearSession()` (fixed)
- Counter not incrementing for real workouts
- Session start workout not being preserved
- Progress calculation using wrong values

## Debug Logs to Watch:
- `[DEBUG] onCreate: isRealWorkout=...`
- `[CycleTracker] Workout completed! X -> Y`
- `[DEBUG] updateProgressIndicator: rawCount=...`
- `[CycleLogic] Current: ... Completed: X/Y`
- `[DEBUG] Session state (...): completedCount=...`