package com.example.utopia_campsite.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.UserFilterResultScreen
import org.junit.Rule
import org.junit.Test

class UserFilterResultScreenTest {

    val viewModel = UserCampsiteFilterViewModel()

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testCampsite = Campsites(
        id = "camp_001",
        name = "Banff Mountain Campground",
        photoUrl = null,
        address = "Banff, Alberta, Canada",
        latitude = 51.1784,
        longitude = -115.5708,
        ratings = listOf(5, 4, 5, 4),
        tags = listOf("Hiking"),
        lotteryWinners = 10,
        lotteryWaitlist = mutableListOf("userDeviceId1"),
        description = "Mountain campsite",
        registrationDate = "01/10/2026-10/10/2026",
        bookingDate = "15/10/2026-20/10/2026",
        availableStartDate = "15/10/2026",
        availableEndDate = "20/10/2026",
        maximumOccupants = 4,
        usersBooked = mutableListOf()
    )

    private fun setupScreen(
        onCampsiteClick: (Campsites) -> Unit = {}
    ) {
        viewModel.updateFilteredCampsites(listOf(testCampsite))

        composeTestRule.setContent {
            UserFilterResultScreen(
                modifier = Modifier,
                filterViewModel = viewModel,
                navController = rememberNavController(),
                onCampsiteClick = onCampsiteClick
            )
        }
    }

    @Test
    fun testResultsDisplayed() {
        setupScreen()

        composeTestRule.onNodeWithTag("matchingCampsitesFound")
            .assertTextContains("Matching Campsites: 1")

        composeTestRule.onNodeWithTag("campsiteCard_camp_001")
            .assertExists()
    }

    @Test
    fun testCampsiteCardClick() {
        var clicked = false

        setupScreen {
            clicked = true
        }

        composeTestRule.onNodeWithTag("campsiteCard_camp_001")
            .performClick()

        composeTestRule.runOnIdle {
            assert(clicked)
        }
    }

    @Test
    fun testBackButtonDisplayed() {
        setupScreen()

        composeTestRule.onNodeWithTag("backToFilterButton")
            .assertIsDisplayed()
    }
}