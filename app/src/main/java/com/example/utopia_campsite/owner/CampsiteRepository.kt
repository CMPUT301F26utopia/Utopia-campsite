package com.example.utopia_campsite.owner

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore

/**
 * The controller handles receiving documents from and adding documents to the firestore campsites database.
 *
 *Outstanding Issues:
 * None
 */
/**
 * Adds documents to the firebase firestore
 * Retrieves documents from the firebase firestore
 */
class CampsiteRepository{

    private val db = FirebaseFirestore.getInstance()
    /**
     * Retrieves all campsites from the firebase firestore
     * Each document is converted into a Campsite object and added to a list
     * The list is then passed to the onSuccess callback
     *
     * @param onSuccess: a callback function that is called when the campsites are retrieved
     * @param onFailure: a callback function that is called when there is an error retrieving the campsites
     */
    fun getCampsites(
        onSuccess: (List<Campsites>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("campsites")
            .get()
            .addOnSuccessListener { result ->
                val campsites = mutableStateListOf<Campsites>()
                for (document in result.documents) {
                    val campsite = Campsites(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        photoUrl = document.getString("photoURL"),
                        address = document.getString("address") ?: "", //the ? sets a default value if the value is null
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        ratings = (document.get("ratings") as List<*>).map { (it as Number).toInt() }, //this casts firebase's List<Any?> to a List<Int>
                        tags = (document.get("tags") as List<*>).filterIsInstance<String>(), //this casts firebase's List<Any?> to a List<String>
                        lotteryWinners = document.getLong("lotteryWinners")?.toInt() ?: 0,
                        lotteryWaitlist = document.getLong("lotteryWaitlist")?.toInt(),
                        description = document.getString("description") ?: "",
                        registrationDate = document.getString("registrationDate") ?: "",
                        bookingDate = document.getString("bookingDate") ?: "",
                        maximumOccupants = document.getLong("maxOccupancy")?.toInt() ?: 0,
                        availableStartDate = document.getString("availableStartDate") ?: "",
                        availableEndDate = document.getString("availableEndDate") ?: ""
                    )
                    campsites.add(campsite)
                }//end for
                onSuccess(campsites)

    }//end addOnSuccessListener
            .addOnFailureListener { exception ->
                onFailure(exception)
            }//end addOnFailureListener

    }//end getCampsites

    /**
     * saves a new campsite to the firebase firestore.
     * Firestore generates the document ID.
     *
     * @param campsite: the campsite being saved.
     * @param onSuccess: called with the new document ID when the campsite is saved.
     * @param onFailure: called when saving a new campsite fails.
     */
    fun registerCampsite(
        campsite: Campsites,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
     ){
        val data = hashMapOf(
            "name" to campsite.name,
            "photoURL" to campsite.photoUrl,
            "address" to campsite.address,
            "latitude" to campsite.latitude,
            "longitude" to campsite.longitude,
            "ratings" to campsite.ratings,
            "tags" to campsite.tags,
            "lotteryWinners" to campsite.lotteryWinners,
            "lotteryWaitlist" to campsite.lotteryWaitlist,
            "description" to campsite.description,
            "registrationDate" to campsite.registrationDate,
            "bookingDate" to campsite.bookingDate,
            "maxOccupancy" to campsite.maximumOccupants,
            "availableStartDate" to campsite.availableStartDate,
            "availableEndDate" to campsite.availableEndDate
        )
        db.collection("campsites")
            .add(data)
            .addOnSuccessListener { ref -> onSuccess(ref.id) }
            .addOnFailureListener { exception -> onFailure(exception) }
    }//end registerCampsite

}//end campsite controller