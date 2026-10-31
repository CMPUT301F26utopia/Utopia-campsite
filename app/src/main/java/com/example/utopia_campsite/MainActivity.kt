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
/*
import com.firebase.ui.auth.FirebaseAuthUI
import com.firebase.ui.auth.configuration.authUIConfiguration
import com.firebase.ui.auth.configuration.auth_provider.AuthProvider
import com.firebase.ui.auth.configuration.theme.AuthUITheme
import com.firebase.ui.auth.ui.screens.FirebaseAuthScreen
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UtopiacampsiteTheme {
                UserProfileScreen(
                    User(deviceID ="ss",
                        photoUrl = "abc@abs.com",
                        email = "qw",
                        firstName = "er",
                        lastName = "true",
                        notificationsEnabled = true,
                        phone ="123456789",
                        currentReservations = mutableListOf(
                            Reservation(
                            campsiteName = "campA",
                            photoUrl = "https://unsplash.com",
                            startReservedDate = "1/2/3",
                            endReservedDate = "1/5/3",
                            numOfGuests = 20
                        ), Reservation(
                                campsiteName = "campB",
                                photoUrl = "https://unsplash.com",
                                startReservedDate = "2/2/3",
                                endReservedDate = "2/5/3",
                                numOfGuests = 1
                            )),
                        emptyList()))
                }
            }
        }
    }

