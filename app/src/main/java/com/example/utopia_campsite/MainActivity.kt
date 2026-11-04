package com.example.utopia_campsite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.utopia_campsite.ui.theme.UtopiacampsiteTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UtopiacampsiteTheme {
                UtopiaNavApp() // Jumps to navApp
            }
        }
    }
}

