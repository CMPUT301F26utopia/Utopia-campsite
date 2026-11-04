package com.example.utopia_campsite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.utopia_campsite.admin.BrowseProfileScreen
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.owner.Owner
import com.example.utopia_campsite.ui.theme.UtopiacampsiteTheme
import com.example.utopia_campsite.user.User
import kotlin.String
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UtopiacampsiteTheme {
                BrowseProfileScreen(
                    ownersList = mutableListOf(
                    Owner(
                        postedCampsitesId = mutableListOf(),
                        deviceID = "wsdfcvbsString",
                        photoUrl = null,
                        email = "wdwdww@wdwd",
                        firstName = "geng",
                        lastName = "geng",
                        notificationsEnabled = true,
                        phone = "09876343",
                        policyViolated = false
                    )
                ), usersList = mutableListOf(
                        User(
                            deviceID = "ss",
                            photoUrl = null,
                            email = "abc@abs.com",
                            lastName = "qw",
                            firstName = "er",
                            notificationsEnabled = true,
                            phone = "123456789",
                            currentReservations = mutableListOf(),
                            pastReservations = emptyList()
                        )
                        )
                )
                }
            }
        }
    }

