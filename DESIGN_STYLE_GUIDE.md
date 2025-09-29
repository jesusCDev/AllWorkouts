# AllWorkouts App Design System & Style Guide
**Modern Comic-Inspired Edition**

## 🎨 **Overview**
AllWorkouts now follows a **modern comic-inspired design system** that perfectly complements the custom character artwork while maintaining excellent readability and accessibility. The neutral, low-chroma surfaces let the vivid character illustrations shine while keeping the UI clean and professional.

---

## 🎯 **Design Philosophy**
- **Comic-clean hybrid** that makes character art feel at home
- **Neutral surfaces, vivid accents** from the artwork (crimson, teal, gold)
- **Dark-first design** for reduced eye strain during workouts  
- **High contrast** with modern stroke/border system
- **Workout-specific colors** for visual organization
- **Clean typography** with subtle comic accents
- **Modern Material Design** with comic panel inspiration

---

## 🌈 **Modern Comic-Inspired Color Palette**

### **Dark Theme (Primary)**
```xml
<!-- Surface Colors -->
<color name="surface_ink">#0F1216</color>           <!-- Highest contrast -->
<color name="surface">#0B1118</color>               <!-- Main surface -->
<color name="surface_variant">#121A23</color>       <!-- Elevated surface -->

<!-- Primary (Crimson) - Sit-ups, Squats -->
<color name="primary">#D2423A</color>               <!-- Crimson red -->
<color name="on_primary">#FFFFFF</color>            <!-- White on crimson -->
<color name="primary_container">#571C1A</color>     <!-- Dark crimson container -->

<!-- Secondary (Teal) - Push-ups -->
<color name="secondary">#2F7C8A</color>             <!-- Teal blue -->
<color name="on_secondary">#FFFFFF</color>          <!-- White on teal -->
<color name="secondary_container">#103842</color>   <!-- Dark teal container -->

<!-- Tertiary (Gold) - Pull-ups -->
<color name="tertiary">#E9B84A</color>              <!-- Gold accent -->
<color name="on_tertiary">#2A2008</color>           <!-- Dark on gold -->

<!-- Status Colors -->
<color name="success">#3BB273</color>               <!-- Success green -->
<color name="warning">#E4A11B</color>               <!-- Warning amber -->
<color name="error">#E64A4A</color>                 <!-- Error red -->

<!-- Outline & Stroke -->
<color name="outline">#577C8DA3</color>             <!-- 34% alpha on dark -->
```

### **Workout-Specific Color Mapping**
- **Sit-ups**: Primary (Crimson) – `#D2423A`
- **Push-ups**: Secondary (Teal) – `#2F7C8A` 
- **Pull-ups**: Tertiary (Gold) – `#E9B84A`
- **Squats**: Primary + Gold accents

### **Color Usage Guidelines**
- **#0B1118**: Main app background, primary surface
- **#121A23**: Cards, elevated surfaces, dialogs
- **#D2423A**: Primary buttons, sit-ups/squats accents
- **#2F7C8A**: Push-ups accents, secondary buttons
- **#E9B84A**: Pull-ups accents, tertiary elements
- **#FFFFFF**: Primary text, button text
- **#8B93A3**: Secondary text, placeholders
- **#577C8DA3**: Borders, strokes, dividers (34% alpha)

---

## 📏 **Spacing System**

### **Spacing Scale** (8dp grid-based)
```xml
<dimen name="spacing_half">2dp</dimen>     <!-- Micro adjustments -->
<dimen name="spacing_1">4dp</dimen>        <!-- Tight spacing -->
<dimen name="spacing_2">8dp</dimen>        <!-- Small spacing -->
<dimen name="spacing_3">16dp</dimen>       <!-- Standard spacing -->
<dimen name="spacing_4">24dp</dimen>       <!-- Large spacing -->
<dimen name="spacing_5">48dp</dimen>       <!-- XL spacing -->
```

