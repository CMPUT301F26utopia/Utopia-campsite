package com.example.utopia_campsite.admin

import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.owner.buildCampsite
import com.example.utopia_campsite.user.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests that an admin can act as an entrant and an owner with their admin profile (US 03.11.01)
 * Nothing here touches Firebase, only the local objects are changed
 */
class AdminAsEntrantAndOwnerTest {

    private lateinit var admin: User
    private lateinit var campsite: Campsites

    @Before
    fun setup() {
        admin = User(
            deviceID = "admin_001",
            email = "admin@utopia.com",
            firstName = "Ada",
            lastName = "Admin",
            phone = "555-555-5555",
            role = "admin"
        )

        campsite = Campsites(
            id = "camp_001",
            name = "Banff Mountain Campground",
            photoUrl = null,
            address = "Banff, Alberta, Canada",
            lotteryWaitlist = mutableListOf("user_001", "user_002"),
            description = "Mountain campsite"
        )
    }//end setup

    @Test
    fun testAdminCanJoinLottery() {
        admin.joinLottery(campsite)

        assertTrue(campsite.lotteryWaitlist.contains("admin_001"))
        assertEquals(3, campsite.lotteryWaitlist.size)
    }//end testAdminCanJoinLottery

    @Test
    fun testAdminCanLeaveLottery() {
        admin.joinLottery(campsite)
        admin.leaveLottery(campsite)

        assertFalse(campsite.lotteryWaitlist.contains("admin_001"))
        assertEquals(listOf("user_001", "user_002"), campsite.lotteryWaitlist)
    }//end testAdminCanLeaveLottery

    @Test
    fun testAdminKeepsAdminRoleAfterJoiningLottery() {
        admin.joinLottery(campsite)

        assertEquals("admin", admin.role)
    }//end testAdminKeepsAdminRoleAfterJoiningLottery

    @Test
    fun testAdminCanCreateCampsite() {
        // same form input an owner would give, campsite creation doesn't depend on the user's role
        val created = buildCampsite(
            name = "Admin's Lakeside",
            address = "74 St NW",
            description = "Quiet spot by the lake",
            maxOccupancy = "6",
            latitude = "53.5",
            longitude = "-113.5",
            photoUrl = "",
            tags = "lake, hiking",
            lotteryWinners = "2",
            registrationStart = "10/10/2026",
            registrationEnd = "20/10/2026",
            bookingStart = "01/11/2026",
            bookingEnd = "10/11/2026"
        )

        assertNotNull(created)
        assertEquals("Admin's Lakeside", created!!.name)
    }//end testAdminCanCreateCampsite
}
