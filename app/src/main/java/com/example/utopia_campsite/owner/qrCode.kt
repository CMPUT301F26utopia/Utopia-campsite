package com.example.utopia_campsite.owner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.rememberQrKitPainter
import qrgenerator.qrkitpainter.solidBrush
import qrscanner.CameraLens
import qrscanner.QrScanner
import androidx.compose.runtime.setValue

@Composable
fun qrGenerator(campsiteId: String) {
    val painter = rememberQrKitPainter(data = campsiteId) {
        colors = QrKitColors(
            darkBrush = QrKitBrush.solidBrush(Color.Black),
            lightBrush = QrKitBrush.solidBrush(Color.White),
            ballBrush = QrKitBrush.solidBrush(Color.Black),
            frameBrush = QrKitBrush.solidBrush(Color.Black)

        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "QR Code generated",
            modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)
        )
    }
}

@Composable
fun qrScanner(onScanResult: ((String) -> Unit)? = null) {
    val context = LocalContext.current
    val navController = rememberNavController()
    var scanConfirm by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        QrScanner(
            modifier = Modifier.fillMaxSize(),
            flashlightOn = false,
            cameraLens = CameraLens.Back,
            openImagePicker = false,
            onCompletion = { scannedText ->
                // Guard against duplicate triggers while animating to the next screen
                if (!scanConfirm) {
                    scanConfirm = true
                    if (onScanResult != null) {
                        onScanResult(scannedText)
                    } else {
                        navController.navigate("user/campsite/${scannedText}")
                    }
                }
            },
            imagePickerHandler = {},
            onFailure = { /* Handle camera/scan error */ }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Camera permission is required to scan QR codes.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}
