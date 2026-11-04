/**
 * Displays the User Profile as the starting view and
 *  Can toggle between user profile,current and past reservations
 *  Outstanding Issues:
 *  highlight button when selected not implemented
 *  user profile not implemented
 *  Past Reservations not implemented
 */
package com.example.utopia_campsite.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utopia_campsite.owner.Owner
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserProfileCurrentReservationScreen
import com.example.utopia_campsite.user.UserProfileMeScreen
import com.example.utopia_campsite.user.UserUpdateProfileScreen

@Composable
fun BrowseProfileScreen(
    ownersList: MutableList<Owner>,
    usersList:  MutableList<User>
) {
    var currentScreen by remember { mutableStateOf("All") } //display user profile as the starting view
    // Define your highlight colors once for buttons
    val selectedContainer = Black
    val selectedContent   = White
    val unselectedContainer = White
    val unselectedContent   = Black

    Column(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Browse Profiles",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Button(
                    modifier = Modifier.weight(.1f),
                    onClick = {currentScreen = "All"},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "All") selectedContainer else unselectedContainer,
                        contentColor = if (currentScreen == "All") selectedContent   else unselectedContent,
                    )
                ) {
                    Text(
                        text = "All",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.weight(.1f),
                    onClick = { currentScreen = "Owners" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "Owners") selectedContainer else unselectedContainer,
                        contentColor = if (currentScreen == "Owners") selectedContent else unselectedContent
                    )
                ) {
                    Text(
                        text = "Owners",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.weight(.1f),
                    onClick = {currentScreen = "Users"},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "Users") selectedContainer else unselectedContainer,
                        contentColor = if (currentScreen == "Users") selectedContent else unselectedContent
                    )
                ) {
                    Text(
                        text = "Users",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }

            item {
                Button(
                    modifier = Modifier.weight(.1f),
                    onClick = {currentScreen = "Admins"},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "Admins") selectedContainer else unselectedContainer,
                        contentColor = if (currentScreen == "Admins") selectedContent else unselectedContent
                    )
                ) {
                    Text(
                        text = "Admins",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
        } //end lazyColumn

        when (currentScreen) { //toggles partial ui composition depending on button selected
            "All" -> AllProfilesScreen(ownersList = ownersList, usersList = usersList)
        }
    }
} //end BrowseProfileScreen