### **Usage Guidelines**
- **spacing_1 (4dp)**: Internal padding, line spacing
- **spacing_2 (8dp)**: Between related elements, chip margins  
- **spacing_3 (16dp)**: Standard card padding, between sections
- **spacing_4 (24dp)**: Screen margins, major section separation
- **spacing_5 (48dp)**: Large gaps, dramatic spacing

---

## 🔤 **Typography System**

### **Font Family**
- **Primary**: `Archivo Black` (Google Fonts) - Bold, high-impact headings
- **Fallback**: System default for body text

### **Type Scale**
```xml
<dimen name="text_size_display">32sp</dimen>     <!-- Hero titles -->
<dimen name="text_size_headline">24sp</dimen>    <!-- Screen titles -->
<dimen name="text_size_title">20sp</dimen>       <!-- Section headers -->
<dimen name="text_size_body">16sp</dimen>        <!-- Standard body text -->
<dimen name="text_size_caption">14sp</dimen>     <!-- Small labels -->
<dimen name="text_size_small">12sp</dimen>       <!-- Fine print -->
```

### **Typography Usage**
- **Display (32sp)**: Workout rep numbers, timer displays
- **Headline (24sp)**: Screen titles, workout names
- **Title (20sp)**: Section headers, important labels
- **Body (16sp)**: Standard text, descriptions, buttons
- **Caption (14sp)**: Small labels, metadata, calendar days
- **Small (12sp)**: Fine print, disclaimers

---

## 🏷️ **Component Library**

### **Cards**
```xml
<!-- Standard elevated card -->
<CardView
    app:cardBackgroundColor="@color/background_elevated"
    app:cardCornerRadius="@dimen/corner_radius_large"    <!-- 16dp -->
    app:cardElevation="@dimen/elevation_card"            <!-- 4dp -->
    android:layout_margin="@dimen/spacing_3" />         <!-- 16dp -->
```

**Card Specifications:**
- **Background**: `#20252E` (background_elevated)
- **Corner Radius**: 16dp (large), 12dp (standard), 8dp (small)
- **Elevation**: 4dp for standard cards
- **Margin**: 16dp from screen edges
- **Padding**: 16dp internal content padding

### **Buttons**

