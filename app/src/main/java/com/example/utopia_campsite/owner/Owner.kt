/**
 * This is an Owner class
 * This model stores information about an owner including its posted campsites,
 * email, phoneNumber, photoURL, etc
 *
 * Outstanding Issues:
 * More fields may be required as more user stories are implemented
 */
package com.example.utopia_campsite.owner

import com.example.utopia_campsite.user.Reservation

/**
 *Stores details about a user
 * @property postedCampsites: campsites posted by the owner
 * @property deviceID: unique device id
 * @property email: email address
 * @property firstName: first name
 * @property lastName: last name
 * @property notificationsEnabled: whether notifications are enabled on the user's device
 * @property phone: phone number
 * @property currentReservations: stores a list of current reservations
 * @property pastReservations: stores a list of past reservations
 */
data class Owner (
     val postedCampsites: MutableList<Campsites>,
     val deviceID: String,
     val email: String,
     val firstName: String,
     val lastName: String,
     val notificationsEnabled: Boolean,
     val phone: String,
     val currentReservations: MutableList<Reservation>,
     val pastReservations : List<Reservation>
) {
    fun cancelReservation(currentReservations: MutableList<Reservation>, reservationToDelete: Reservation) {
        //take a reservation object to delete and the list of currentReservation
        //remove the object in currentReservations
        currentReservations.remove(reservationToDelete)
    } //end cancelReservation
}