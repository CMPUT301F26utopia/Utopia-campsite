/**
 * This is a conversation class
 * This model stores the chat between one campsite owner and one entrant who reserved that campsite
 *
 * There is exactly one conversation per campsite and entrant, its ID is built from both
 * (see conversationId), so the owner and the entrant always open the same one
 *
 * This model does not deal with interacting with the Firebase database,
 * that is the responsibility of MessageRepository.kt
 *
 * Outstanding Issues:
 * None
 */
package com.example.utopia_campsite.messaging

import com.example.utopia_campsite.owner.Campsites
import java.util.Date

/**
 * Builds the ID of the conversation between an entrant and the owner of a campsite
 *
 * @param campsiteId: the Firestore document ID of the campsite
 * @param userId: the uid of the entrant
 * @return the conversation ID, "campsiteId_userId"
 */
fun conversationId(campsiteId: String, userId: String): String = "${campsiteId}_$userId"

/**
 * Stores details about a conversation
 * @property id: the unique Firestore document ID of the conversation
 * @property campsiteId: the ID of the campsite the conversation is about
 * @property campsiteName: the name of the campsite, so lists can show it without loading the campsite
 * @property ownerId: the uid of the campsite's owner (Campsites.ownerId)
 * @property userId: the uid of the entrant (User.deviceID)
 * @property lastMessage: a short preview of the newest message
 * @property lastMessageAt: when the newest message was sent, or null if there is none yet
 */
data class Conversation(
    val id: String = "",
    val campsiteId: String = "",
    val campsiteName: String = "",
    val ownerId: String = "",
    val userId: String = "",
    val lastMessage: String = "",
    val lastMessageAt: Date? = null
) {

    /**
     * The two people who can read and write in this conversation
     */
    val participants: List<String>
        get() = listOf(ownerId, userId)

    /**
     * Gets the uid of the other person in the conversation
     *
     * @param myId: the uid of the person looking at the conversation
     * @return the owner's uid if myId is the entrant, otherwise the entrant's uid
     */
    fun otherPersonId(myId: String): String = if (myId == ownerId) userId else ownerId

    companion object {

        /**
         * Builds the conversation for a campsite and an entrant, before any message has been sent
         *
         * @param campsite: the campsite the entrant reserved
         * @param userId: the uid of the entrant
         * @return the conversation, its ownerId is blank if the campsite has no ownerId yet
         */
        fun forCampsite(campsite: Campsites, userId: String): Conversation = Conversation(
            id = conversationId(campsite.id, userId),
            campsiteId = campsite.id,
            campsiteName = campsite.name,
            ownerId = campsite.ownerId,
            userId = userId
        )
    } //end companion
}
