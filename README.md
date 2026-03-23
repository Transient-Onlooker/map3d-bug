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

- ## Error message(Raw)

```text
2026-03-23 23:31:01.886  5923-5923  AndroidRuntime          com.example.myapplication            E  FATAL EXCEPTION: main (Ask Gemini)
                                                                                                    Process: com.example.myapplication, PID: 5923
                                                                                                    java.lang.IllegalArgumentException: Tilt must be in the range [0.0, 90.0]: -90.0
                                                                                                    	at m138.io.a(:com.google.android.gms.policy_maps3d_dynamite@251899901@251899900032.753057229.753057229:48)
                                                                                                    	at m138.io.d(:com.google.android.gms.policy_maps3d_dynamite@251899901@251899900032.753057229.753057229:11)
                                                                                                    	at m138.ht.<init>(:com.google.android.gms.policy_maps3d_dynamite@251899901@251899900032.753057229.753057229:12)
                                                                                                    	at m138.hs.createFromParcel(:com.google.android.gms.policy_maps3d_dynamite@251899901@251899900032.753057229.753057229:94)
                                                                                                    	at m138.ak.a(:com.google.android.gms.policy_maps3d_dynamite@251899901@251899900032.753057229.753057229:9)
                                                                                                    	at m138.hn.G(:com.google.android.gms.policy_maps3d_dynamite@251899901@251899900032.753057229.753057229:97)
                                                                                                    	at m138.aj.onTransact(:com.google.android.gms.policy_maps3d_dynamite@251899901@251899900032.753057229.753057229:21)
                                                                                                    	at android.os.Binder.transact(Binder.java:1273)
                                                                                                    	at com.google.android.gms.internal.maps3d.zza.zzb(com.google.android.gms:play-services-maps3d@@0.2.0:2)
                                                                                                    	at com.google.android.gms.maps3d.zzi.zzd(com.google.android.gms:play-services-maps3d@@0.2.0:4)
                                                                                                    	at com.google.android.gms.maps3d.zzn.zzc(com.google.android.gms:play-services-maps3d@@0.2.0:8)
                                                                                                    	at com.google.android.gms.maps3d.zzap.zzg(com.google.android.gms:play-services-maps3d@@0.2.0:2)
                                                                                                    	at com.google.android.gms.maps3d.zzal.invoke(Unknown Source:8)
                                                                                                    	at com.google.android.gms.maps3d.zzn.zza(com.google.android.gms:play-services-maps3d@@0.2.0:9)
                                                                                                    	at com.google.android.gms.maps3d.zzm.invoke(Unknown Source:4)
                                                                                                    	at com.google.android.gms.maps3d.zzab.zzb(com.google.android.gms:play-services-maps3d@@0.2.0:3)
                                                                                                    	at com.google.android.gms.maps3d.zzr.invoke(Unknown Source:8)
                                                                                                    	at com.google.android.gms.maps3d.zzs.onSuccess(com.google.android.gms:play-services-maps3d@@0.2.0:1)
                                                                                                    	at com.google.android.gms.tasks.zzm.run(com.google.android.gms:play-services-tasks@@18.4.1:1)
                                                                                                    	at android.os.Handler.handleCallback(Handler.java:995)
                                                                                                    	at android.os.Handler.dispatchMessage(Handler.java:103)
                                                                                                    	at android.os.Looper.loopOnce(Looper.java:248)
                                                                                                    	at android.os.Looper.loop(Looper.java:338)
                                                                                                    	at android.app.ActivityThread.main(ActivityThread.java:9067)
                                                                                                    	at java.lang.reflect.Method.invoke(Native Method)
                                                                                                    	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:593)
                                                                                                    	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:932)
```

