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

object MessageValidation {

    /** the longest message that can be sent, in characters */
    const val MAX_LENGTH = 1000

    /**
     * Cleans up the text of a message before it is sent
     *
     * @param text: the text the person typed
     * @return the text with surrounding whitespace removed, or null if it is empty or longer than MAX_LENGTH
     */
    fun clean(text: String): String? {
        val trimmed = text.trim()
        return if (trimmed.isEmpty() || trimmed.length > MAX_LENGTH) null else trimmed
    } //end clean
}