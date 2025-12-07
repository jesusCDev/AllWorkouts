# Noir Tatami Redesign

A distinctive redesign of AllWorkouts inspired by Japanese modernism and vintage gym aesthetics, moving away from generic "AI slop" design patterns.

## Design Philosophy

**Noir Tatami** combines charcoal ink surfaces with warm bone text, bold vermilion accents, and restrained jade for success states. The aesthetic avoids:
- Generic lime/purple gradients on dark backgrounds
- Overused fonts (Inter, Roboto, Space Grotesk)
- Glossy glassmorphism effects
- Scattered micro-interactions

Instead, it embraces:
- Confident, focused color palette
- Distinctive typography (Fraunces, Teko, Atkinson Hyperlegible)
- Atmospheric backgrounds with vignette + subtle texture
- Meaningful, cohesive motion design

## Color Palette

### Ink Surfaces (Charcoal base)
- `ink_950` #0A0B0D - Deepest ink
- `ink_900` #0F1215 - Main surface
- `ink_850` #161A1F - Elevated cards
- `ink_line` #343841 - Stroke & borders

### Bone Text (Warm off-white)
- `bone_100` #F2EDE3 - Primary text
- `bone_70` #C9C3B8 - Secondary text
- `bone_40` #66F2EDE3 - Tertiary text (40% alpha)

### Vermilion Accent (High-energy primary)
- `vermilion` #FF432C - Primary CTA
- `vermilion_60` #99FF432C - 60% alpha
- `vermilion_30` #4DFF432C - 30% alpha
- `vermilion_20` #33FF432C - Ripple effects
- `on_vermilion` #0F1215 - Dark text on vermilion

### Jade Accent (Success)
- `jade` #21C48E - Success state
- `jade_20` #3321C48E - Jade ripple

## Typography

### Display & Headlines
**Fraunces** (SemiBold, 600)
- Display: 32sp
- Headline 1: 28sp
- Headline 2: 24sp
- Used for: App title, page headers, card titles, celebration text

### Numerals (Timer/Reps)
**Teko** (Bold, 700)
- Athletic, condensed, bold
- Timer: 200sp
- Rep numbers: 180sp/160sp
- Primary buttons: 18-24sp
- Used for: All jumbo numbers, energetic CTAs

### Body Text
**Atkinson Hyperlegible** (Regular 400 / Bold 700)
- Body: 16sp
- Body Large: 18sp
- Caption: 14sp
- Used for: All readable content, secondary buttons, labels

## Key Components

### Backgrounds
- **App background**: `bg_app_noir_tatami.xml` - Radial vignette over ink base for atmosphere
- **Calendar**: `bg_calendar_noir.xml` - Ink surface with subtle stroke and inner highlight
- **Cards**: `bg_card_noir.xml` - Editorial style with 2dp ink line stroke

### Buttons
- **Primary**: `bg_button_primary_noir.xml` - Solid vermilion with 16dp radius
- **Tonal**: `bg_button_tonal_noir.xml` - Ink surface with 2dp stroke, editorial style

### Chips
- **Set values**: `bg_chip_noir.xml` - Stroke-only style with bone outline for editorial feel

### Heat Map
Vermilion intensity scale:
- Empty: `ink_850`
- Low: 25% vermilion
- Medium: 50% vermilion
- High: 75% vermilion
- Max: Full vermilion

## Motion Design

### Page Load (Home Screen)
Staggered fade-in + translate animation on bottom bar elements:
- 300ms duration
- 15% delay between items
- Fast-out-slow-in interpolator
- Subtle 8dp vertical rise

### Button Interactions
- Ripple effects use vermilion_20 (20% alpha)
- Elevation animator on primary buttons (2-4dp lift)
- Scale preserved at 1.0 (no shrinking effects)

## File Structure

### New Files Created
```
app/src/main/res/
├── font/
│   ├── fraunces_semibold.ttf
│   ├── teko_bold.ttf
│   ├── atkinson_hyperlegible_regular.ttf
│   ├── atkinson_hyperlegible_bold.ttf
│   ├── fraunces.xml
│   ├── teko.xml
│   └── atkinson_hyperlegible.xml
├── drawable/
│   ├── bg_app_noir_tatami.xml
│   ├── bg_button_primary_noir.xml
│   ├── bg_button_tonal_noir.xml
│   ├── bg_chip_noir.xml
│   ├── bg_calendar_noir.xml
│   └── bg_card_noir.xml
└── anim/
    ├── home_stagger_fade_in.xml
    └── layout_stagger_home.xml
```

### Modified Files
- `values/colors.xml` - Complete Noir Tatami palette
- `values/styles.xml` - Typography and button style updates
- `values-night/colors.xml` - Night theme mappings
- `layout/activity_home.xml` - Noir backgrounds and colors
- `layout/activity_workout_session.xml` - Teko numerals, noir chips
- `layout/activity_log.xml` - Card backgrounds, Fraunces headers
- `layout/activity_workout_finish.xml` - Noir styling

## Legacy Compatibility

All legacy color references are preserved via mappings:
- `accent_primary` → `vermilion`
- `colorAccent` → `vermilion`
- `text_primary` → `bone_100`
- `text_secondary` → `bone_70`
- `surface_*` → `ink_*`

This ensures existing Java code continues to work without modification.

## Build Status

✅ Compiles successfully on Gradle 8.13
✅ All resources validated
✅ No breaking changes to Java codebase
✅ Backward compatible with API 27

## Design Rationale

### Why Vermilion over Lime?
Vermilion (#FF432C) provides:
- Stronger brand differentiation
- Athletic, energetic connotation
- Better contrast against charcoal
- Avoids the "gamer neon" trope

### Why These Fonts?
- **Fraunces**: Soft, humanist serif with character - not corporate
- **Teko**: Athletic condensed sans - gym aesthetic without being aggressive
- **Atkinson Hyperlegible**: Designed for accessibility, distinctively friendly

### Why "Tatami"?
The name references Japanese training mat aesthetics: minimalist, functional, textured surfaces with strong contrast. Combined with "Noir" to emphasize the dark, confident palette.

## Future Enhancements (Optional)

If you want to push the design further:
1. Add subtle texture overlay (2-4% noise PNG) to `bg_app_noir_tatami.xml`
2. Implement 45° hatch pattern at 6% opacity for tatami texture
3. Add micro-interaction on Complete button (scale + haptic)
4. Explore light theme variant: "Gym Field Manual" (parchment + forest green)
5. Add progress celebration animation with vermilion burst

---

**Design System Version**: 1.0  
**Date**: December 2025  
**Status**: Production Ready
