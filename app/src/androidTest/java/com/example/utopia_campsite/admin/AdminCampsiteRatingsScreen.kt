package com.example.utopia_campsite.admin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.utopia_campsite.owner.Campsites
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for AdminCampsiteRatingsScreen
 * Fake removeRating and getUserName are passed in, so no test touches Firestore
 */
@RunWith(AndroidJUnit4::class)
class AdminCampsiteRatingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // A campsite with two ratings, keyed by uid like addRating saves them
    private val testCampsite = Campsites(
        id = "test-campsite",
        name = "Test Campsite",
        photoUrl = null,
        address = "",
        latitude = 0.0,
        longitude = 0.0,
        ratings = mapOf("uid-john" to 5, "uid-jane" to 2),
        tags = emptyList(),
        lotteryWinners = 0,
        lotteryWaitlist = mutableListOf(),
        description = "",
        registrationDate = "",
        bookingDate = "",
        maximumOccupants = 0,
        availableStartDate = "",
        availableEndDate = "",
        usersBooked = mutableListOf(),
        reviews = emptyList()
    )

    // Fake name lookup: returns a name straight away instead of calling UserRepository
    private val fakeNames = mapOf("uid-john" to "John Doe", "uid-jane" to "Jane Roe")
    private val fakeGetUserName: (String, (String) -> Unit, () -> Unit) -> Unit =
        { userId, onSuccess, onFailure ->
            val name = fakeNames[userId]
            if (name != null) onSuccess(name) else onFailure()
        }

    /**
     * When removing succeeds, the screen passes that user's uid
     * and only that rating disappears from the list
     */
    @Test
    fun removeRating_success_removesOnlyThatRating() {
        var removedUserId: String? = null

        composeTestRule.setContent {
            AdminCampsiteRatingsScreen(
                campsite = testCampsite,
                navController = rememberNavController(),
                removeRating = { _, userId, onSuccess, _ ->
                    removedUserId = userId
                    onSuccess()
                },
                getUserName = fakeGetUserName
            )
        }

        // Tap Remove on the first rating (John Doe's)
        composeTestRule.onAllNodesWithText("Remove")[0].performClick()

        assertEquals("uid-john", removedUserId)
        composeTestRule.onNodeWithText("John Doe").assertDoesNotExist()
        composeTestRule.onNodeWithText("Rating: 5").assertDoesNotExist()
        composeTestRule.onNodeWithText("Jane Roe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rating: 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Could not remove rating").assertDoesNotExist()
    } //end removeRating_success_removesOnlyThatRating

    /**
     * When removing fails, the rating stays on screen and the error message is shown
     */
    @Test
    fun removeRating_failure_keepsRatingAndShowsError() {
        composeTestRule.setContent {
            AdminCampsiteRatingsScreen(
                campsite = testCampsite,
                navController = rememberNavController(),
                removeRating = { _, _, _, onFailure ->
                    onFailure(Exception("Simulated Firestore failure"))
                },
                getUserName = fakeGetUserName
            )
        }

        composeTestRule.onAllNodesWithText("Remove")[0].performClick()

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jane Roe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Could not remove rating").assertIsDisplayed()
    } //end removeRating_failure_keepsRatingAndShowsError
} //end AdminCampsiteRatingsScreenTest