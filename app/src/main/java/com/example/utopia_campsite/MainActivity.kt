package com.example.utopia_campsite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.utopia_campsite.ui.theme.UtopiacampsiteTheme
import com.example.utopia_campsite.user.Reservation
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserProfileScreen
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

val db = Firebase.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UtopiacampsiteTheme {
                UserProfileScreen(User("ss", "abc@abs.com", "qw", "er", true, "123456789",
                        mutableListOf(), mutableListOf()))
                }
            }
        }
    }

