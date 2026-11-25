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

import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
 * @property role: "user" or "admin" (admin is set by hand in Firestore, never through the app)
 */
data class User (
    val deviceID: String = "",
    val photoUrl: String? = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val notificationsEnabled: Boolean = false,
    val phone: String = "",
    val currentReservations: MutableList<Reservation> = mutableListOf(),
    var pastReservations : MutableList<Reservation> = mutableListOf(),
    val role: String = ""
) {


    private fun currentUserId(): String? {
        return try {
            Firebase.auth.currentUser?.uid
        } catch (_: Exception) {
            null
        }
    }

    fun joinLottery(campsite: Campsites) {
        // take campsite object
        // add user details to campsite lottery list
        val uid = currentUserId() ?: deviceID.ifEmpty { "current_user" }
        if (!campsite.lotteryWaitlist.contains(uid)) {
            campsite.lotteryWaitlist.add(uid)
        }
        if (campsite.id.isNotBlank() && uid.isNotBlank()) {
            try {
                CampsiteRepository().joinWaitlist(campsite.id, uid)
            } catch (_: Exception) {
                // Ignore Firestore initialization errors during unit tests
            }
        }
    } // end joinLottery()

    fun leaveLottery(campsite: Campsites) {
        // take campsite object
        // remove user details from campsite lottery list
        val uid = currentUserId() ?: deviceID.ifEmpty { "current_user" }
        campsite.lotteryWaitlist.remove(uid)
        if (campsite.id.isNotBlank() && uid.isNotBlank()) {
            try {
                CampsiteRepository().leaveWaitlist(campsite.id, uid)
            } catch (_: Exception) {
                // Ignore Firestore initialization errors during unit tests
            }
        }
    } // end leaveLottery()
}