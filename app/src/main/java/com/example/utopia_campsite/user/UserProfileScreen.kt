/**
 * Displays the User Profile as the starting view and
 *  Can toggle between user profile,current and past reservations
 *  Outstanding Issues:
 *  Past Reservations not implemented
 */

package com.example.utopia_campsite.user

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserProfileScreen(
    user: User,
    saveUser: (User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) -> Unit =
        { _, onSuccess, _ -> onSuccess() },
    deleteUser: (onSuccess: () -> Unit, onFailure: (Exception) -> Unit) -> Unit = { onSuccess, _ -> onSuccess() }
) {
    var currentScreen by remember { mutableStateOf("Me") } //display user profile as the starting view
    var currentUser by remember(user) { mutableStateOf(user) }
    var saveError by remember { mutableStateOf<String?>(null) }
    var deleteError by remember { mutableStateOf<String?>(null) }

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
                text = "Profile",
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
                    modifier = Modifier.weight(.1f).testTag("meButton"),
                    onClick = {currentScreen = "Me"},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "Me") selectedContainer else unselectedContainer,
                        contentColor = if (currentScreen == "Me") selectedContent   else unselectedContent,
                    )
                ) {
                    Text(
                        text = "Me",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.weight(.1f).testTag("currentReservationsButton"),
                    onClick = { currentScreen = "Current Reservations" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "Current Reservations") selectedContainer else unselectedContainer,
                        contentColor = if (currentScreen == "Current Reservations") selectedContent else unselectedContent
                    )
                ) {
                    Text(
                        text = "Current Reservations",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.weight(.1f),
                    onClick = {currentScreen = "Past Reservations"},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "Past Reservations") selectedContainer else unselectedContainer,
                        contentColor = if (currentScreen == "Past Reservations") selectedContent else unselectedContent
                    )
                ) {
                    Text(
                        text = "Past Reservations",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
        } //end lazyColumn

        when (currentScreen) { //toggles partial ui composition depending on button selected
            "Current Reservations" -> UserProfileCurrentReservationScreen(currentUser)
            "Me" -> UserProfileMeScreen(user = currentUser, onEditProfileClick = {currentScreen = "Update Profile"})
            "Update Profile" -> {
                UserUpdateProfileScreen(
                    user = currentUser,
                    onSave = { updated ->
                        saveError = null
                        saveUser(
                            updated,
                            { currentUser = updated; currentScreen = "Me" },
                            { e -> saveError = "Couldn't save: ${e.message}" }
                        )
                    },
                    onCancel = { saveError = null;deleteError = null; currentScreen = "Me" },
                    onDeleteProfile = {
                        deleteError = null
                        deleteUser(
                            {}, //on success the navhost leaves this screen, so nothing to do here
                            { e -> deleteError = "Couldn't delete: ${e.message}" }
                        )

                    }//end onDeleteProfile
                )
                saveError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                deleteError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        } //end when
    } //end Column
} //end UserProfileScreen