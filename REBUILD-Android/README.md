# REBUILD Android project

A native Android starter project for the REBUILD post-surgery gym tracker.

## Build an APK
1. Install Android Studio (latest stable) and Android SDK Platform 35.
2. Open this folder in Android Studio.
3. Allow Gradle sync to complete (Android Gradle Plugin 8.7.3).
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Android Studio writes the debug APK under `app/build/outputs/apk/debug/app-debug.apk`.

For a signed release APK, use **Build > Generate Signed Bundle / APK**.

## Features in this starter
- Native Android app, Java + Android SDK; minimum Android 6.0 (API 23).
- Today, Plan, Calendar and Progress sections.
- Three seeded workout days and per-date exercise completion.
- Per-exercise logging for weight (kg), sets, reps and notes.
- Local SQLite persistence.
- Calendar date selection and session log review.
- JSON text export for backup.

## Notes / next iteration
- The equipment tiles currently use local icon-style placeholders rather than licensed photographic assets.
- The starter includes a calendar date picker and history view; a future iteration can add a richer month grid, backup import, editable routines, per-set rows and bundled equipment photography.
- No chest press, flyes, push-ups or dips are included. Exercise selection is a conservative template, not medical clearance. Follow the surgeon's exact movement and loading restrictions.
