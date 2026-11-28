package com.example.utopia_campsite.owner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CampsiteWaitlistTest {

    @Test
    fun campsiteWaitlist_containsRegisteredEntrants() {
        val campsite = Campsites(
            id = "camp1",
            lotteryWaitlist = mutableListOf("user1", "user2")
        )

        assertEquals(2, campsite.lotteryWaitlist.size)
        assertTrue(campsite.lotteryWaitlist.contains("user1"))
        assertTrue(campsite.lotteryWaitlist.contains("user2"))
    }

    @Test
    fun campsiteWaitlist_emptyByDefault() {
        val campsite = Campsites(id = "camp1")

        assertTrue(campsite.lotteryWaitlist.isEmpty())
    }

    @Test
    fun campsiteWaitlist_doesNotContainUnregisteredUser() {
        val campsite = Campsites(
            id = "camp1",
            lotteryWaitlist = mutableListOf("user1", "user2")
        )

        assertFalse(campsite.lotteryWaitlist.contains("user3"))
    }
}