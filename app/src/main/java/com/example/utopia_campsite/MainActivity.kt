package com.example.utopia_campsite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.utopia_campsite.owner.drawEndedLotteries
import com.example.utopia_campsite.ui.theme.UtopiacampsiteTheme
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserProfileMeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        drawEndedLotteries() // US 02.05.01: draws the winners of lotteries whose registration period is over
        setContent {
            UtopiacampsiteTheme {
                UtopiaNavApp() // Jumps to navApp
            }
        }
    }
}

