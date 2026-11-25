package com.example.utopia_campsite.messaging
import java.util.Date

/**
 * Stores details about one message
 * @property id: the unique Firestore document ID of the message
 * @property senderId: the uid of the person who sent the message (the owner or the entrant)
 * @property text: the text of the message
 * @property sentAt: when the server received the message, or null if it is not known yet
 */
data class Message (
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val sentAt: Date? = null
)
