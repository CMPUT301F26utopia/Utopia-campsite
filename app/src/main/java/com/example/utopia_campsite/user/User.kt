package com.example.utopia_campsite.user

/**
 *Stores details about a user
 *
 * @property deviceID: unique device id
 * @property email: email address
 * @property firstName: first name
 * @property lastName: last name
 * @property notificationsEnabled: whether notifications are enabled on the user's device
 * @property phone: phone number
 *
 */
data class User (
    val deviceID: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val notificationsEnabled: Boolean,
    val phone: String,
    val currentReservations: List<Reservation>,
    val pastReservations : List<Reservation>

    ){
    fun joinLottery(campsite: String, user: User) {
        // take campsite object
        // add user details to campsite lottery list
        // logic for whether it is an active lottery or a waitlist for a lottery will be worked out within function rather than doing multiple different functions for minor differences in functionality
    }

    fun leaveLottery(campsite: String, user: User) {
        // take campsite object
        // remove user details from campsite lottery list
    }

}