#### **Primary Button** (Call-to-Action)
```xml
<Button
    style="@style/Widget.App.Button.Primary.Large"
    android:background="@drawable/bg_button_primary"
    android:textColor="@color/background_dark"          <!-- Dark text on orange -->
    android:elevation="@dimen/elevation_card" />        <!-- 4dp -->
```
- **Background**: Orange gradient with ripple effect
- **Text**: Dark (#1B1F27) for contrast
- **Height**: 56dp for large, 48dp for standard
- **Corner Radius**: 12dp

#### **Secondary Button** (Outline style)  
```xml
<Button
    style="@style/Widget.App.Button.Secondary"
    android:textColor="@color/colorAccent" />           <!-- Orange text -->
```
- **Background**: Transparent with orange border
- **Text**: Orange (#FF7844)
- **Height**: 48dp

### **Interactive Elements**

#### **Chips** (Value displays)
```xml
<!-- Used for workout rep numbers -->
<TextView
    android:background="@drawable/bg_chip"
    android:textColor="@color/selectedButton"
    android:padding="@dimen/spacing_2" />               <!-- 8dp -->
```
- **Background**: Elevated dark with subtle border
- **Border**: 1dp gray (#46566E)
- **Corner Radius**: 12dp
- **Text**: White, bold

#### **Ripple Effects**
```xml
<ripple android:color="@color/colorAccentLess">         <!-- 56% orange -->
```
- **Touch feedback**: Semi-transparent orange
- **Animation**: Standard Android ripple timing

---

## 📐 **Layout Patterns**

### **Touch Targets**
```xml
<dimen name="touch_target_min">48dp</dimen>            <!-- Minimum accessibility -->
<dimen name="touch_target_large">56dp</dimen>          <!-- Important actions -->
```

### **Screen Margins**
- **Horizontal**: 16dp (spacing_3) from screen edges
- **Vertical**: 16-24dp depending on content density
- **Cards**: Additional 16dp internal padding

### **Elevation Hierarchy**
```xml
<dimen name="elevation_button">2dp</dimen>             <!-- Interactive elements -->
<dimen name="elevation_card">4dp</dimen>               <!-- Content cards -->
<dimen name="elevation_fab">6dp</dimen>                <!-- Floating action buttons -->
```

---

## 🎯 **Android-Specific Guidelines**

### **Material Design Compliance**
- ✅ **Touch targets**: Minimum 48dp for accessibility
- ✅ **Ripple feedback**: On all interactive elements
- ✅ **Elevation shadows**: Consistent z-axis hierarchy
- ✅ **Corner radius**: Rounded corners for modern feel

### **Accessibility (A11Y)**
- **Color contrast**: High contrast ratios for readability
- **Text size**: Scalable with system font size
- **Touch targets**: Large enough for varied dexterity
- **Content descriptions**: Present on interactive elements

### **Dark Theme Best Practices**
- **Avoid pure black**: Uses #1B1F27 for reduced eye strain
- **Elevation contrast**: Lighter colors for elevated surfaces
- **Color temperature**: Warm orange accent reduces blue light
- **Status bar**: Darker variant maintains visual hierarchy

### **Performance Considerations**
- **Vector drawables**: Used for icons (scalable, small)
- **PNG assets**: Only for custom artwork/photos
- **Ripple drawables**: Hardware-accelerated animations
- **CardView**: Optimized shadow rendering

---

## 🎨 **Custom Image Guidelines**

### **Character Illustrations** (Your custom workout images)
- **Background**: Transparent with subtle contact shadow
- **Style**: Clean, modern illustration style
- **Colors**: Can use full color palette - no tinting applied
- **Format**: PNG with transparency
- **Resolution**: 500x500px minimum for sharp display
- **Shadow**: Soft drop shadow beneath character for depth

### **Icon Style**
- **Existing icons**: Vector-based, minimal, consistent stroke weight
- **Color**: Single color (#46566E for inactive, #FF7844 for active)
- **Size**: 24dp standard, scalable

---

## 🔧 **Implementation Notes**

### **Key Drawable Resources**
```
bg_button_primary.xml    - Orange button with ripple
bg_button_secondary.xml  - Outlined button  
bg_chip.xml             - Elevated chip background
bg_chooser_item.xml     - Transparent ripple effect
```

### **Key Styles**
```
Widget.App.Button.Primary.Large    - Main CTA buttons
Widget.App.Button.Secondary         - Secondary actions  
TextAppearance.App.Headline         - Screen titles
TextAppearance.App.Body             - Standard text
```

### **Theme Configuration**
- **Base**: `Theme.AppCompat.DayNight.NoActionBar`
- **Status bar**: Dark theme with custom color
- **Text colors**: Global overrides for consistency
- **Accent tinting**: Applied to interactive elements

---

## 📱 **Screen Density Support**
- **MDPI** (160dpi): 1x baseline
- **HDPI** (240dpi): 1.5x scaling  
- **XHDPI** (320dpi): 2x scaling
- **XXHDPI** (480dpi): 3x scaling
- **XXXHDPI** (640dpi): 4x scaling

**Custom images should provide multiple densities or use vector format when possible.**

---

## ✅ **Component Checklist**

When creating new components, ensure:
- [ ] Uses established color palette
- [ ] Follows 8dp spacing grid
- [ ] Meets 48dp minimum touch targets
- [ ] Includes ripple/press feedback
- [ ] Has proper elevation for hierarchy
- [ ] Uses consistent corner radius (8dp/12dp/16dp)
- [ ] Maintains high contrast ratios
- [ ] Works in dark theme context
- [ ] Uses Archivo Black for headings
- [ ] Follows Material Design motion principles

---

*This style guide ensures consistency across all AllWorkouts app components while maintaining the fitness-focused, accessible design that users expect during their workout sessions.*