package com.example.utopia_campsite.messaging

import com.example.utopia_campsite.owner.Campsites
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the parts of messaging that do not need Firebase:
 * message validation, conversation IDs and the Conversation model
 * (Utopia 02.12.01)
 */
class MessagingTest {

    private val campsite = Campsites(
        id = "camp_001",
        name = "Banff Mountain Campground",
        ownerId = "owner_001"
    )


    //Message tests
    @Test
    fun message_defaultsToNoSendTime() {
        assertNull(Message().sentAt)
    }

    //Conversation ID tests
    @Test
    fun conversationId_joinsCampsiteAndUser() {
        assertEquals("camp_001_user_001", conversationId("camp_001", "user_001"))
    }

    @Test
    fun conversationId_differsForDifferentUsers() {
        assertTrue(conversationId("camp_001", "user_001") != conversationId("camp_001", "user_002"))
    }

    @Test
    fun conversationId_differsForDifferentCampsites() {
        assertTrue(conversationId("camp_001", "user_001") != conversationId("camp_002", "user_001"))
    }

    @Test
    fun forCampsite_copiesTheCampsiteDetails() {
        val conversation = Conversation.forCampsite(campsite, "user_001")
        assertEquals("camp_001_user_001", conversation.id)
        assertEquals("camp_001", conversation.campsiteId)
        assertEquals("Banff Mountain Campground", conversation.campsiteName)
        assertEquals("owner_001", conversation.ownerId)
        assertEquals("user_001", conversation.userId)
    }

    //Conversation tests
    @Test
    fun forCampsite_startsWithoutAPreviewOrTime() {
        val conversation = Conversation.forCampsite(campsite, "user_001")
        assertEquals("", conversation.lastMessage)
        assertNull(conversation.lastMessageAt)
    }

    @Test
    fun forCampsite_keepsABlankOwnerForOldCampsites() {
        val conversation = Conversation.forCampsite(campsite.copy(ownerId = ""), "user_001")
        assertEquals("", conversation.ownerId)
    }

    @Test
    fun forCampsite_givesTheOwnerAndTheEntrantTheSameConversation() {
        val seenByEntrant = Conversation.forCampsite(campsite, "user_001")
        val seenByOwner = Conversation.forCampsite(campsite, "user_001")
        assertEquals(seenByEntrant.id, seenByOwner.id)
    }

    @Test
    fun participants_listsTheOwnerThenTheEntrant() {
        val conversation = Conversation(ownerId = "owner_001", userId = "user_001")
        assertEquals(listOf("owner_001", "user_001"), conversation.participants)
    }

    @Test
    fun otherPersonId_isTheEntrantWhenTheOwnerIsLooking() {
        val conversation = Conversation(ownerId = "owner_001", userId = "user_001")
        assertEquals("user_001", conversation.otherPersonId("owner_001"))
    }

    @Test
    fun otherPersonId_isTheOwnerWhenTheEntrantIsLooking() {
        val conversation = Conversation(ownerId = "owner_001", userId = "user_001")
        assertEquals("owner_001", conversation.otherPersonId("user_001"))
    }
}