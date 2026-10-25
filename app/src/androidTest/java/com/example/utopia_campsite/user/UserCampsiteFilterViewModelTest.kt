package com.example.utopia_campsite.user

import com.example.utopia_campsite.owner.Campsites
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Contains unit tests for the campsite filtering functionality
 * Verifies that the ViewModel correctly filters campsites based on user inputs
 * Tests valid filters, invalid inputs, and campsite availability conditions
 */
class UserCampsiteFilterViewModelTest {
    private val viewModel = UserCampsiteFilterViewModel()

    @Before
    fun setup() {
        viewModel.updateAvailableCampsites(
            listOf(
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
                    availableStartDate = "15/10/2026",
                    availableEndDate = "20/10/2026",
                    maximumOccupants = 4
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
                    availableStartDate = "15/10/2026",
                    availableEndDate = "20/10/2026",
                    maximumOccupants = 8
                )
            )
        )
    }

    @Test
    fun testInvalidFilters() {
        val results = viewModel.filterCampsites("wrong", "wrong", "wrong", "wrong")
        assertEquals(0, results.size)
    }

    @Test
    fun testEmptyFilters() {
        val results = viewModel.filterCampsites("", "", "", "")
        assertEquals(2, results.size)
    }

    @Test
    fun testFilterByLocation() {
        val results = viewModel.filterCampsites("Banff", "", "", "")
        assertEquals(1, results.size)
        assertEquals("Banff", results[0].address) // Added check to ensure we are talking about the banff campsite object
    }

    @Test
    fun testFiltersByGuests() {
        val results = viewModel.filterCampsites("", "6", "", "")
        assertEquals(1, results.size)
        assertEquals("Jasper", results[0].address)
    }

    @Test
    fun testFilterByDates() {
        val results = viewModel.filterCampsites("", "", "16/10/2026", "19/10/2026")
        assertEquals(2, results.size)
    }

    @Test
    fun testInvalidInputs() {
        val results = viewModel.filterCampsites("", "hello", "", "")
        assertEquals(0, results.size)
    }

}