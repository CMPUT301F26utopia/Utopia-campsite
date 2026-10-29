package com.example.utopia_campsite.user

import com.example.utopia_campsite.owner.Campsites
import junit.framework.TestCase
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
                    ratings = mutableListOf(5, 4, 5, 4),
                    tags = mutableListOf("Hiking"),
                    lotteryWinners = 10,
                    lotteryWaitlist = mutableListOf("sdfs0", "dsffdsfdsfds"),
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
                    ratings = mutableListOf(4, 5, 4, 5),
                    tags = mutableListOf("Fishing"),
                    lotteryWinners = 10,
                    lotteryWaitlist = mutableListOf("fds", "fds"),
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
        TestCase.assertEquals(0, results.size)
    }

    @Test
    fun testEmptyFilters() {
        val results = viewModel.filterCampsites("", "", "", "")
        TestCase.assertEquals(2, results.size)
    }

    @Test
    fun testFilterByLocation() {
        val results = viewModel.filterCampsites("Banff", "", "", "")
        TestCase.assertEquals(1, results.size)
        TestCase.assertEquals(
            "camp_001",
            results[0].id
        ) // Added check to ensure we are talking about the banff campsite object
    }

    @Test
    fun testFiltersByGuests() {
        val results = viewModel.filterCampsites("", "6", "", "")
        TestCase.assertEquals(1, results.size)
        TestCase.assertEquals("camp_002", results[0].id)
    }

    @Test
    fun testFilterByDates() {
        val results = viewModel.filterCampsites("", "", "16/10/2026", "19/10/2026")
        TestCase.assertEquals(2, results.size)
    }

    @Test
    fun testInvalidInputs() {
        val results = viewModel.filterCampsites("", "hello", "", "")
        TestCase.assertEquals(0, results.size)
    }

}