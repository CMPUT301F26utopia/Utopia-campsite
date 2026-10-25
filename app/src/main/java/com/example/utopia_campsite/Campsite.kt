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

package com.example.utopia_campsite

/**
 *Stores details about a campsite
 *
 * @property id: the unique Firestore document ID of the campsite
 * @property name: the name of the campsite
 * @property description: a description of the campsite
 * @property photoUrl: the URL of the campsite's photo, or null if campsite owner does not provide a photo
 */
data class Campsite(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val photoUrl: String? = null
)