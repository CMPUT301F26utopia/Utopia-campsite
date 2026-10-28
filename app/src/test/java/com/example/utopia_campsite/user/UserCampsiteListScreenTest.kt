package com.example.utopia_campsite.user

import com.example.utopia_campsite.owner.Campsites
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class UserCampsiteListScreenTest {

    private lateinit var campsites: List<Campsites>
    @Before
    fun setup() {
        campsites = listOf(
            Campsites(
                id = "camp_001",
                name = "Banff Mountain Campground",
                photoUrl = null,
                address = "Banff, Alberta, Canada",
                latitude = 51.1784,
                longitude = -115.5708,
                ratings = listOf(5, 4, 5, 4),
                tags = listOf("Hiking"),
                lotteryWinners = 10,
                lotteryWaitlist = 5,
                description = "Mountain campsite",
                registrationDate = "01/10/2026-10/10/2026",
                bookingDate = "15/10/2026-20/10/2026",
                maximumOccupants = 4,
                availableStartDate = "15/10/2026",
                availableEndDate = "20/10/2026"
            ),
            Campsites(
                id = "camp_002",
                name = "Jasper Lakeside Campground",
                photoUrl = null,
                address = "Jasper, Alberta, Canada",
                latitude = 52.8737,
                longitude = -118.0814,
                ratings = listOf(4, 5, 4, 5),
                tags = listOf("Fishing"),
                lotteryWinners = 10,
                lotteryWaitlist = 5,
                description = "Lakeside campsite",
                registrationDate = "01/10/2026-10/10/2026",
                bookingDate = "15/10/2026-20/10/2026",
                maximumOccupants = 8,
                availableStartDate = "15/10/2026",
                availableEndDate = "20/10/2026"
            )
        )
    }//end setup

    @Test
    fun searchByNameReturnsMatchingCampsite(){
        val query = "Banff"

        val results = campsites.filter { campsite ->
            campsite.name.contains(query.trim(), ignoreCase = true)
        }

        assertEquals(1, results.size)
        assertEquals("Banff Mountain Campground", results[0].name)
    }//end searchByNameReturnsMatchingCampsite

    @Test
    fun searchByNameIsCaseInsensitive(){
        val query = "banff"

        val results = campsites.filter { campsite ->
            campsite.name.contains(query.trim(), ignoreCase = true)
        }

        assertEquals(1, results.size)
        assertEquals("Banff Mountain Campground", results[0].name)
    }//end searchByNameIsCaseInsensitive

    @Test
    fun searchWithTrailingAndLeadingWhiteSpacesReturnsMatchingCampsite(){
        val query = "  Banff  "

        val results = campsites.filter { campsite ->
            campsite.name.contains(query.trim(), ignoreCase = true)
        }

        assertEquals(1, results.size)
        assertEquals("Banff Mountain Campground", results[0].name)
    }//end searchWithTrailingAndLeadingWhiteSpacesReturnsMatchingCampsite

    @Test
    fun searchWithNoMatchesReturnsEmptyList(){
        val query = "Nonexistent"

        val results = campsites.filter { campsite ->
            campsite.name.contains(query.trim(), ignoreCase = true)
        }

        assertTrue(results.isEmpty())
    } //end searchWithNoMatchesReturnsEmptyList

    @Test
    fun emptySearchReturnsAllCampsites(){
        val query = ""
        val results = campsites.filter { campsite ->
            campsite.name.contains(query.trim(), ignoreCase = true)
        }
        assertEquals(campsites.size, results.size)
    }







}//end UserCampsiteListScreenTest