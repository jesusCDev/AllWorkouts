# AllWorkouts

A comprehensive Android fitness application for managing multiple workout routines including Calisthenics, Cardio, Abs, Legs, and Back Strengthening exercises.

## Features

- **5 Workout Categories**: Calisthenics, Cardio, Abs, Legs, Back Strengthening
- **Circular Session Management**: Progress through workouts in sequence with automatic cycling
- **Progress Tracking**: Log workout sessions with detailed history and charts
- **Home Screen Widget**: Slim progress bar showing 9-day heat-map (4 past + today + 4 future), current streak, and max-out indicator — matches the in-app calendar intensity colors
- **Daily Notification Reminders**: Configurable per-day-of-week workout reminders with time picker, test notification, and boot persistence
- **Customizable Settings**: Adjust workout positions, enable/disable routines, backup/restore data
- **Clean Architecture**: MVC/MVP pattern with separated UI, data, and business logic layers
- **Dark Theme Support**: Automatic theme detection with custom notification colors

## Requirements

### Development Environment
- **Java Development Kit**: Java 11 or higher (project configured for Java 21)
- **Android Studio**: Latest stable version (2023.1+)
- **Android SDK**: API Level 33 (Android 13)
- **Minimum Android Version**: API Level 21 (Android 5.0)
- **Gradle**: Version 8.13.0 or compatible

### Build Tools
- **Android Gradle Plugin**: 8.13.0
- **Compile SDK Version**: 33
- **Target SDK Version**: 33
- **Build Tools Version**: 33.0.0+

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd AllWorkouts
```

### 2. Java Version Setup
Ensure Java 11+ is installed and configured:

**On Linux (Fedora/Ubuntu):**
```bash
# Check Java version
java -version

# Install OpenJDK 21 if needed (Fedora)
sudo dnf install java-21-openjdk java-21-openjdk-devel

# Or on Ubuntu
sudo apt install openjdk-21-jdk
```

**On macOS:**
```bash
# Using Homebrew
brew install openjdk@21
```

**On Windows:**
- Download and install OpenJDK 21 from [Adoptium](https://adoptium.net/)
- Set JAVA_HOME environment variable

### 3. Android Studio Setup
1. Open Android Studio
2. Install Android SDK API Level 33 via SDK Manager
3. Install required build tools (33.0.0+)
4. Open the project: `File → Open → Select AllWorkouts folder`

### 4. Gradle Configuration
The project uses these key configurations (already set in `gradle.properties`):
```properties
org.gradle.jvmargs=-Xmx2048M
android.useAndroidX=true
android.nonTransitiveRClass=false
android.nonFinalResIds=false
```

### 5. Build and Run
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install and run on connected device
./gradlew installDebug
```

## Project Structure

```
app/src/main/
├── java/com/allvens/allworkouts/
│   ├── base/                 # Base classes for MVC/MVP pattern
│   ├── data_manager/         # Database and data access classes
│   ├── managers/             # Business logic and data managers
│   ├── settings_manager/     # Settings, notifications, preferences
│   ├── ui/                   # UI managers for activities
│   ├── utils/                # Utility classes
│   ├── widget/               # Home screen widget (provider, data helper, midnight receiver)
│   └── *.java               # Activity classes
├── res/
│   ├── layout/               # XML layout files (includes widget_workout.xml)
│   ├── values/               # Colors, strings, styles
│   ├── values-night/         # Dark theme resources
│   ├── drawable/             # Images, icons, widget cell drawables
│   └── xml/                  # Widget metadata, backup rules
└── AndroidManifest.xml       # App configuration
```

## Architecture

The app follows a clean **MVC/MVP architecture** with:

- **Activities**: Slim coordinators handling lifecycle and navigation
- **UI Managers**: Handle all view binding, styling, and user interactions
- **Data Managers**: Manage preferences, database operations, and data persistence
- **Controllers**: Coordinate business logic between UI and data layers
- **Base Classes**: Provide consistent patterns and lifecycle management

## Key Dependencies

- **Android Support Libraries**: AppCompat, RecyclerView, CardView, Design
- **MPAndroidChart**: For workout progress charts and data visualization
- **OutlineTextView**: Enhanced text rendering for workout displays
- **ConstraintLayout**: Modern layout management

## Development Setup

### IDE Configuration
- **Code Style**: Follow existing camelCase for methods/variables, PascalCase for classes
- **Git**: `.idea/` files are ignored to prevent cross-system conflicts
- **Java Version**: Project targets Java 11 with compatibility for Java 21

### Working Across Multiple Systems
This project is configured to work seamlessly across different development machines:

- All IDE-specific files (`.idea/`) are gitignored
- No absolute paths in tracked configuration files
- Gradle wrapper ensures consistent build environment
- README provides complete setup instructions

## Building for Release

1. Update version in `app/build.gradle`:
   ```gradle
   versionCode 3
   versionName "1.4"
   ```

2. Build release APK:
   ```bash
   ./gradlew assembleRelease
   ```

3. Sign APK with your keystore (configure in `build.gradle` if needed)

## Contributing

1. Follow the existing code style and architecture patterns
2. Test thoroughly on multiple devices and API levels
3. Update documentation for any new features or changes
4. Ensure all lint warnings are addressed

## License

[Add your license information here]

## Version History

- **v1.4**: Home screen widget with 9-day heat-map, daily notification reminders with per-day scheduling
- **v1.3**: Added Back Strengthening workout, improved MVC architecture
- **v1.2**: Enhanced UI/UX, dark theme support
- **v1.1**: Circular workout sessions, progress tracking
- **v1.0**: Initial release with 4 workout categories
