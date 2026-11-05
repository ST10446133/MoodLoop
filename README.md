# MoodLoop

MoodLoop is an Android mood tracking and reflection app built with Kotlin and Firebase. It lets users record moods, write private reflections, share anonymously on the Mood Wall, and follow their progress.

## Features

- Email and Google sign-in, registration, password reset, and password change
- Mood logging, journaling, and anonymous Mood Wall posts
- Offline mood and reflection actions saved locally and synced when connected
- Mood streaks, points, levels, and achievement badges
- English, isiZulu, and Afrikaans interface text
- Profile, tracker, reminder, and account settings
- Account deletion status stored in Firestore
- Firebase Cloud Messaging notifications for achievements and helpful reflection activity

## Tech stack

- Kotlin and Android Views
- Firebase Authentication
- Cloud Firestore
- Firebase Cloud Messaging and Cloud Functions
- Roomless local offline queue backed by Android SharedPreferences
- Gradle

## Project layout

```text
MoodLoop/
├── app/
│   ├── src/main/java/com/moodloop/app/  # Android screens, storage, auth, sync, notifications
│   ├── src/main/res/                    # App resources, fonts, and launcher artwork
│   ├── src/test/                        # Automated local/offline feature tests
│   ├── build.gradle
│   └── google-services.json
├── functions/                           # Firebase Cloud Functions
├── .github/workflows/android.yml        # Build and test on GitHub Actions
├── firestore.rules
├── firebase.json
├── build.gradle
└── settings.gradle.kts
```

## Build and test

Open the repository root in Android Studio, allow Gradle sync to finish, then run the app on an emulator or Android device.

To run the automated tests and build the debug APK on Windows:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

GitHub Actions runs the same Android test/build command and checks the Cloud Functions JavaScript syntax for pushes and pull requests to `main`.

## Firebase setup

The Android application uses the Firebase project configuration in `app/google-services.json`. If you connect a different Firebase project, replace that file and update `.firebaserc`. Deploy Firestore rules and Cloud Functions from the repository root with the Firebase CLI when backend changes are needed.

The Firebase backend stores account data and anonymous wall posts in Firestore. The app uses Firebase Authentication for signed-in access; Firestore security rules protect user-owned collections.
