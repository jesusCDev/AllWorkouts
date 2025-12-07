# What Changed Visually - Noir Tatami

After rebuilding and reinstalling the app, here's what you should see:

## Color Changes (Most Obvious)

### Before → After
- **Accent color**: Lime green (#D3FF3E) → **Vermilion red** (#FF432C)
- **Text**: Pure white → **Bone** (warm off-white #F2EDE3)
- **Cards**: Blue-gray → **Charcoal ink** (#161A1F)
- **Backgrounds**: Gradient (diagonal blue) → **Vignette** (atmospheric radial fade)

## Typography Changes (Very Noticeable)

### Home Screen
- **"4Basic Workouts" title**: Now uses **Fraunces** (soft serif) instead of Archivo Black
  - Should look more elegant and less aggressive

### Workout Session
- **Timer numbers** (30, 29, 28...): Now uses **Teko** (athletic condensed)
  - Taller, more condensed, gym-poster feel
  - Should be very obvious during rest timer
- **Rep numbers** (15, 12, 10...): Also **Teko**
  - Same distinctive athletic look

### Max Value Screen
- **Big number** (showing your max): Now **Teko**
  - Should immediately look different - taller, tighter spacing

### All Screens
- **Body text**: Now **Atkinson Hyperlegible**
  - More readable, distinctive letterforms
- **Card titles**: **Fraunces** (Settings, Workout Log, etc.)

## Component Changes

### Chips (Set value boxes)
- Before: Solid fill, no border
- After: **Stroke only** with bone outline
  - Editorial/magazine style
  - More refined look

### Buttons
- **Primary (Start, Complete)**: Now solid **vermilion** instead of lime
- **Tonal buttons**: Now have **2dp stroke** border (ink line color)
  - More defined, editorial feel

### Cards
- All cards now have **subtle 2dp stroke** borders
- Less floating, more grounded/editorial

### Calendar Heatmap
- Color scale now uses **vermilion intensity** instead of lime
- Low activity: faint red → High activity: full vermilion

## Animation
- **Home screen**: Bottom bar elements now **fade in with stagger**
  - Each button appears 50ms after the previous one
  - Subtle upward slide

## What Should Stand Out Immediately

1. **Color shift**: Red accent everywhere (no more lime green)
2. **Huge numbers**: Timer/reps/max values look taller and more condensed (Teko)
3. **Titles**: Softer, more elegant serif font (Fraunces) on "4Basic Workouts", screen headers
4. **Borders**: Stroke outlines on chips and cards
5. **Text warmth**: Bone color feels warmer than pure white

## If You Don't See Changes

1. **Uninstall the old APK completely**:
   ```bash
   adb uninstall com.allvens.allworkouts
   ```

2. **Install fresh build**:
   ```bash
   ./gradlew installDebug
   ```

3. **Force stop and clear cache**:
   - Settings → Apps → AllWorkouts → Force Stop
   - Clear cache (not data, unless you want to reset)

4. **Reboot device/emulator** (last resort)

## Quick Visual Test

Open the app and check:
- [ ] Home title uses a serif font (Fraunces)
- [ ] "Start" button is vermilion/red (not lime)
- [ ] Background is solid dark with subtle vignette (not diagonal gradient)
- [ ] Start a workout → Timer numbers look tall/condensed (Teko)
- [ ] Set value chips have visible stroke borders

If all 5 check out, Noir Tatami is working!

## Fonts You Should Recognize

- **Fraunces**: Soft, slightly old-fashioned serif - used in craft beer labels, boutique branding
- **Teko**: Athletic, condensed - used in sports graphics, gym posters
- **Atkinson Hyperlegible**: Clean, friendly - designed by Braille Institute for accessibility

None of these are "default Android" or generic web fonts. They should feel distinctive.
