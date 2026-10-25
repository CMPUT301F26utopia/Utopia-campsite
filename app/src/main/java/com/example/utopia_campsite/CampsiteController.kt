/**
 * The controller retrieves documents from the firebase firestore
 *
 *Outstanding Issues:
 * None
 */

package com.example.utopia_campsite

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore


/**
 *
 * Retrieves documents from the firebase firestore
 */
class CampsiteController{

    val db = FirebaseFirestore.getInstance()
    /**
     * Retrieves all campsites from the firebase firestore
     * Each document is converted into a Campsite object and added to a list
     * The list is then passed to the onSuccess callback
     *
     * @param onSuccess: a callback function that is called when the campsites are retrieved
     * @param onFailure: a callback function that is called when there is an error retrieving the campsites
     */
    fun getCampsites(
        onSuccess: (List<Campsite>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("campsites")
            .get()
            .addOnSuccessListener { result ->
                val campsites = mutableStateListOf<Campsite>()
                for (document in result.documents) {
                    val campsite = Campsite(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        photoUrl = document.getString("photoURL"),


                    )
                    campsites.add(campsite)
                }//end for
                onSuccess(campsites)

    }//end addOnSuccessListener
            .addOnFailureListener { exception ->
                onFailure(exception)
            }//end addOnFailureListener

    }//end getCampsites

}//end campsite controller
