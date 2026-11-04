package com.example.utopia_campsite

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.admin.AdminCampsiteDetailScreen
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.util.CampsiteDetailsContent
import org.junit.Rule
import org.junit.Test

class CampsiteDetailsTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun campsite(photoUrl: String?) = Campsites(
        id = "test_1",
        name = "Test Campsite",
        photoUrl = photoUrl,
        address = "Edmonton, Alberta",
        description = "A test campsite",
        tags = listOf("Hiking", "Fishing"),
        maximumOccupants = 4,
        availableStartDate = "01/01/2027",
        availableEndDate = "10/01/2027"
    )


    @Test
    fun detailsContent_showsCampsiteInfo() {
        composeTestRule.setContent { CampsiteDetailsContent(campsite(photoUrl = null)) }

        composeTestRule.onNodeWithText("Test Campsite").assertIsDisplayed()
        composeTestRule.onNodeWithText("Edmonton, Alberta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tags: Hiking, Fishing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Max occupancy: 4").assertIsDisplayed()
        composeTestRule.onNodeWithText("Available: 01/01/2027 - 10/01/2027").assertIsDisplayed()
    }

    @Test
    fun detailsContent_noRatings_showsMessageInsteadOfNaN() {
        composeTestRule.setContent { CampsiteDetailsContent(campsite(photoUrl = null)) }

        composeTestRule.onNodeWithText("Rating: No ratings yet").assertIsDisplayed()
    }

    @Test
    fun detailsContent_nullOrBlankPhoto_hidesImage() {
        composeTestRule.setContent { CampsiteDetailsContent(campsite(photoUrl = null)) }
        composeTestRule.onNodeWithContentDescription("Image of Test Campsite").assertDoesNotExist()
    }

    @Test
    fun detailsContent_blankPhoto_hidesImage() {
        composeTestRule.setContent { CampsiteDetailsContent(campsite(photoUrl = "")) }
        composeTestRule.onNodeWithContentDescription("Image of Test Campsite").assertDoesNotExist()
    }

    @Test
    fun detailsContent_withPhoto_showsImage() {
        composeTestRule.setContent { CampsiteDetailsContent(campsite(photoUrl = "https://example.com/a.jpg")) }
        composeTestRule.onNodeWithContentDescription("Image of Test Campsite").assertExists()
    }


    @Test
    fun adminScreen_withPhoto_showsRemoveImageAndDeleteButtons() {
        composeTestRule.setContent {
            AdminCampsiteDetailScreen(campsite(photoUrl = "https://example.com/a.jpg"), rememberNavController())
        }

        composeTestRule.onNodeWithText("Remove image").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete campsite").assertIsDisplayed()
    }

    @Test
    fun adminScreen_withoutPhoto_hidesRemoveImageButton() {
        composeTestRule.setContent {
            AdminCampsiteDetailScreen(campsite(photoUrl = null), rememberNavController())
        }

        composeTestRule.onNodeWithText("Remove image").assertDoesNotExist()
        composeTestRule.onNodeWithText("Delete campsite").assertIsDisplayed()
    }

    @Test
    fun adminScreen_deleteButton_opensConfirmationDialog() {
        composeTestRule.setContent {
            AdminCampsiteDetailScreen(campsite(photoUrl = null), rememberNavController())
        }

        composeTestRule.onNodeWithText("Delete campsite").performClick()

        composeTestRule.onNodeWithText("Delete campsite?").assertIsDisplayed()
    }

    @Test
    fun adminScreen_cancelInDialog_closesDialogAndDeletesNothing() {
        composeTestRule.setContent {
            AdminCampsiteDetailScreen(campsite(photoUrl = null), rememberNavController())
        }

        composeTestRule.onNodeWithText("Delete campsite").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithText("Delete campsite?").assertDoesNotExist()
    }
}