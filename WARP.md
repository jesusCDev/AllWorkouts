# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

AllWorkouts is an Android fitness application built with Java that provides structured workout routines for four exercise types: Pull-ups, Push-ups, Sit-ups, and Squats. The app features workout sessions with timers, progress tracking, and workout history logging.

## Architecture

### Core Architecture Pattern
The app follows a manager-based architecture with clear separation between UI, business logic, and data layers:

- **Activity Layer**: `MainActivity`, `WorkoutSessionActivity`, `LogActivity`, `SettingsActivity`
- **Manager Layer**: Session managers, UI managers, data managers for business logic
- **Data Layer**: SQLite database with wrapper classes for workout data and history
- **Assets**: Constants, helpers, and utility classes

### Key Components

#### Session Management
- `WorkoutSession_Manager`: Orchestrates workout sessions, timers, and UI updates
- `WorkoutSession_UI_Manager`: Handles UI state changes during workouts
- `Timer`: Manages rest periods between workout sets

#### Workout System
- `Workout`: Base workout class with difficulty scaling and break time calculations
- `WorkoutGenerator`: Creates workout instances from database data
- Individual workout classes: `PullUps`, `PushUps`, `SitUps`, `Squats`

#### Data Management
- `WorkoutWrapper`: Database abstraction layer for CRUD operations
- `WorkoutInfo`: Domain model for workout data
- `WorkoutHistory_Info`: Domain model for workout session history
- `Workout_SQLiteOpenHelper`: Database schema management

#### Settings & Preferences
- `SettingsPrefsManager`: SharedPreferences abstraction
- `WorkoutPosAndStatus`: Manages workout ordering and enabled status
- `Notification_Manager`: Handles workout notifications

### Database Schema
The app uses SQLite with two main tables:
- `workout_info`: Stores workout types, max values, progress, and types
- `workout_history`: Stores completed workout session data (5 set values + max)

## Common Development Commands

### Building the App
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK  
./gradlew assembleRelease

# Build all variants
./gradlew assemble

# Clean build artifacts
./gradlew clean
```

### Testing
```bash
# Run all unit tests
./gradlew test

# Run debug unit tests only
./gradlew testDebugUnitTest

# Run connected Android tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run specific instrumented test
./gradlew connectedDebugAndroidTest
```

### Code Quality & Analysis
```bash
# Run lint analysis
./gradlew lint

# Run lint and apply safe fixes
./gradlew lintFix

# Update lint baseline
./gradlew updateLintBaseline

# Check for dependency issues
./gradlew checkDebugDuplicateClasses
```

### Installation & Deployment
```bash
# Install debug build to connected device
./gradlew installDebug

# Uninstall from connected device
./gradlew uninstallDebug

# Create signed bundle for release
./gradlew bundleRelease
```

### Development Workflow
```bash
# Generate sources and check for compilation issues
./gradlew compileDebugSources

# Build and run all checks
./gradlew check

# View project dependencies
./gradlew dependencies
```

## Key Development Notes

### Activity Flow
1. `MainActivity` → Select workout → `WorkoutSessionActivity` → Complete → `WorkoutSessionFinishActivity`
2. `MainActivity` → Log → `LogActivity` (view workout history with charts)
3. `MainActivity` → Settings → `SettingsActivity` → Various settings sub-activities

### Workout Session Logic
- Each workout has 5 sets with calculated reps based on user's maximum and difficulty scaling
- Rest timers run between sets with duration based on workout difficulty
- Progress is tracked and stored in the database upon completion
- UI alternates between workout display and timer display

### Database Integration
- All database operations go through `WorkoutWrapper` for consistency
- Database connections must be explicitly opened/closed
- Workout history is linked to workout info via foreign key relationship

### UI State Management
- UI managers handle state transitions and view updates
- Timer state affects UI display mode (workout view vs timer view)
- Settings changes require refreshing workout lists in MainActivity

## File Structure Highlights

```
app/src/main/java/com/allvens/allworkouts/
├── MainActivity.java                    # Main entry point and workout selection
├── WorkoutSessionActivity.java          # Active workout session
├── assets/                             # Constants and utility classes
├── data_manager/                       # Database and preferences management  
├── workout_session_manager/            # Core workout session logic
│   └── workouts/                      # Individual workout implementations
├── log_manager/                        # Workout history and charting
└── settings_manager/                   # App settings and preferences
```

## Legacy Considerations

This project targets Android API 27 and uses older Android Support libraries. When making changes:
- Maintain compatibility with `android.support.*` libraries
- Use `AppCompatActivity` for all activities  
- Follow existing patterns for SharedPreferences usage
- Database schema changes require migration planning