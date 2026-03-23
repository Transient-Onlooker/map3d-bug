# Maps3D 0.2.0 Tilt Crash Reproduction

Minimal Android reproduction for a crash in `com.google.android.gms:play-services-maps3d:0.2.0`.

The crash occurs during `Map3DView` initialization, before any custom camera update is applied.

## Setup

Add this entry to `local.properties` in the project root:

```properties
MAPS3D_API_KEY=YOUR_REAL_API_KEY
```

## Environment

- Maps3D SDK: `com.google.android.gms:play-services-maps3d:0.2.0`
- Play services base: `com.google.android.gms:play-services-base:18.10.0`
- Kotlin: `2.2.20`
- Android Gradle Plugin: `8.13.2`
- targetSdk: `36`
- minSdk: `26`
- Verified reproduction device: `Pixel 9 emulator`
- Verified reproduction Android version: `Android 16 / API 36`
- Verified reproduction Google Play services version: `25.34.34`

## Reproduction Steps

1. Open this project in Android Studio.
2. Sync the project.
3. Run the app on the environment above.
4. The app launches directly into a minimal Compose `AndroidView` wrapper around `Map3DView`.

## Minimal Repro Pattern

The app uses the same minimum structure that reproduces the issue:

```kotlin
AndroidView(
    factory = { context ->
        Map3DView(context).apply {
            onCreate(null)
        }
    },
    update = { map3dView ->
        map3dView.getMap3DViewAsync(object : OnMap3DViewReadyCallback {
            override fun onMap3DViewReady(map: GoogleMap3D) {
                // No custom camera updates here
            }

            override fun onError(error: Exception) {
                error.printStackTrace()
            }
        })
    }
)
```

No custom `Map3DOptions`, `setCamera(...)`, tilt, or bearing updates are required to trigger the crash.

## Expected Result

`Map3DView` initializes and renders normally.

## Actual Result

The app crashes during initialization with:

```text
java.lang.IllegalArgumentException: Tilt must be in the range [0.0, 90.0]: -90.0
```

## Notes

- The same failure reproduced in a fresh minimal project.
- The crash reproduces before any app-side camera logic is applied.
- This suggests an SDK initialization issue, emulator/device compatibility issue, or undocumented Maps3D constraint rather than an app camera input bug.
