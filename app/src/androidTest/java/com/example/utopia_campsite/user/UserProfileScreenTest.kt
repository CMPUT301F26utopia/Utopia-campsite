package com.example.utopia_campsite.user

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
* Contains ui test for currentReservations
* Click the CurrentReservations tab and check if reserved campsites are displayed
* Click the CurrentReservations tab and check if message displays if no campsites are reserved
*/
class UserProfileScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var reservationsList :List<Reservation>
    private lateinit var userOne :User

    private lateinit var userTwo :User

    @Before
    fun setup() {
        reservationsList = listOf(
            Reservation(
                campsiteName = "campA",
                photoUrl = "https://unsplash.com",
                startReservedDate = "1/2/3",
                endReservedDate = "1/5/3",
                numOfGuests = 20
            ),
            Reservation(
                campsiteName = "campB",
                photoUrl = "https://unsplash.com",
                startReservedDate = "2/2/3",
                endReservedDate = "2/5/3",
                numOfGuests = 1
            )
        )
        userOne = User(
            deviceID = "ss",
            email = "abc@abs.com",
            lastName = "qw",
            firstName = "er",
            notificationsEnabled = true,
            phone = "123456789",
            currentReservations = reservationsList,
            pastReservations = emptyList()
        )
        userTwo = User(
            deviceID = "ee",
            email = "ad@abs.com",
            lastName = "we",
            firstName = "qw",
            notificationsEnabled = true,
            phone = "98765433",
            currentReservations = emptyList(),
            pastReservations = emptyList()
        )

    }

    @Test
    fun buttonClickedCurrentReservationsDisplayed() {
        // 2. Load your Composable screen into the active test view window
        composeTestRule.setContent {
            UserProfileScreen(userOne) // Your Composable function
        }
        // 3. Find the button by its starting text and assert it is visible
        composeTestRule.onNodeWithTag("currentReservationsButton").performClick()

        // 4. Perform a user interaction click event
        composeTestRule.onNodeWithText("campA").assertIsDisplayed()

        composeTestRule.onNodeWithText("campB").assertIsDisplayed()
    }

    @Test
    fun buttonClickedNoReservationsDisplayed() {
        // 2. Load your Composable screen into the active test view window
        composeTestRule.setContent {
            UserProfileScreen(userTwo) // Your Composable function
        }
        // 3. Find the button by its starting text and assert it is visible
        composeTestRule.onNodeWithTag("currentReservationsButton").performClick()

        // 4. Perform a user interaction click event
        composeTestRule.onNodeWithText("No Current Reservations").assertIsDisplayed()




    }
}