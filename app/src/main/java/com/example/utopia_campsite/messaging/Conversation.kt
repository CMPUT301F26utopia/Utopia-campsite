package com.example.utopia_campsite.messaging

import com.example.utopia_campsite.owner.Campsites
import java.util.Date

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
){
    val participants: List<String>
        get() = listOf(ownerId,userId)

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
/**
 * Builds the ID of the conversation between an entrant and the owner of a campsite
 *
 * @param campsiteId: the Firestore document ID of the campsite
 * @param userId: the uid of the entrant
 * @return the conversation ID, "campsiteId_userId"
 */
fun conversationId(campsiteId: String, userId: String): String = "${campsiteId}_$userId"
