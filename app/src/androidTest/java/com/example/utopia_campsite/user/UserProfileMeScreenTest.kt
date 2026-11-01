package com.example.utopia_campsite.user

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserProfileMeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var userA: User


    @Before
    fun setup(){
        userA = User(
                deviceID = "abcdefghijklmnopqrstuvwxyz",
                photoUrl = "https://tse2.mm.bing.net/th/id/OIP.SkTfUkSX5MfSRNtdYb0R-gHaJI?r=0&rs=1&pid=ImgDetMain&o=7&rm=3",
                email = "bayharbor@butcher.com",
                lastName = "Morgan",
                firstName = "Dexter",
                notificationsEnabled = true,
                phone = "123456789",
                currentReservations = mutableListOf(),
                pastReservations = emptyList()
        )

        composeTestRule.setContent {
            UserProfileMeScreen(user = userA)
        }
    }//end setup



    @Test
    fun testProfilePicDisplaysProperly(){
        //im making this a separate test because displaying an image is different than displaying text
        composeTestRule.onNodeWithContentDescription("Profile Picture").assertIsDisplayed()
    }//end testProfilePicDisplaysProperly

    @Test
    fun testUserInfoDisplaysProperly(){
        composeTestRule.onNodeWithText("Name: Dexter Morgan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email: bayharbor@butcher.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone number: 123456789").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifications: Enabled").assertIsDisplayed()
    }//end testUserInfoDisplaysProperly

    @Test
    fun testAllTextFieldsDisplayProperly(){
        composeTestRule.onNodeWithTag("editProfileButton").performClick()
        composeTestRule.onNodeWithTag("firstNameTextField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lastNameTextField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emailTextField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("phoneTextField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("notificationsSwitch").assertIsDisplayed()
    }//end testAllTextFieldsDisplayProperly

    @Test
    fun testUpdateProfile(){
        composeTestRule.onNodeWithTag("editProfileButton").performClick()
        composeTestRule.onNodeWithTag("firstNameTextField").performTextInput("Jim")
        composeTestRule.onNodeWithTag("lastNameTextField").performTextInput("Lindsay")
        composeTestRule.onNodeWithTag("emailTextField").performTextInput("ironLake@gmail.com")
        composeTestRule.onNodeWithTag("phoneTextField").performTextInput("987654321")
        composeTestRule.onNodeWithTag("notificationsSwitch").performClick()
        composeTestRule.onNodeWithTag("applyChangesButton").performClick()
        composeTestRule.onNodeWithText("Name: Jim Lindsay").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email: ironLake@gmail.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone number: 987654321").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifications: Disabled").assertIsDisplayed()
    }//end testUpdateProfile




}