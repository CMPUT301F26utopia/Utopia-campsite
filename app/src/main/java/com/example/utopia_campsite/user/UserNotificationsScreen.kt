/**
 * Displays the notifications sent to the signed in entrant, newest first
 *  Displays message if there are no notifications
 *  Utopia 01.05.02, 01.07.02
 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utopia_campsite.owner.Notification
import com.example.utopia_campsite.owner.NotificationRepository

/**
 * Loads the user's notifications from firebase and shows each one in a card
 * @param userId: the firebase document ID of the user (their deviceID)
 */
@Composable
fun UserNotificationsScreen(userId: String, onWinnerClick: (String)-> Unit) {
    var notifications by remember { mutableStateOf<List<Notification>?>(null) } //null while loading
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        NotificationRepository().getForUser(
            userId = userId,
            onSuccess = { notifications = it },
            onFailure = { error = "Couldn't load notifications" }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Notifications", fontWeight = FontWeight.Bold, fontSize = 24.sp)

        val loaded = notifications
        when {
            error != null -> Text(error!!)
            loaded == null -> CircularProgressIndicator()
            loaded.isEmpty() -> Text("No notifications")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(loaded) { notification ->
                    Card(
                        onClick = {
                            if (
                                notification.title == "You won the lottery!" &&
                                notification.campsiteId.isNotBlank()
                            ) {
                                onWinnerClick(notification.campsiteId)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()// Opens the lottery winner page when a winning notification is clicked.
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(notification.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(notification.body, fontSize = 14.sp)
                            Text("${notification.date} ${notification.time}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } //end LazyColumn
        } //end when
    } //end Column
} //end UserNotificationsScreen
