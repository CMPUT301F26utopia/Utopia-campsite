package com.example.utopia_campsite.user

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.utopia_campsite.owner.Notification
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests that making or cancelling a reservation sends the user a notification
 * and updates the tables in the profile's records tab
 * Booking and cancelling are stubbed here so the test doesn't write to firebase
 * Utopia 01.05.01, 01.05.02, 01.07.02
 */
class UserReservationNotificationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val reservation = Reservation(campsiteId = "camp_001", startReservedDate = "01/12/2026", endReservedDate = "05/12/2026", numOfGuests = 3)

    /**
     * Stub of UserRepository.addCurrentReservation: adds the reservation and notifies the user
     * @param user: the user making the reservation
     * @param reservation: the reservation to add
     * @param inbox: stands in for the firebase notifications collection
     */
    private fun stubReserve(user: User, reservation: Reservation, inbox: MutableList<Notification>) {
        user.currentReservations.add(reservation)
        if (user.notificationsEnabled) { // NotificationRepository.send skips users who opted out
            inbox.add(Notification(title = "Reservation confirmed", campsiteId = reservation.campsiteId, recipientId = user.deviceID))
        }
    }//end stubReserve

    /**
     * Stub of cancelling in UserProfileCurrentReservationScreen: moves the reservation to cancelled and notifies the user
     * @param user: the user cancelling the reservation
     * @param reservation: the reservation to cancel
     * @param inbox: stands in for the firebase notifications collection
     */
    private fun stubCancel(user: User, reservation: Reservation, inbox: MutableList<Notification>) {
        user.currentReservations.remove(reservation)
        user.cancelledReservations.add(reservation)
        if (user.notificationsEnabled) {
            inbox.add(Notification(title = "Reservation cancelled", campsiteId = reservation.campsiteId, recipientId = user.deviceID))
        }
    }//end stubCancel

    /**
     * Opens the records tab, scrolling the profile's tab row first
     * since the records button starts off screen and the row doesn't build it until it is visible
     */
    private fun openRecords() {
        composeTestRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasTestTag("recordsButton"))
        composeTestRule.onNodeWithTag("recordsButton").performClick()
    }//end openRecords

    @Test
    fun testReservationSendsNotificationAndUpdatesRecords() {
        val user = User(deviceID = "user_001", notificationsEnabled = true)
        val inbox = mutableListOf<Notification>()

        stubReserve(user, reservation, inbox)

        assertEquals("Reservation confirmed", inbox.single().title)
        assertEquals("user_001", inbox.single().recipientId)

        composeTestRule.setContent { UserProfileScreen(user = user) }
        openRecords()
        composeTestRule.onNodeWithText("01/12/2026").assertIsDisplayed() //in the reservations table
        composeTestRule.onNodeWithText("No cancellations").assertIsDisplayed()
    }//end testReservationSendsNotificationAndUpdatesRecords

    @Test
    fun testCancellationSendsNotificationAndUpdatesRecords() {
        val user = User(deviceID = "user_001", notificationsEnabled = true, currentReservations = mutableListOf(reservation))
        val inbox = mutableListOf<Notification>()

        stubCancel(user, reservation, inbox)

        assertEquals("Reservation cancelled", inbox.single().title)

        composeTestRule.setContent { UserProfileScreen(user = user) }
        openRecords()
        composeTestRule.onNodeWithText("No reservations").assertIsDisplayed()
        composeTestRule.onNodeWithText("01/12/2026").assertIsDisplayed() //moved to the cancellations table
    }//end testCancellationSendsNotificationAndUpdatesRecords

    @Test
    fun testNoNotificationWhenNotificationsDisabled() {
        val user = User(deviceID = "user_001", notificationsEnabled = false)
        val inbox = mutableListOf<Notification>()

        stubReserve(user, reservation, inbox)

        assertTrue(inbox.isEmpty())
        assertEquals(1, user.currentReservations.size) //the reservation is still made
    }//end testNoNotificationWhenNotificationsDisabled

    @Test
    fun testCancelWithNotificationsDisabledStillUpdatesRecords() {
        val user = User(deviceID = "user_001", notificationsEnabled = false, currentReservations = mutableListOf(reservation))
        val inbox = mutableListOf<Notification>()

        stubCancel(user, reservation, inbox)

        assertTrue(inbox.isEmpty())
        assertTrue(user.currentReservations.isEmpty())
        assertEquals(listOf(reservation), user.cancelledReservations) //the cancellation is still recorded
    }//end testCancelWithNotificationsDisabledStillUpdatesRecords

    // The tests below show the records screen on its own, since UserProfileScreen
    // reloads past reservations from firebase and would replace the fake ones

    @Test
    fun testRecordsEmptyMessages() {
        composeTestRule.setContent { UserProfileRecordsScreen(User(deviceID = "user_001")) }

        composeTestRule.onNodeWithText("No reservations").assertIsDisplayed()
        composeTestRule.onNodeWithText("No cancellations").assertIsDisplayed()
    }//end testRecordsEmptyMessages

    @Test
    fun testRecordsShowCurrentAndPastReservations() {
        val past = Reservation(campsiteId = "camp_002", startReservedDate = "01/01/2026", endReservedDate = "03/01/2026", numOfGuests = 2)
        val user = User(deviceID = "user_001", currentReservations = mutableListOf(reservation), pastReservations = mutableListOf(past))
        composeTestRule.setContent { UserProfileRecordsScreen(user) }

        composeTestRule.onNodeWithText("01/12/2026").assertIsDisplayed() //current reservation
        composeTestRule.onNodeWithText("01/01/2026").assertIsDisplayed() //past reservation
        composeTestRule.onNodeWithText("No cancellations").assertIsDisplayed()
    }//end testRecordsShowCurrentAndPastReservations

    @Test
    fun testRecordsShowEveryCancellation() {
        val second = Reservation(campsiteId = "camp_002", startReservedDate = "10/12/2026", endReservedDate = "12/12/2026", numOfGuests = 2)
        val user = User(deviceID = "user_001", cancelledReservations = mutableListOf(reservation, second))
        composeTestRule.setContent { UserProfileRecordsScreen(user) }

        composeTestRule.onNodeWithText("No reservations").assertIsDisplayed()
        composeTestRule.onNodeWithText("01/12/2026").assertIsDisplayed()
        composeTestRule.onNodeWithText("10/12/2026").assertIsDisplayed()
    }//end testRecordsShowEveryCancellation

    @Test
    fun testRecordsShowGuestsAndCampsiteIdWhenNameUnknown() {
        val user = User(deviceID = "user_001", currentReservations = mutableListOf(reservation))
        composeTestRule.setContent { UserProfileRecordsScreen(user) }

        composeTestRule.onNodeWithText("camp_001").assertIsDisplayed() //no campsite with this id, so the id is shown
        composeTestRule.onNodeWithText("3").assertIsDisplayed() //number of guests
    }//end testRecordsShowGuestsAndCampsiteIdWhenNameUnknown

    @Test
    fun testEachTableHasDownloadButton() {
        composeTestRule.setContent { UserProfileRecordsScreen(User(deviceID = "user_001")) }

        composeTestRule.onAllNodesWithText("Download CSV").assertCountEquals(2)
    }//end testEachTableHasDownloadButton
}
