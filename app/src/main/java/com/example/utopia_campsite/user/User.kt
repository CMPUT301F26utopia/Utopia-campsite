/**
 * This is a User class
 * This model stores information about a user including its deviceID,
 *  device ID, email, phoneNumber, photoURL, etc
 *
 * This model does not deal with interacting with the Firebase database
 *
 * Outstanding Issues:
 * More fields may be required as more user stories are implemented
 */
package com.example.utopia_campsite.user

import com.example.utopia_campsite.owner.Campsites

/**
 *Stores details about a user
 * @property deviceID: unique device id
 * @property email: email address
 * @property firstName: first name
 * @property lastName: last name
 * @property notificationsEnabled: whether notifications are enabled on the user's device
 * @property phone: phone number
 * @property currentReservations: stores a list of current reservations
 * @property pastReservations: stores a list of past reservations
 */
data class User (
    val deviceID: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val notificationsEnabled: Boolean,
    val phone: String,
    val currentReservations: MutableList<Reservation>,
    val pastReservations : List<Reservation>

    ) {
    fun joinLottery(campsite: Campsites) {
        // take campsite object
        // add user details to campsite lottery list
        campsite.lotteryWaitlist.add(deviceID)
    } // end joinLottery()

    fun leaveLottery(campsite: Campsites) {
        // take campsite object
        // remove user details from campsite lottery list
        campsite.lotteryWaitlist.remove(deviceID)
    } // end leaveLottery()

    fun cancelReservation(currentReservations: MutableList<Reservation>, reservationToDelete: Reservation) {
        //take a reservation object to delete and the list of currentReservation
        //remove the object in currentReservations
        currentReservations.remove(reservationToDelete)
    } //end cancelReservation
}