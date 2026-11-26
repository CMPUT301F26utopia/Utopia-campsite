package com.example.utopia_campsite.messaging

import com.example.utopia_campsite.owner.Campsites
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for the parts of messaging that do not need Firebase
 */
class MessagingTest {

    @Test
    fun clean_trimsWhitespace() {
        assertEquals("hello", MessageValidation.clean("  hello \n"))
    }

    @Test
    fun clean_rejectsEmptyAndBlank() {
        assertNull(MessageValidation.clean(""))
        assertNull(MessageValidation.clean("   \n "))
    }

    @Test
    fun clean_rejectsTooLong() {
        assertNull(MessageValidation.clean("a".repeat(MessageValidation.MAX_LENGTH + 1)))
        assertEquals(MessageValidation.MAX_LENGTH, MessageValidation.clean("a".repeat(MessageValidation.MAX_LENGTH))?.length)
    }

    @Test
    fun conversationId_joinsCampsiteAndUser() {
        assertEquals("camp1_user1", conversationId("camp1", "user1"))
    }

    @Test
    fun forCampsite_copiesOwnerAndBuildsParticipants() {
        val campsite = Campsites(id = "camp1", name = "Lakeview", ownerId = "owner1")
        val conversation = Conversation.forCampsite(campsite, "user1")
        assertEquals("camp1_user1", conversation.id)
        assertEquals("Lakeview", conversation.campsiteName)
        assertEquals(listOf("owner1", "user1"), conversation.participants)
    }

    @Test
    fun otherPersonId_returnsTheOtherSide() {
        val conversation = Conversation(ownerId = "owner1", userId = "user1")
        assertEquals("user1", conversation.otherPersonId("owner1"))
        assertEquals("owner1", conversation.otherPersonId("user1"))
    }
}
