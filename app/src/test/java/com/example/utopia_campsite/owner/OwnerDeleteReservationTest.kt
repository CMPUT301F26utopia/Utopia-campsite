package com.example.utopia_campsite.owner

import com.example.utopia_campsite.user.Reservation
import com.example.utopia_campsite.user.User
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Contains unit test for OwnerCurrentReservations
 * Click cancel registration and check if registration is successfully removed from the list
 */
class OwnerDeleteReservationTest {
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
    fun testReservationIsDeleted() {
        ownerOne.cancelReservation(reservationsList, reservationsList[0])
        assertEquals(1, reservationsList.size)
    }
}