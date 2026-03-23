package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.android.gms.maps3d.GoogleMap3D
import com.google.android.gms.maps3d.Map3DView
import com.google.android.gms.maps3d.OnMap3DViewReadyCallback

private const val TAG = "Maps3DRepro"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Maps3DMinimalRepro()
            }
        }
    }
}

@Composable
private fun Maps3DMinimalRepro() {
    val callback = remember {
        object : OnMap3DViewReadyCallback {
            override fun onMap3DViewReady(map: GoogleMap3D) {
                Log.d(TAG, "Map3DView ready: $map")
            }

            override fun onError(error: Exception) {
                Log.e(TAG, "Map3DView failed", error)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                Map3DView(context).apply {
                    onCreate(null)
                }
            },
            update = { map3DView ->
                map3DView.getMap3DViewAsync(callback)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "Maps3DMinimalRepro disposed")
        }
    }
}
