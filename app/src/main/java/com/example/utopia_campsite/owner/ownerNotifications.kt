package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.user.User
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a notification sent by a campsite owner to entrants
 */
data class Notification (
    val title: String = "",
    val body: String = "",
    val ownerId: String = "",
    val campsiteId: String = "",
    val recipientId: String = "",
    val date: String = "",
    val time: String = "",
)

var isAllNotificationsVisible by mutableStateOf(false)

fun allNotificationsScreen() {
    isAllNotificationsVisible = true
}

@Composable
fun AllNotificationsDialog(ownerId: String) {
    if (isAllNotificationsVisible) {
        AlertDialog(
            onDismissRequest = { isAllNotificationsVisible = false },
            confirmButton = {
                TextButton(onClick = { isAllNotificationsVisible = false }) {
                    Text("Close")
                }
            },
            title = { Text("All Sent Notifications") },
            text = {
                var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
                var error by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(ownerId) {
                    if (ownerId.isNotBlank()) {
                        FirebaseFirestore.getInstance().collection("notifications")
                            .whereEqualTo("ownerId", ownerId)
                            .get()
                            .addOnSuccessListener { result ->
                                notifications = result.documents.mapNotNull { it.toObject(Notification::class.java) }
                                    .sortedByDescending { it.date + it.time }
                            }
                            .addOnFailureListener {
                                error = "Couldn't load notifications"
                            }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when {
                        error != null -> Text(error!!)
                        notifications.isEmpty() -> Text("No notifications found")
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(notifications) { notification ->
                                    NotificationCard(notification)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

/**
 * Repository to send and manage notifications in Firebase Firestore
 */
class NotificationRepository {
    private val db by lazy { FirebaseFirestore.getInstance() }

    /**
     * Sends a notification from a campsite owner to a list of recipients.
     * Only recipients with notificationsEnabled set to true will receive the notification.
     */
    fun send(
        ownerId: String,
        campsiteId: String,
        title: String,
        body: String,
        recipients: List<User>,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val now = Date()
        val dateStr = dateFormat.format(now)
        val timeStr = timeFormat.format(now)

        // Filter recipients to only send notifications to users who have notifications enabled
        val enabledRecipients = recipients.filter { it.notificationsEnabled }

        if (enabledRecipients.isEmpty()) {
            onSuccess()
            return
        }

        try {
            val batch = db.batch()
            for (recipient in enabledRecipients) {
                val noti = Notification(
                    title = title,
                    body = body,
                    ownerId = ownerId,
                    campsiteId = campsiteId,
                    recipientId = recipient.deviceID,
                    date = dateStr,
                    time = timeStr
                )

                // Add notification to top-level "notifications" collection (for logs & owner view)
                val notiRef = db.collection("notifications").document()
                batch.set(notiRef, noti)

                // Add notification to user's "notifications" subcollection (for entrant view)
                if (recipient.deviceID.isNotBlank()) {
                    val userNotiRef = db.collection("users")
                        .document(recipient.deviceID)
                        .collection("notifications")
                        .document(notiRef.id)
                    batch.set(userNotiRef, noti)
                }
            }

            batch.commit()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    /**
     * Fetches every notification sent to one user, newest first
     * Sorted on the device so Firestore doesn't need a composite index
     * @param userId: the firebase document ID of the user (their deviceID)
     */
    fun getForUser(
        userId: String,
        onSuccess: (List<Notification>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("notifications")
            .whereEqualTo("recipientId", userId)
            .get()
            .addOnSuccessListener { result ->
                // "yyyy-MM-dd" + "HH:mm:ss" strings sort in time order
                onSuccess(result.documents.mapNotNull { it.toObject(Notification::class.java) }
                    .sortedByDescending { it.date + it.time })
            }
            .addOnFailureListener { onFailure(it) }
    }


}


@Composable
fun NotificationCard(notification: Notification) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(text = notification.body)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${notification.date} ${notification.time}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Campsite ID: ${notification.campsiteId}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
