package com.example.utopia_campsite.owner

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
}
