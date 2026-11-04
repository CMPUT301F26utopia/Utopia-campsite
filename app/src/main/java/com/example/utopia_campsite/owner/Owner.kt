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
 * @property postedCampsitesId: list of unique ids of campsites the owner posted
 * @property deviceID: unique device id
 * @property photoUrl: image of the owner
 * @property email: email address
 * @property firstName: first name
 * @property lastName: last name
 * @property notificationsEnabled: whether notifications are enabled on the user's device
 * @property phone: phone number
 * @property policyViolated: shows if the owner violated the app policy
 */
data class Owner (
     val postedCampsitesId: MutableList<String>,
     val deviceID: String,
     val photoUrl: String?,
     val email: String,
     val firstName: String,
     val lastName: String,
     val notificationsEnabled: Boolean,
     val phone: String,
     val policyViolated: Boolean
) {
}