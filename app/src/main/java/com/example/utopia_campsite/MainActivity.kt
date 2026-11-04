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
                    ),
                        Owner(
                            postedCampsitesId = mutableListOf(),
                            deviceID = "wsdfcvbsString",
                            photoUrl = null,
                            email = "wdwdww@wdwd",
                            firstName = "gang",
                            lastName = "geng",
                            notificationsEnabled = true,
                            phone = "09876343",
                            policyViolated = true
                        ),
                        Owner(
                            postedCampsitesId = mutableListOf(),
                            deviceID = "wsdfcvbsString",
                            photoUrl = null,
                            email = "wdwdww@wdwd",
                            firstName = "gung",
                            lastName = "geng",
                            notificationsEnabled = true,
                            phone = "09876343",
                            policyViolated = true
                        )
                ), usersList = mutableListOf(
                        User(
                            deviceID = "ss",
                            photoUrl = null,
                            email = "abc@abs.com",
                            lastName = "tangina",
                            firstName = "mo",
                            notificationsEnabled = true,
                            phone = "123456789",
                            currentReservations = mutableListOf(),
                            pastReservations = emptyList()
                        ),
                        User(
                            deviceID = "ss",
                            photoUrl = null,
                            email = "abc@abs.com",
                            lastName = "iyutin",
                            firstName = "kita",
                            notificationsEnabled = true,
                            phone = "123456789",
                            currentReservations = mutableListOf(),
                            pastReservations = emptyList()
                        ),
                        User(
                            deviceID = "ss",
                            photoUrl = null,
                            email = "abc@abs.com",
                            lastName = "bobo",
                            firstName = "ka",
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

