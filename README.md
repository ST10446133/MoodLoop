# MoodLoop

MoodLoop is an Android mood tracking and reflection app built with Kotlin and Firebase. The app allows users to record moods, write private reflections, share anonymous posts to the Mood Wall, track progress, earn achievements, and manage account settings.

## Features

- User registration and sign-in with Firebase Authentication
- Google Sign-In support
- Forgot password and password update functionality
- Mood logging with notes
- Private reflections
- Anonymous Mood Wall sharing
- “This helped me” interaction on Mood Wall posts
- Profile page with streaks, points, level, mood count, and achievements
- Reminder settings
- Account settings, display name update, logout, and delete account
- Cloud Firestore storage for user and app data
- Firestore Security Rules for data protection

## Technologies Used

- Kotlin
- Android Studio
- Firebase Authentication
- Cloud Firestore
- Firebase Cloud Messaging token storage
- Firestore Security Rules
- Gradle

## Firebase Usage

MoodLoop uses Firebase as the backend API layer for the app.

Firebase Authentication is used for:
- Registering users
- Signing users in
- Google Sign-In
- Password reset
- Password updates
- Account deletion

Cloud Firestore is used to store:
- User profiles
- Mood entries
- Reflections
- Anonymous Mood Wall posts
- Achievements
- Streaks and points
- Reminder settings
- Notification tokens

Firestore Security Rules are used to protect private user data so each user can only access their own information. Anonymous Mood Wall posts are stored separately in the `wallPosts` collection so they can be read by signed-in users.

## Project Structure

```text
MoodLoop/
├── app/
│   ├── src/main/java/com/moodloop/app/
│   ├── src/main/res/
│   ├── build.gradle
│   └── google-services.json
├── functions/
│   ├── index.js
│   └── package.json
├── firestore.rules
├── firebase.json
├── build.gradle
├── settings.gradle.kts
└── gradle/
