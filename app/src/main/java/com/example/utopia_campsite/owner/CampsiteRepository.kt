/**
 * The controller retrieves documents from the firebase firestore
 *
 *Outstanding Issues:
 * None
 */
package com.example.utopia_campsite.owner

import androidx.compose.runtime.mutableStateListOf
import com.example.utopia_campsite.user.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.toMutableList
import com.example.utopia_campsite.user.UserRepository
import com.google.firebase.auth.FirebaseAuth

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
                        ratings = (document.get("ratings") as? Map<*, *>) ?.mapNotNull { (key, value) ->
                                if (key is String && value is Number) {
                                    key to value.toInt()
                                } else null
                            }
                            ?.toMap()
                            ?: emptyMap(), //this casts firebase's List<Any?> to a List<Int>
                        tags = (document.get("tags") as? List<*>)?.filterIsInstance<String>()?: mutableListOf(), //this casts firebase's List<Any?> to a List<String>
                        lotteryWinners = document.getLong("lotteryWinners")?.toInt() ?: 0,
                        waitlistCapacity = document.getLong("waitlistCapacity")?.toInt(),
                        lotteryWaitlist = getUserIdsFromDocument(document, "lotteryWaitlist"),
                        description = document.getString("description") ?: "",
                        registrationDate = document.getString("registrationDate") ?: "",
                        bookingDate = document.getString("bookingDate") ?: "",
                        maximumOccupants = document.getLong("maxOccupancy")?.toInt() ?: 0,
                        availableStartDate = document.getString("availableStartDate") ?: "",
                        availableEndDate = document.getString("availableEndDate") ?: "",
                        usersBooked = getUserIdsFromDocument(document, "usersBooked"),
                        reviews = (document.get("reviews") as? List<*>)?.filterIsInstance<String>() ?: emptyList() //this casts firebase's List<Any?> to a List<String>
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
            "waitlistCapacity" to campsite.waitlistCapacity,
            "lotteryWaitlist" to campsite.lotteryWaitlist.map { db.collection("users").document(it) },
            "description" to campsite.description,
            "registrationDate" to campsite.registrationDate,
            "bookingDate" to campsite.bookingDate,
            "maxOccupancy" to campsite.maximumOccupants,
            "availableStartDate" to campsite.availableStartDate,
            "availableEndDate" to campsite.availableEndDate,
            "usersBooked" to campsite.usersBooked.map{db.collection("users").document(it)},
            "reviews" to campsite.reviews
        )
        db.collection("campsites").add(data)
            .addOnSuccessListener { campsiteDocument ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (uid == null) {
                    onFailure(Exception("No owner signed in"))
                    return@addOnSuccessListener
                }
                db.collection("owners")
                    .document(uid)
                    .update(
                        "postedCampsitesId",
                        FieldValue.arrayUnion(campsiteDocument.id)
                    )
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    } //end addCampsite

    /**
     * Adds a user to a campsite's lottery waitlist in Firebase Firestore
     * @param campsiteId: campsite firestore id
     * @param userId: user firestore id
     * @param onSuccess: callback function called when updated
     * @param onFailure: callback function called on error
     */
    fun joinWaitlist(
        campsiteId: String,
        userId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        if (campsiteId.isBlank() || userId.isBlank()) return
        try {
            val userRef = db.collection("users").document(userId)
            db.collection("campsites").document(campsiteId)
                .update("lotteryWaitlist", FieldValue.arrayUnion(userRef))
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    /**
     * Removes a user from a campsite's lottery waitlist in Firebase Firestore
     * @param campsiteId: campsite firestore id
     * @param userId: user firestore id
     * @param onSuccess: callback function called when updated
     * @param onFailure: callback function called on error
     */
    fun leaveWaitlist(
        campsiteId: String,
        userId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        if (campsiteId.isBlank() || userId.isBlank()) return
        try {
            val userRef = db.collection("users").document(userId)
            db.collection("campsites").document(campsiteId)
                .update("lotteryWaitlist", FieldValue.arrayRemove(userRef))
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

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

    /**
     * Removes campsite's photo in the firebase
     * Sets photoUrl to null
     *
     * @param campsiteId: the Firestore document ID of the campsite
     * @param onSuccess: a callback function that is called when the photo is removed
     * @param onFailure: a callback function that is called when there is an error removing the photo
     */
    fun removeCampsitePhoto(
        campsiteId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        db.collection("campsites").document(campsiteId).update("photoUrl", null)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    } //end removeCampsitePhoto

    /**
     * Deletes a campsite and removes its current and past reservations from every user (admin)
     * Everything happens in one batch, so either all of it is deleted or none of it is
     *
     * @param campsite: the campsite to delete
     * @param onSuccess: called once the campsite and its reservations are deleted
     * @param onFailure: called if anything fails, nothing is deleted in that case
     */
    fun deleteCampsite(
        campsite: Campsites,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ){
        // past reservations belong to users who are no longer in usersBooked, so check every user
        db.collection("users").get()
            .addOnSuccessListener { users ->
                val batch = db.batch()
                for (user in users.documents) {
                    val updates = mutableMapOf<String, Any>()
                    for (field in listOf("currentReservations", "pastReservations")) {
                        val reservations = user.get(field) as? List<Map<String, Any?>> ?: emptyList()
                        // matching by campsiteName for now, change to an id match if Reservation has a campsiteId
                        val toRemove = reservations.filter { it["campsiteName"] == campsite.name }
                        if (toRemove.isNotEmpty()) {
                            updates[field] = FieldValue.arrayRemove(*toRemove.toTypedArray())
                        } //end if
                    } //end for
                    if (updates.isNotEmpty()) {
                        batch.update(user.reference, updates)
                    } //end if
                } //end for
                batch.delete(db.collection("campsites").document(campsite.id)) // then the campsite itself
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    } //end deleteCampsite

    /**
     * Retrieves all the campsites that are tied to the owner. method calls the getCampsites method,
     * but filters internally
     *
     * @param owner: the campsite owner to retrieve the campsites from
     * @param onSuccess: called once the filtered campsites have been successfully retrieved
     * @param onFailure: called if anything fails, nothing changes
     */
    fun getOwnerCampsites(
        owner: Owner,
        onSuccess: (List<Campsites>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        getCampsites(
            onSuccess = { allCampsites ->
                val ownerCampsites = allCampsites.filter { campsite ->
                    campsite.id in owner.postedCampsitesId
                }

                onSuccess(ownerCampsites)
            },
            onFailure = onFailure
        )
    } //end getOwnerCampsites
    /**
     * Adds a review to a campsite in the firebase firestore (entrant posts a review)
     * Looks up the signed in user's name with getUser, then saves the review as one string, e.g. "John Doe: Great spot!"
     * arrayUnion appends to the reviews array without overwriting the other reviews
     * Note: arrayUnion skips an exact duplicate string, so the same person posting the same text twice is only saved once
     *
     * @param campsiteId: the Firestore document ID of the campsite
     * @param reviewText: the text the user wrote, without their name
     * @param onSuccess: called with the full saved review once it is saved
     * @param onFailure: called if the user or the review could not be loaded/saved
     */
    fun addReview(
        campsiteId: String,
        reviewText: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onFailure(Exception("No user is signed in"))
            return
        } //end if

        UserRepository().getUser(
            userId = uid,
            onSuccess = { user ->
                val review = "${user.firstName} ${user.lastName}: $reviewText"
                db.collection("campsites").document(campsiteId)
                    .update("reviews", FieldValue.arrayUnion(review))
                    .addOnSuccessListener { onSuccess(review) }
                    .addOnFailureListener { onFailure(it) }
            },
            onFailure = onFailure
        )
    } //end addReview
    /**
     * Removes one review from a campsite in the firebase firestore (admin)
     * arrayRemove deletes every array entry equal to the given string, so the review must match exactly
     * what is stored, e.g. "John Doe: Great spot!"
     *
     * @param campsiteId: the Firestore document ID of the campsite
     * @param review: the full review string to remove, name included
     * @param onSuccess: called once the review is removed
     * @param onFailure: called if the review could not be removed
     */
    fun removeReview(
        campsiteId: String,
        review: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit
    ){
        db.collection("campsites").document(campsiteId)
            .update("reviews", FieldValue.arrayRemove(review))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }//end removeReview
    /**
     * Saves the fields edited on the owner campsite details screen.
     *
     * Other fields, such as the lottery waitlist and reviews, are preserved.
     */
    fun updateCampsiteDetails(
        campsite: Campsites,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("campsites")
            .document(campsite.id)
            .update(
                mapOf(
                    "name" to campsite.name,
                    "address" to campsite.address,
                    "description" to campsite.description,
                    "maxOccupancy" to campsite.maximumOccupants,
                    "tags" to campsite.tags,
                    "latitude" to campsite.latitude,
                    "longitude" to campsite.longitude
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Adds the user specified rating on the specific campsite, in its current implementation, there isnt any specification on which user gave which ratings
     *
     * */
    fun addRating(
        campsiteId: String,
        rating: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            onFailure(Exception("No user signed in"))
            return
        }

        if (rating !in 1..5) {
            onFailure(IllegalArgumentException("Rating must be between 1 and 5"))
            return
        }
        val campsiteReference = db.collection("campsites").document(campsiteId)
        db.runTransaction { transaction ->
            val campsite = transaction.get(campsiteReference)

            if (!campsite.exists()) {
                throw Exception("Campsite not found")
            }

            // Retrieve existing ratings and handling edge cases for retrieval
            val ratings = (campsite.get("ratings") as? Map<*, *>)
                ?.mapNotNull { (key, value) ->
                    if (key is String && value is Number) {
                        key to value.toInt()
                    } else null
                }
                ?.toMap()
                ?.toMutableMap()
                ?: mutableMapOf()

            ratings[uid] = rating
            transaction.update(campsiteReference, "ratings", ratings)

            null
        }
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }//end addRating
    /**
     * Removes one user's rating from a campsite in the firebase firestore (admin)
     * Runs in a transaction, like addRating, so it reads the current ratings and writes them back
     * without that user's entry, without overwriting a rating someone else adds at the same time
     *
     * @param campsiteId: the Firestore document ID of the campsite
     * @param userId: the uid of the user whose rating is removed (the key in the ratings map)
     * @param onSuccess: called once the rating is removed
     * @param onFailure: called if the campsite or rating doesn't exist, or the rating could not be removed
     */
    fun removeRating(
        campsiteId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val campsiteReference = db.collection("campsites").document(campsiteId)
        db.runTransaction { transaction ->
            val campsite = transaction.get(campsiteReference)

            if (!campsite.exists()) {
                throw Exception("Campsite not found")
            }

            // Retrieve existing ratings and handling edge cases for retrieval
            val ratings = (campsite.get("ratings") as? Map<*, *>)
                ?.mapNotNull { (key, value) ->
                    if (key is String && value is Number) {
                        key to value.toInt()
                    } else null
                }
                ?.toMap()
                ?.toMutableMap()
                ?: mutableMapOf()

            if (ratings.remove(userId) == null) {
                throw Exception("Rating not found")
            }

            transaction.update(campsiteReference, "ratings", ratings)

            null
        }
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    } //end removeRating
}//end campsite controller