package com.example.utopia_campsite.user

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Contains unit test for userCurrentReservations
 * Click cancel registration and check if registration is successfully removed from the list
 */
class UserDeleteReservationTest {
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
    fun testReservationIsDeleted() {
        userOne.cancelReservation(reservationsList, reservationsList[0])
        assertEquals(1, reservationsList.size)
    }
}