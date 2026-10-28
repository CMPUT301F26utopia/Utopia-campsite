/**
 * This is a campsite class
 * This model stores information about a campsite including its ID,
 * name, description, photoURL, etc
 *
 * This model does not deal with interacting with the Firebase database,
 * that is the responsibility of CampsiteController.kt
 *
 *Outstanding Issues:
 * More fields may be required as more user stories are implemented
 */

package com.example.utopia_campsite.owner

import com.example.utopia_campsite.user.User

/**
 *Stores details about a campsite
 *
 * @property id: the unique Firestore document ID of the campsite
 * @property name: the name of the campsite
 * @property photoUrl: the URL of the campsite's photo, or null if campsite owner does not provide a photo
 * @property address: the address of the campsite
 * @property latitude: the latitude of the campsite
 * @property longitude: the longitude of the campsite
 * @property ratings: a list of ratings for the campsite
 * @property tags: a list of tags for the campsite
 * @property lotteryWinners: the number of lottery winners for the campsite
 * @property lotteryWaitlist: number of people in a waitlist for the campsite lottery
 * @property description: a description of the campsite
 * @property registrationDate: the days where a user can sign up for the lottery
 * @property bookingDate: the days where a lottery winner can accept book their reservation
 * @property maximumOccupants: the maximum number of occupants for the campsite
 * @property availableStartDate: the starting date of which the campsite is available to be booked
 * @property availableEndDate: the ending date of which the campsite is available to be booked
 */
data class Campsites(
    val id: String,
    val name: String,
    val photoUrl: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val ratings: MutableList<Int>,
    val tags: MutableList<String>,
    val lotteryWinners: Int,
    val lotteryWaitlist: MutableList<String>,
    val description: String,
    val registrationDate: String,   // (start date-end date) -> eg. 10/10/2026-20/10/2026
    val bookingDate: String,        // (start date-end date) -> eg. 10/10/2026-20/10/2026
    val maximumOccupants: Int,
    val availableStartDate: String, // Added on, this is the starting date of which the campsite is available
    val availableEndDate: String    // Added on, this is the ending date of which the campsite is available
)
