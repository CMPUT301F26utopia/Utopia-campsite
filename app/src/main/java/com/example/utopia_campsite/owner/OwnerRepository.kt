package com.example.utopia_campsite.owner


import com.example.utopia_campsite.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class OwnerRepository {
    private val db = FirebaseFirestore.getInstance()


    /**
     * fetches one owner from firebase
     * @param ownerId: the firebase document ID of the owner (their deviceID)
     * @param onSuccess: a callback function that is called when the user is retrieved
     * @param onFailure: a callback function that is called when there is an error retrieving the user
     */
    fun getOwner(
        ownerId: String,
        onSuccess: (Owner) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        db.collection("owners")
            .document(ownerId)
            .get()
            .addOnSuccessListener { document ->
                val owner = document.toObject(Owner::class.java)?.copy(deviceID = document.id)
                if (owner != null) {
                    onSuccess(owner)
                } else {
                    onFailure(Exception("Owner $ownerId does not exist"))
                }
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }//end onFailureListener
    }//end getOwner


    /**
     * cancels a user's booking at a campsite in firebase
     * and sends the user a cancellation notification once it is saved
     * @param userId: the firebase document ID of the user (their deviceID)
     * @param campsite: the campsite where the user is booked to
     */

    fun cancelUserReservation(userId: String, campsite: Campsites) {
        db.collection("campsites")
            .document(campsite.id)
            .update("usersBooked", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                // send() needs the whole user to skip anyone who turned notifications off
                UserRepository().getUser(
                    userId,
                    onSuccess = { user ->
                        NotificationRepository().send(
                            ownerId = FirebaseAuth.getInstance().currentUser?.uid ?: "", // the signed in owner
                            campsiteId = campsite.id,
                            title = "Reservation cancelled",
                            body = "The owner cancelled your reservation at ${campsite.name}.",
                            recipients = listOf(user)
                        )
                    },
                    onFailure = { } // the user's profile was deleted, so there is nobody to notify
                )
            }
    }

}