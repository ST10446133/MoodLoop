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

## Firebase REST API testing with Postman

MoodLoop can be tested through Firebase's REST APIs in Postman. This is direct access to Firebase Authentication and Cloud Firestore; it is not a separate MoodLoop server or a custom REST endpoint, and the Android app continues to use the Firebase Android SDK.

### Sign in and get a Firebase ID token

Create a `POST` request to the Firebase Authentication REST API:

```text
https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=YOUR_FIREBASE_WEB_API_KEY
```

Set `Content-Type` to `application/json` and use this raw JSON body, replacing the placeholders in Postman:

```json
{
  "email": "YOUR_FIREBASE_USER_EMAIL",
  "password": "YOUR_FIREBASE_USER_PASSWORD",
  "returnSecureToken": true
}
```

A successful response contains `localId` (the Firebase Authentication UID) and `idToken`. Treat the password and ID token as private: do not commit them, export them in a shared collection, or post them in screenshots.

### Read the signed-in user's Firestore document

Create a `GET` request using the UID returned as `localId`:

```text
https://firestore.googleapis.com/v1/projects/YOUR_FIREBASE_PROJECT_ID/databases/(default)/documents/users/YOUR_FIREBASE_UID
```

Add this request header, using the token returned as `idToken`:

```text
Authorization: Bearer YOUR_FIREBASE_ID_TOKEN
```

Firestore evaluates requests authenticated with a Firebase ID token against the project's Firestore Security Rules. In this project, a user can access `/users/{userId}` and its private subcollections only when `{userId}` matches their authenticated UID. Therefore, requesting `/users` as a collection is not allowed by these owner-only rules and returns `PERMISSION_DENIED` (403). Do not make the data public or weaken the rules to resolve that response.

Direct Firebase Authentication and Firestore REST requests can be tested on the no-cost Spark plan, within Firebase's quotas; deploying Cloud Functions is separate and requires the Blaze plan. See the [Firebase pricing plan documentation](https://firebase.google.com/docs/projects/billing/firebase-pricing-plans), [Firebase Authentication REST API](https://firebase.google.com/docs/reference/rest/auth), and [Cloud Firestore REST API authentication documentation](https://firebase.google.com/docs/firestore/use-rest-api#authentication_and_authorization) for current details.

These Postman requests are for manual API testing and documentation. They do not add REST networking code to the Android app. Never commit real passwords, Firebase ID tokens, service-account keys, or other credentials to this repository.
