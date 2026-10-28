package com.example.utopia_campsite.user

import com.example.utopia_campsite.owner.Campsites

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

){
    fun joinLottery(campsite: Campsites) {
        // take campsite object
        // add user details to campsite lottery list
        campsite.lotteryWaitlist.add(deviceID)
    }

    fun leaveLottery(campsite: Campsites) {
        // take campsite object
        // remove user details from campsite lottery list
        campsite.lotteryWaitlist.remove(deviceID)
    }

}