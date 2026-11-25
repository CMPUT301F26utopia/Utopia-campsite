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
 * UI tests for AdminCampsiteReviewsScreen
 * A fake removeReview is passed in, so no test touches Firestore
 */
@RunWith(AndroidJUnit4::class)
class AdminCampsiteReviewsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // A campsite with two reviews, in the "First Last: text" format addReview saves
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
        reviews = listOf("John Doe: Great spot!", "Jane Roe: Too buggy")
    )

    /**
     * When removing succeeds, the screen passes the full review string (name included)
     * and only that review disappears from the list
     */
    @Test
    fun removeReview_success_removesOnlyThatReview() {
        var removedReview: String? = null

        composeTestRule.setContent {
            AdminCampsiteReviewsScreen(
                campsite = testCampsite,
                navController = rememberNavController(),
                removeReview = { _, review, onSuccess, _ ->
                    removedReview = review
                    onSuccess()
                }
            )
        }

        // Tap Remove on the first review
        composeTestRule.onAllNodesWithText("Remove")[0].performClick()

        assertEquals("John Doe: Great spot!", removedReview)
        composeTestRule.onNodeWithText("Great spot!").assertDoesNotExist()
        composeTestRule.onNodeWithText("Too buggy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Could not remove review").assertDoesNotExist()
    } //end removeReview_success_removesOnlyThatReview

    /**
     * When removing fails, the review stays on screen and the error message is shown
     */
    @Test
    fun removeReview_failure_keepsReviewAndShowsError() {
        composeTestRule.setContent {
            AdminCampsiteReviewsScreen(
                campsite = testCampsite,
                navController = rememberNavController(),
                removeReview = { _, _, _, onFailure ->
                    onFailure(Exception("Simulated Firestore failure"))
                }
            )
        }

        composeTestRule.onAllNodesWithText("Remove")[0].performClick()

        composeTestRule.onNodeWithText("Great spot!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Too buggy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Could not remove review").assertIsDisplayed()
    } //end removeReview_failure_keepsReviewAndShowsError
} //end AdminCampsiteReviewsScreenTest