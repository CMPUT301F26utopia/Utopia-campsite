package com.example.utopia_campsite.user

import java.util.UUID

/**
*Stores details about a reservation
* @property reservationId: unique id generated when a reservation object is created
* @property campsiteId: the id of the campsite the user is reserved to
* @property startReservedDate: the start date of the reservation
* @property endReservedDate: the end date of the reservation
* @property numOfGuests: number of people reserved
*/
data class Reservation(
    val reservationId : String = UUID.randomUUID().toString(),
    val campsiteId : String,
    val startReservedDate: String,
    val endReservedDate: String,
    val numOfGuests: Int,
    val appPolicyRead: Boolean
) //change code so that we have a collection of reservation instead of storing them in an array field
//of users since a document have a limit size. reservation should store user id to tie it to the user
// who made the reservation.
// might change end reserved date to a timestamp object so its easier to filter current and past reservations