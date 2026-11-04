/**
 * The controller retrieves documents from the firebase firestore
 *
 *Outstanding Issues:
 * None
 */
package com.example.utopia_campsite.owner

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

/**
 *
 * Retrieves documents from the firebase firestore
 */
class CampsiteRepository{

    private val db = FirebaseFirestore.getInstance()

    /**
     * Pulls the document reference from an array field
     * @param campsiteDocument: the document containing a specific campsite
     * @param fieldName: the name of the array field (eg LotteryWaitlist)
     */
    private fun getReferencesFromDocument(campsiteDocument: DocumentSnapshot, fieldName: String): List<DocumentReference>{
        return (campsiteDocument.get(fieldName) as? List<*>)?.filterIsInstance<DocumentReference>() ?: emptyList()

    }//end getReferencesFromDocument

    /**
     * Pulls the user IDs from an array field of user references
     * @param campsiteDocument: the document containing a specific campsite
     * @param fieldName: the name of the array field (eg lotteryWaitlist)
     */
    private fun getUserIdsFromDocument(campsiteDocument: DocumentSnapshot, fieldName: String): MutableList<String> {
        return getReferencesFromDocument(campsiteDocument, fieldName).map { it.id }.toMutableList<String>()
    }//end getUserIdsFromDocument

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
                        photoUrl = document.getString("photoUrl"),
                        address = document.getString("address") ?: "", //the ? sets a default value if the value is null
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        ratings = (document.get("ratings") as? List<*>)?.map { (it as Number).toInt() }?: mutableListOf(), //this casts firebase's List<Any?> to a List<Int>
                        tags = (document.get("tags") as? List<*>)?.filterIsInstance<String>()?: mutableListOf(), //this casts firebase's List<Any?> to a List<String>
                        lotteryWinners = document.getLong("lotteryWinners")?.toInt() ?: 0,
                        lotteryWaitlist = getUserIdsFromDocument(document, "lotteryWaitlist"),
                        description = document.getString("description") ?: "",
                        registrationDate = document.getString("registrationDate") ?: "",
                        bookingDate = document.getString("bookingDate") ?: "",
                        maximumOccupants = document.getLong("maxOccupancy")?.toInt() ?: 0,
                        availableStartDate = document.getString("availableStartDate") ?: "",
                        availableEndDate = document.getString("availableEndDate") ?: "",
                        usersBooked = getUserIdsFromDocument(document, "usersBooked")
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
     * fetches one campsite from firebase
     * @param campsiteId: the firebase document ID of the campsite (campsite id)
     * @param onSuccess: a callback function that is called when the user is retrieved
     * @param onFailure: a callback function that is called when there is an error retrieving the user
     */
    fun getCampsite(
        campsiteId: String,
        onSuccess: (Campsites) -> Unit,
        onFailure: (Exception) -> Unit
    ){

        db.collection("campsites")
            .document(campsiteId)
            .get()
            .addOnSuccessListener { document ->
                val campsite = document.toObject(Campsites::class.java)?.copy(id = document.id)
                if (campsite != null) {
                    onSuccess(campsite)
                } else {
                    onFailure(Exception("Campsite identifier $campsiteId does not exist"))
                }
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }//end onFailureListener
    }//end getCampsite

    /**
     * Saves a new campsite to the firebase firestore (US 02.01.02)
     * Firestore makes the document ID, so campsite.id is not saved
     * The field names match the ones getCampsites reads
     *
     * @param campsite: the campsite to save
     */
    fun addCampsite(
        campsite: Campsites,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ){
        val data = hashMapOf(
            "name" to campsite.name,
            "photoUrl" to campsite.photoUrl,
            "address" to campsite.address,
            "latitude" to campsite.latitude,
            "longitude" to campsite.longitude,
            "ratings" to campsite.ratings,
            "tags" to campsite.tags,
            "lotteryWinners" to campsite.lotteryWinners,
            "lotteryWaitlist" to campsite.lotteryWaitlist.map { db.collection("users").document(it) },
            "description" to campsite.description,
            "registrationDate" to campsite.registrationDate,
            "bookingDate" to campsite.bookingDate,
            "maxOccupancy" to campsite.maximumOccupants,
            "availableStartDate" to campsite.availableStartDate,
            "availableEndDate" to campsite.availableEndDate,
            "usersBooked" to campsite.usersBooked.map{db.collection("users").document(it)}
        )
        db.collection("campsites").add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    } //end addCampsite

    /**
     * Saves a campsite's lottery registration period to the firebase firestore (US 02.04.01, 02.04.03)
     * Only the registrationDate field changes; the rest of the campsite stays the same
     *
     * @param campsiteId: the Firestore document ID of the campsite
     * @param registrationDate: the registration period, e.g. "10/10/2026-20/10/2026"
     */
    fun updateRegistrationPeriod(campsiteId: String, registrationDate: String){
        db.collection("campsites").document(campsiteId).update("registrationDate", registrationDate)
    } //end updateRegistrationPeriod

    data class CampsiteImage(
        val campsiteId: String,
        val campsiteName: String,
        val photoUrl: String
    )

    /**
     * Retrieves every campsite that has a photo.
     *
     * @param onSuccess: called with one CampsiteImage per campsite that has a photoUrl
     * @param onFailure: called when the query fails
     */
    fun getCampsiteImages(
        onSuccess: (List<CampsiteImage>) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        db.collection("campsites").get().addOnSuccessListener {
            result ->
                    val image = result.documents.mapNotNull {
                        doc ->
                        val url = doc.getString("photoUrl")
                        if (url.isNullOrBlank()) null
                        else CampsiteImage(doc.id, doc.getString("name")?: "",url)
                    }
            onSuccess(image)
        }
            .addOnFailureListener { onFailure(it) }
    }
    //End getCampsiteImages

    fun deleteCampsiteImage(
        campsiteId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        db.collection("campsites").document(campsiteId)
            .update("photoUrl",null)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

}//end campsite controller