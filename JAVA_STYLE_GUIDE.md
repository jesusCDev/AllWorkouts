# AllWorkouts Java Style Guide

## Overview
This guide establishes consistent naming conventions and coding standards for the AllWorkouts Android project, following modern Java best practices and Android conventions.

## 1. Naming Conventions

### Classes & Interfaces
- **Use PascalCase** (no underscores)
- **Be descriptive and specific**

```java
// ❌ Bad
WorkoutBasicsPrefs_Checker
Settings_Manager
WorkoutPos_TouchListener

// ✅ Good  
WorkoutPreferencesChecker
SettingsManager
WorkoutPositionTouchListener
```

### Methods
- **Use camelCase** (no underscores)
- **Use verbs that describe the action**
- **Boolean methods should start with `is`, `has`, `can`, etc.**

```java
// ❌ Bad
get_WorkoutInfo()
update_WorkoutStatusPref()
get_TurnOnStatus()

// ✅ Good
getWorkoutInfo()
updateWorkoutStatusPreference()
isEnabled() // instead of get_TurnOnStatus
```

### Variables & Fields
- **Use camelCase** (no underscores)
- **Be descriptive, avoid abbreviations**
- **Constants should be UPPER_SNAKE_CASE**

```java
// ❌ Bad
private WorkoutInfo workout_info;
private WorkoutPosAndStatus[] turnOffOn_workouts;
WorkoutPosAndStatus[] pos;

// ✅ Good
private WorkoutInfo workoutInfo;
private WorkoutPosAndStatus[] enabledWorkouts;
WorkoutPosAndStatus[] workoutPositions;
```

### Constants
- **Use UPPER_SNAKE_CASE**
- **Group related constants in interfaces or enum classes**

```java
// ✅ Good
public static final String PULL_UPS = "Pull Ups";
public static final int DEFAULT_WORKOUT_COUNT = 5;
```

### Package Names
- **Use lowercase, separated by dots**
- **Use meaningful, hierarchical names**

```java
// ✅ Current structure is good
com.allvens.allworkouts.data_manager
com.allvens.allworkouts.workout_session_manager
```

## 2. Method Structure

### Getters and Setters
```java
// ❌ Bad naming
public WorkoutInfo get_WorkoutInfo() { ... }
public void set_TurnOnStatus(boolean status) { ... }
public boolean get_TurnOnStatus() { ... }

// ✅ Good naming
public WorkoutInfo getWorkoutInfo() { ... }
public void setEnabled(boolean enabled) { ... }
public boolean isEnabled() { ... }
```

### Method Parameters
```java
// ❌ Bad
public void update_WorkoutStatusPref(String prefKey, boolean value)
public WorkoutPosAndStatus get_WorkoutData(String workoutName, String posPrefKey, String statusPrefKey, int resourceID)

// ✅ Good
public void updateWorkoutStatusPreference(String preferenceKey, boolean isEnabled)
public WorkoutPosAndStatus createWorkoutData(String workoutName, String positionKey, String statusKey, int resourceId)
```

## 3. Class Organization

### Field Declaration Order
1. Constants (static final)
2. Static fields
3. Instance fields
4. Constructors  
5. Methods (public first, then private)

### Method Naming by Responsibility
- **Data access**: `get*()`, `find*()`, `load*()`
- **Data modification**: `set*()`, `update*()`, `save*()`, `create*()`
- **Boolean queries**: `is*()`, `has*()`, `can*()`, `should*()`
- **Actions**: `start*()`, `stop*()`, `initialize*()`, `process*()`

## 4. Android-Specific Conventions

### View References
```java
// ❌ Current mixed style
private TextView           workoutSelectorText;
private ImageView          workoutSelectorArrow;

// ✅ Consistent style
private TextView workoutSelectorText;
private ImageView workoutSelectorArrow;
private LinearLayout changeWorkoutButton;
```

### Resource IDs
- Keep existing Android resource ID conventions (snake_case)
- These are generated and should not be changed

## 5. Refactoring Priority

### Phase 1: Core Classes (High Impact)
1. `WorkoutBasicsPrefs_Checker` → `WorkoutPreferencesManager`
2. `Settings_Manager` → `SettingsManager`
3. `WorkoutGenerator` methods: `get_Workout()` → `getWorkout()`

### Phase 2: Data Classes
1. `WorkoutPosAndStatus` methods: `get_TurnOnStatus()` → `isEnabled()`
2. Preference key naming consistency

### Phase 3: UI and Session Management
1. Activity method consistency
2. UI manager class naming

## 6. Refactoring Strategy

### Backward Compatibility
- Refactor incrementally to avoid breaking changes
- Test after each major class refactoring
- Keep commit history clean with meaningful messages

### Testing After Refactoring
```bash
# After each phase, run:
./gradlew compileDebugSources  # Check compilation
./gradlew testDebugUnitTest    # Run unit tests
./gradlew assembleDebug        # Full build test
```

## 7. Examples of Refactored Code

### Before (Current Style)
```java
public class WorkoutBasicsPrefs_Checker {
    private WorkoutPosAndStatus[] turnOffOn_workouts;
    
    public WorkoutPosAndStatus[] get_WorkoutsPos(boolean includeOffStatus) {
        // Implementation
    }
    
    private WorkoutPosAndStatus get_WorkoutData(String workoutName, String posPrefKey, String statusPrefKey, int resourceID) {
        // Implementation  
    }
}
```

### After (Refactored Style)
```java
public class WorkoutPreferencesManager {
    private WorkoutPosAndStatus[] allWorkouts;
    
    public WorkoutPosAndStatus[] getWorkoutPositions(boolean includeDisabled) {
        // Implementation
    }
    
    private WorkoutPosAndStatus createWorkoutData(String workoutName, String positionKey, String statusKey, int resourceId) {
        // Implementation
    }
}
```

This refactoring will make the codebase more maintainable, easier to understand, and aligned with modern Java/Android development practices.