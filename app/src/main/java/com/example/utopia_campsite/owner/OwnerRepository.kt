package com.example.utopia_campsite.owner


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
}