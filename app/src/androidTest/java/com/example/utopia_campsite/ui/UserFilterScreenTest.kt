package com.example.utopia_campsite.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.UserFilterScreen
import org.junit.Rule
import org.junit.Test

class UserFilterScreenTest {
    val filterViewModel = UserCampsiteFilterViewModel()
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setupScreen() {
        composeTestRule.setContent {
            UserFilterScreen(
                modifier = Modifier,
                filterViewModel = filterViewModel,
                navController = rememberNavController()
            )
        }
    }

    @Test
    fun testFilterFieldsAreDisplayed() {
        setupScreen()

        composeTestRule.onNodeWithTag("locationInput").assertExists()
        composeTestRule.onNodeWithTag("guestsInput").assertExists()
        composeTestRule.onNodeWithTag("startDateInput").assertExists()
        composeTestRule.onNodeWithTag("endDateInput").assertExists()
        composeTestRule.onNodeWithTag("applyFiltersButton").assertExists()
    }

    @Test
    fun testUserCanEnterFiltersFields() {
        setupScreen()

        composeTestRule.onNodeWithTag("locationInput")
            .performTextInput("Edmonton")

        composeTestRule.onNodeWithTag("guestsInput")
            .performTextInput("3")

        composeTestRule.onNodeWithTag("locationInput")
            .assertTextContains("Edmonton")

        composeTestRule.onNodeWithTag("guestsInput")
            .assertTextContains("3")
    }

    @Test
    fun testCalendarOpensWhenFieldIsClicked() {
        setupScreen()

        composeTestRule.onNodeWithContentDescription("Select Start Date")
            .performClick()

        composeTestRule.onNodeWithTag("calendarCancel")
            .assertExists()

        composeTestRule.onNodeWithTag("calendarCancel")
            .performClick()
    }
}