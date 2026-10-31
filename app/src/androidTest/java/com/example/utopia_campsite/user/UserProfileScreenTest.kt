package com.example.utopia_campsite.user

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
* Contains ui test for userCurrentReservations
* Click the CurrentReservations tab and check if reserved campsites are displayed
* Click the CurrentReservations tab and check if message displays if no campsites are reserved
 * Click Cancel Reservation and check if the reservations displayed updates
*/
class UserProfileScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var reservationsList :MutableList<Reservation>
    private lateinit var userOne :User
    private lateinit var userTwo :User

    @Before
    fun setup() {
        reservationsList = mutableListOf(
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
            photoUrl = null,
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
            photoUrl = null,
            email = "ad@abs.com",
            lastName = "we",
            firstName = "qw",
            notificationsEnabled = true,
            phone = "98765433",
            currentReservations = mutableListOf(),
            pastReservations = emptyList()
        )

    }

    @Test
    fun buttonClickedCurrentReservationsDisplayed() {

        composeTestRule.setContent {
            UserProfileScreen(userOne) // Your Composable function
        }

        composeTestRule.onNodeWithTag("currentReservationsButton").performClick()

        composeTestRule.onNodeWithText("campA").assertIsDisplayed()

        composeTestRule.onNodeWithText("campB").assertIsDisplayed()
    }

    @Test
    fun buttonClickedNoReservationsDisplayed() {
        composeTestRule.setContent {
            UserProfileScreen(userTwo) // Your Composable function
        }

        composeTestRule.onNodeWithTag("currentReservationsButton").performClick()

        composeTestRule.onNodeWithText("No Current Reservations").assertIsDisplayed()

    }

    @Test
    fun buttonClickedCurrentReservationsCancelReservation() {

        composeTestRule.setContent {
            UserProfileScreen(userOne)
        }

        composeTestRule.onNodeWithTag("currentReservationsButton").performClick()

        composeTestRule.onAllNodesWithTag("cancelReservationButton")[0].performClick()

        composeTestRule.onNodeWithText("campA").assertIsNotDisplayed()

        composeTestRule.onNodeWithText("campB").assertIsDisplayed()
    }
}