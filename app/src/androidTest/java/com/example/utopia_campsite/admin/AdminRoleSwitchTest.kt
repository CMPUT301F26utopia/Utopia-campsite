package com.example.utopia_campsite.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.util.BottomNavigationBar
import com.example.utopia_campsite.user.util.CampsiteDetailsContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Tests for US 03.11.01: an admin can switch between the entrant, owner and admin UIs
 */
class AdminRoleSwitchTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Shows the bottom bar the way UtopiaNavApp does for an admin: switching changes the home route
    private fun setAdminNavBar() {
        composeTestRule.setContent {
            var homeScreenRoute by remember { mutableStateOf("user/campsites") }
            BottomNavigationBar(
                navController = rememberNavController(),
                homeScreenRoute = homeScreenRoute,
                profileScreenRoute = "user/profile",
                onSwitchRole = { newRole -> homeScreenRoute = "$newRole/campsites" }
            )
        }
    }//end setAdminNavBar

    @Test
    fun testNewUserIsNotAdmin() {
        assertEquals("user", User().role)
    }//end testNewUserIsNotAdmin

    @Test
    fun testNonAdminHasNoSwitchButton() {
        composeTestRule.setContent {
            BottomNavigationBar(
                navController = rememberNavController(),
                homeScreenRoute = "user/campsites",
                profileScreenRoute = "user/profile"
            )
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("switchRoleButton").assertDoesNotExist()
    }//end testNonAdminHasNoSwitchButton

    @Test
    fun testAdminSeesSwitchButtonAsEntrant() {
        setAdminNavBar()

        composeTestRule.onNodeWithTag("switchRoleButton").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entrant").assertIsDisplayed()
    }//end testAdminSeesSwitchButtonAsEntrant

    @Test
    fun testAdminSwitchesToOwner() {
        setAdminNavBar()

        composeTestRule.onNodeWithTag("switchRoleButton").performClick()

        composeTestRule.onNodeWithText("Owner").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entrant").assertDoesNotExist()
    }//end testAdminSwitchesToOwner

    @Test
    fun testAdminSwitchesToAdmin() {
        setAdminNavBar()

        composeTestRule.onNodeWithTag("switchRoleButton").performClick()
        composeTestRule.onNodeWithTag("switchRoleButton").performClick()

        composeTestRule.onNodeWithText("Admin").assertIsDisplayed()
    }//end testAdminSwitchesToAdmin

    @Test
    fun testAdminSwitchesBackToEntrant() {
        setAdminNavBar()

        repeat(3) { composeTestRule.onNodeWithTag("switchRoleButton").performClick() }

        composeTestRule.onNodeWithText("Entrant").assertIsDisplayed()
        // Home and Profile stay in every role
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }//end testAdminSwitchesBackToEntrant

    @Test
    fun testAdminCanPostReviewAsEntrant() {
        // fake onPostReview stands in for CampsiteRepository().addReview so Firebase isn't touched
        var postedText: String? = null
        composeTestRule.setContent {
            CampsiteDetailsContent(
                campsite = Campsites(id = "camp_001", name = "Banff Mountain Campground", photoUrl = null, address = "Banff, Alberta, Canada", description = "Mountain campsite"),
                onPostReview = { text, _, _ -> postedText = text }
            )
        }

        composeTestRule.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("Post Review"))
        composeTestRule.onNodeWithText("Write a review").performTextInput("Great spot, says the admin!")
        composeTestRule.onNodeWithText("Post Review").performClick()

        assertEquals("Great spot, says the admin!", postedText)
    }//end testAdminCanPostReviewAsEntrant
}
