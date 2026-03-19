# FreeStream Music App

A complete Android music streaming application that aggregates free, Creative Commons licensed music from 4 sources:
- **Jamendo** (Primary) - 400,000+ modern tracks from independent artists
- **Freesound** - Sound effects and samples
- **Internet Archive** - Live concerts and vintage recordings
- **ccMixter** - Remixes, acapellas, and samples

## Features

- Search across all 4 music sources simultaneously
- Trending tracks from Jamendo
- Create and manage playlists
- Favorite tracks for offline access
- Playback history
- Full media controls with background playback
- Spotify-inspired dark UI theme
- Material 3 design with Jetpack Compose

## Development Environment

This project is designed to run in **GitHub Codespaces** with zero local installation.

### Prerequisites

1. **GitHub Account** (Free tier includes 60 hours/month of Codespaces)
2. **Jamendo Client ID** (Free registration)
3. **Freesound API Key** (Free registration)

### Getting Your API Keys

#### Jamendo Client ID
1. Go to https://devportal.jamendo.com/
2. Create a free account
3. Register a new application
4. Copy your Client ID

#### Freesound API Key
1. Go to https://freesound.org/apiv2/apply/
2. Create a free account
3. Apply for API access
4. Copy your API Key

## Setup Instructions

### 1. Create GitHub Repository

1. Go to https://github.com/new
2. Name your repository (e.g., `freestream-music-app`)
3. Make it Public or Private
4. Check "Add a README file"
5. Click "Create repository"

### 2. Open in Codespaces

1. In your repository, click the green **"Code"** button
2. Select the **"Codespaces"** tab
3. Click **"Create codespace on main"**
4. Wait 2-3 minutes for Android Studio to load in browser

### 3. Upload Project Files

Upload all project files to your repository. You can:
- Use the GitHub web interface to upload files
- Or use Git commands in the Codespaces terminal

### 4. Configure API Keys

1. In Codespaces, open: `app/src/main/java/com/freestream/util/ApiKeys.kt`
2. Replace the placeholder values:
   ```kotlin
   const val JAMENDO_CLIENT_ID = "your_actual_jamendo_client_id"
   const val FREESOUND_API_KEY = "your_actual_freesound_api_key"
   ```

### 5. Build the Project

In the Codespaces terminal:

```bash
# Make gradlew executable
chmod +x gradlew

# Sync project with Gradle
./gradlew build
```

### 6. Run the App

1. Create an Android Virtual Device (AVD):
   - Click the device dropdown in the toolbar
   - Select "Device Manager"
   - Create a new device (Pixel 7, API 34 recommended)

2. Click the **Run** button (green triangle) or press `Shift+F10`

3. The app will install and launch on the emulator

### 7. Build APK (Optional)

To create an APK for installation on your phone:

```bash
./gradlew assembleDebug
```

The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

Download it from Codespaces and install on your Android device.

## Project Structure

```
app/src/main/java/com/freestream/
├── FreeStreamApplication.kt          # Application class with Hilt
├── MainActivity.kt                     # Single Activity entry point
├── data/
│   ├── model/                          # Data models (Track, Playlist, etc.)
│   ├── remote/
│   │   ├── api/                        # Retrofit API interfaces
│   │   ├── dto/                        # JSON DTOs for all 4 sources
│   │   └── source/                     # Music source implementations
│   ├── local/
│   │   ├── database/                   # Room database and DAOs
│   │   ├── entity/                     # Room entities
│   │   └── datastore/                  # DataStore preferences
│   └── repository/
│       └── MusicRepository.kt          # Aggregates all sources
├── service/
│   └── PlaybackService.kt              # MediaSessionService with ExoPlayer
├── di/
│   └── AppModule.kt                    # Hilt dependency injection
├── ui/
│   ├── theme/                          # Colors, typography, shapes
│   ├── components/                     # Reusable UI components
│   ├── screens/                        # Screen composables
│   ├── navigation/                     # Navigation components
│   └── viewmodel/                      # ViewModels
└── util/
    ├── ApiKeys.kt                      # API keys (configure this!)
    ├── Constants.kt                    # App constants
    ├── Extensions.kt                   # Kotlin extensions
    ├── NetworkUtils.kt                 # Network utilities
    └── TimeUtils.kt                    # Time formatting utilities
```

## Technical Stack

- **Language**: Kotlin 1.9.22
- **UI Framework**: Jetpack Compose 1.6+ (Material 3)
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with StateFlow
- **Dependency Injection**: Hilt 2.50
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0
- **Audio Playback**: ExoPlayer 1.2.1 (Media3)
- **Database**: Room 2.6.1
- **Preferences**: DataStore 1.0.0
- **Image Loading**: Coil 2.5.0

## Troubleshooting

### "No trending results"
- Check that your Jamendo Client ID is correct
- Verify internet connection in the emulator
- Check logcat for API errors

### "Search empty"
- Some sources may be temporarily down
- Try searching for common terms like "pop" or "rock"
- Check if sources are enabled in Settings

### "Playback error"
- Stream URL may be geo-blocked or expired
- Try the next track in results
- Check logcat for ExoPlayer errors

### "Build fails"
- Ensure Kotlin version is compatible
- Try `./gradlew clean` then rebuild
- Check for missing dependencies in build.gradle

### "Emulator audio not working"
- Enable emulator audio in AVD settings
- Or use a physical Android device for testing

## License

This project is provided as-is for educational purposes. The music content is sourced from free, Creative Commons licensed platforms. Please respect the licenses of individual tracks.

## Contributing

Feel free to fork and modify this project for your own use. If you find bugs or have improvements, contributions are welcome!

## Support

For issues with:
- **Jamendo API**: https://developer.jamendo.com/
- **Freesound API**: https://freesound.org/docs/api/
- **Internet Archive**: https://archive.org/help/
- **ccMixter**: http://ccmixter.org/api

---

**Enjoy streaming free music with FreeStream!**
