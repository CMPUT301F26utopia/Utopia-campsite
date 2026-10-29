package com.example.utopia_campsite.owner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.utopia_campsite.user.Reservation
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserProfileScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Contains ui test for ownerCurrentReservations
 * Click the CurrentReservations tab and check if reserved campsites are displayed
 * Click the CurrentReservations tab and check if message displays if no campsites are reserved
 * Click Cancel Reservation and check if the reservations displayed updates
 */
class OwnerProfileScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var reservationsList :MutableList<Reservation>
    private lateinit var ownerOne :Owner
    private lateinit var ownerTwo :Owner

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
        ownerOne = Owner(
            postedCampsites = mutableListOf(),
            deviceID = "ss",
            email = "abc@abs.com",
            lastName = "qw",
            firstName = "er",
            notificationsEnabled = true,
            phone = "123456789",
            currentReservations = reservationsList,
            pastReservations = emptyList()
        )
        ownerTwo = Owner(
            postedCampsites = mutableListOf(),
            deviceID = "ee",
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
            OwnerProfileScreen(ownerOne) // Your Composable function
        }

        composeTestRule.onNodeWithTag("currentReservationsButton").performClick()

        composeTestRule.onNodeWithText("campA").assertIsDisplayed()

        composeTestRule.onNodeWithText("campB").assertIsDisplayed()
    }

    @Test
    fun buttonClickedNoReservationsDisplayed() {
        composeTestRule.setContent {
            OwnerProfileScreen(ownerTwo) // Your Composable function
        }

        composeTestRule.onNodeWithTag("currentReservationsButton").performClick()

        composeTestRule.onNodeWithText("No Current Reservations").assertIsDisplayed()

    }

    @Test
    fun buttonClickedCurrentReservationsCancelReservation() {

        composeTestRule.setContent {
            OwnerProfileScreen(ownerOne)
        }

        composeTestRule.onNodeWithTag("currentReservationsButton").performClick()

        composeTestRule.onAllNodesWithTag("cancelReservationButton")[0].performClick()

        composeTestRule.onNodeWithText("campA").assertIsNotDisplayed()

        composeTestRule.onNodeWithText("campB").assertIsDisplayed()
    }
}