package com.example.utopia_campsite.user

import com.example.utopia_campsite.owner.Campsites
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserWaitlistTest {

    private lateinit var user: User
    private lateinit var csA: Campsites
    private lateinit var csB: Campsites

    @Before
    fun setup() {
        user = User("user_005","user@website.com", "john","doe", true, "555-555-5555")

        csA = Campsites(
            id = "camp_001",
            name = "Banff Mountain Campground",
            photoUrl = null,
            address = "Banff, Alberta, Canada",
            latitude = 51.1784,
            longitude = -115.5708,
            ratings = mutableListOf<Int>(5, 4, 5, 4),
            tags = mutableListOf<String>("Hiking"),
            lotteryWinners = 10,
            lotteryWaitlist = mutableListOf<String>("user_001", "user_002"),
            description = "Mountain campsite",
            registrationDate = "01/10/2026-10/10/2026",
            bookingDate = "15/10/2026-20/10/2026",
            availableStartDate = "15/10/2026",
            availableEndDate = "20/10/2026",
            maximumOccupants = 4
        )

        csB = Campsites(
            id = "camp_002",
            name = "Jasper Lakeside Campground",
            photoUrl = null,
            address = "Jasper, Alberta, Canada",
            latitude = 52.8737,
            longitude = -118.0814,
            ratings = mutableListOf<Int>(4, 5, 4, 5),
            tags = mutableListOf<String>("Fishing"),
            lotteryWinners = 10,
            lotteryWaitlist = mutableListOf<String>("user_003", "user_005"),
            description = "Lakeside campsite",
            registrationDate = "01/10/2026-10/10/2026",
            bookingDate = "15/10/2026-20/10/2026",
            availableStartDate = "15/10/2026",
            availableEndDate = "20/10/2026",
            maximumOccupants = 8
        )
    } // end setup()

    @Test
    fun addUsersToWaitlist () {
        val results = listOf("user_001", "user_002", "user_005")
        user.joinLottery(csA)
        assertEquals(results, csA.lotteryWaitlist)
    }

    @Test
    fun removeUsersFromWaitlist () {
        val results = listOf("user_003")
        user.leaveLottery(csB)
        assertEquals(results, csB.lotteryWaitlist)
    }

} // end class UserWaitlistTest