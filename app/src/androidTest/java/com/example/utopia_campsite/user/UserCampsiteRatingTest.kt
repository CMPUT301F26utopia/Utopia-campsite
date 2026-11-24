package com.example.utopia_campsite.user

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UserCampsiteRatingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testSubmitCampsiteRating() {
        var submittedRating = 0

        // Display rating dialog
        composeTestRule.setContent {
            UserCampsiteRating(
                modifier = Modifier,
                onDismiss = {},
                onSubmit = { rating ->
                    submittedRating = rating
                }
            )
        }

        // Select 4 stars
        composeTestRule
            .onNodeWithTag("ratingStar_4")
            .performClick()

        // Submit rating
        composeTestRule
            .onNodeWithTag("submitRatingButton")
            .performClick()

        // Verify submitted rating
        composeTestRule.runOnIdle {
            assertEquals(4, submittedRating)
        }
    }
}