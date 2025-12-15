# Media Controls Enhancement Guide

## New Features Added ✨

### 1. Track Info Display 🎵
The media widget now shows **what's currently playing** from Poweramp (or any music app).

**What you'll see:**
- Song Title • Artist Name
- Example: "Eye of the Tiger • Survivor"
- Falls back to "♪ Playing" if track info unavailable

**Setup Required:**
To enable track info display, you need to grant notification access permission **one time**:

1. Open **Android Settings**
2. Go to **Apps** → **All Apps** → **AllWorkouts**
3. Select **Notifications** or **Special app access**
4. Find **Notification access**
5. Toggle **ON** for AllWorkouts

**Why this permission?**
Android requires this special permission for apps to read what music is currently playing. The app only reads track info - nothing else!

**Note:** If you don't grant this permission, the media controls still work perfectly - you just won't see the song name.

---

### 2. Skip Workout Gesture 💪

Now you can **skip to the next workout set** using a long-press gesture - perfect when your arms are tired after arm day!

**How to use:**

1. Press and **hold anywhere on the screen** (workout title, background, etc.) - **but NOT on buttons**
2. Hold for about **1 second**
3. A countdown overlay appears (3...2...1...)
4. **Release your finger** to cancel, or **keep holding** through the countdown to skip

**Important:**
- ❌ Long-pressing on buttons won't trigger the skip (they'll just act normally)
- ✅ Long-press on the workout title, image area, or background
- ✅ If you change your mind, just release before the countdown finishes

**Features:**
- ✅ Haptic feedback when long-press is detected
- ✅ 3-second countdown with visual overlay
- ✅ Release finger to cancel anytime
- ✅ Works on both workout and timer screens
- ✅ Cannot skip past the final (5th) set
- ✅ Doesn't interfere with button clicks

**Perfect for:**
- When you need to move on quickly
- After tough arm days when precise tapping is hard
- When you want to adjust your workout pace
- No accidental double-taps on buttons!

---

## Testing Checklist

### Track Info Display
- [ ] Install the updated app
- [ ] Grant notification access permission (see setup steps above)
- [ ] Open Poweramp and start playing music
- [ ] Start a workout session
- [ ] Check if the media widget shows your song name

### Skip Gesture
- [ ] Start a workout session
- [ ] Try long-pressing on the workout title (hold for 1 second)
- [ ] Observe the countdown overlay
- [ ] Release your finger early to cancel
- [ ] Try again and hold through the full countdown to skip to the next set
- [ ] Verify you get haptic feedback when long-press detected
- [ ] Verify buttons still work normally (don't trigger skip)

---

## Technical Details

**New Files Created:**
- `MediaSessionTracker.java` - Tracks current media session info
- `MediaNotificationListenerService.java` - Notification listener service
- `SkipWorkoutGestureHandler.java` - Handles triple-tap and long-press
- `skip_workout_countdown_overlay.xml` - Countdown overlay UI

**Modified Files:**
- `AndroidManifest.xml` - Added notification listener service
- `WorkoutMediaController.java` - Uses tracked session data
- `WorkoutSessionController.java` - Added skipToNextSet() method
- `WorkoutSessionActivityUIManager.java` - Manages countdown overlay
- `WorkoutSessionActivity.java` - Wires up gesture handler
- `activity_workout_session.xml` - Includes overlay layout

---

## Troubleshooting

**Track info not showing?**
1. Make sure notification access is enabled (Settings → Apps → AllWorkouts → Notification access)
2. Check that music is actually playing in Poweramp
3. Try pausing and playing the music again
4. Restart the workout session

**Skip gesture not working?**
1. Make sure you're pressing on a non-button area (title, background, etc.)
2. Hold for at least 1 second
3. Keep holding through the countdown - releasing early cancels the skip
4. Check device logs for "SkipGestureHandler" messages

**App crashes?**
1. Check logcat for error messages
2. Report any issues with the stack trace

---

## Build & Deploy

```bash
# Build debug APK
./gradlew assembleDebug

# Install to device
./gradlew installDebug

# Build release APK
./gradlew assembleRelease
```

Enjoy your enhanced workout experience! 🏋️‍♂️🎵
