package com.example.utopia_campsite

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserProfileScreen
import com.example.utopia_campsite.user.UserUpdateProfileScreen
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class UserDeleteProfileTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUser = User(deviceID = "u1", firstName = "Test", lastName = "User")


    @Test
    fun updateScreen_showsDeleteButton() {
        composeTestRule.setContent { UserUpdateProfileScreen(user = testUser) }

        composeTestRule.onNodeWithTag("deleteProfileButton").assertIsDisplayed()
    }

    @Test
    fun updateScreen_deleteButton_opensConfirmationDialog() {
        composeTestRule.setContent { UserUpdateProfileScreen(user = testUser) }

        composeTestRule.onNodeWithTag("deleteProfileButton").performClick()

        composeTestRule.onNodeWithText("Delete profile?").assertIsDisplayed()
    }

    @Test
    fun updateScreen_deleteButton_doesNotDeleteWithoutConfirming() {
        var deleted = false
        composeTestRule.setContent {
            UserUpdateProfileScreen(user = testUser, onDeleteProfile = { deleted = true })
        }

        composeTestRule.onNodeWithTag("deleteProfileButton").performClick()

        assertFalse(deleted) // the dialog is open, but nothing has been deleted yet
    }

    @Test
    fun updateScreen_cancelInDialog_closesDialogAndDoesNotDelete() {
        var deleted = false
        composeTestRule.setContent {
            UserUpdateProfileScreen(user = testUser, onDeleteProfile = { deleted = true })
        }

        composeTestRule.onNodeWithTag("deleteProfileButton").performClick()
        composeTestRule.onNodeWithTag("cancelDeleteButton").performClick()

        composeTestRule.onNodeWithText("Delete profile?").assertDoesNotExist()
        assertFalse(deleted)
    }

    @Test
    fun updateScreen_confirmInDialog_callsOnDeleteProfile() {
        var deleted = false
        composeTestRule.setContent {
            UserUpdateProfileScreen(user = testUser, onDeleteProfile = { deleted = true })
        }

        composeTestRule.onNodeWithTag("deleteProfileButton").performClick()
        composeTestRule.onNodeWithTag("confirmDeleteButton").performClick()

        assertTrue(deleted)
        composeTestRule.onNodeWithText("Delete profile?").assertDoesNotExist() // dialog closes too
    }


    @Test
    fun profileScreen_confirmDelete_callsDeleteUser() {
        var deleteCalled = false
        composeTestRule.setContent {
            UserProfileScreen(user = testUser, deleteUser = { _, _ -> deleteCalled = true })
        }

        composeTestRule.onNodeWithTag("editProfileButton").performClick()
        composeTestRule.onNodeWithTag("deleteProfileButton").performClick()
        composeTestRule.onNodeWithTag("confirmDeleteButton").performClick()

        assertTrue(deleteCalled)
    }

    @Test
    fun profileScreen_cancelDelete_doesNotCallDeleteUser() {
        var deleteCalled = false
        composeTestRule.setContent {
            UserProfileScreen(user = testUser, deleteUser = { _, _ -> deleteCalled = true })
        }

        composeTestRule.onNodeWithTag("editProfileButton").performClick()
        composeTestRule.onNodeWithTag("deleteProfileButton").performClick()
        composeTestRule.onNodeWithTag("cancelDeleteButton").performClick()

        assertFalse(deleteCalled)
    }

    @Test
    fun profileScreen_deleteFails_showsErrorMessage() {
        composeTestRule.setContent {
            UserProfileScreen(
                user = testUser,
                deleteUser = { _, onFailure -> onFailure(Exception("boom")) }
            )
        }

        composeTestRule.onNodeWithTag("editProfileButton").performClick()
        composeTestRule.onNodeWithTag("deleteProfileButton").performClick()
        composeTestRule.onNodeWithTag("confirmDeleteButton").performClick()

        composeTestRule.onNodeWithText("Couldn't delete: boom").assertIsDisplayed()
    }

    @Test
    fun profileScreen_cancelEditing_clearsDeleteError() {
        composeTestRule.setContent {
            UserProfileScreen(
                user = testUser,
                deleteUser = { _, onFailure -> onFailure(Exception("boom")) }
            )
        }

        composeTestRule.onNodeWithTag("editProfileButton").performClick()
        composeTestRule.onNodeWithTag("deleteProfileButton").performClick()
        composeTestRule.onNodeWithTag("confirmDeleteButton").performClick()
        composeTestRule.onNodeWithTag("cancelButton").performClick() // leave the edit screen
        composeTestRule.onNodeWithTag("editProfileButton").performClick() // and come back
        composeTestRule.onNodeWithText("Couldn't delete: boom").assertDoesNotExist()
    }
}