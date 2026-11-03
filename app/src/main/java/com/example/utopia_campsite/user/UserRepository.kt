package com.example.utopia_campsite.user

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.toMutableStateList

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    /**
     * fetches all users from firebase
     *
     * @param onSuccess: a callback function that is called when the users are retrieved
     * @param onFailure: a callback function that is called when there is an error retrieving the users
     */
    fun getUsers(
        onSuccess: (List<User>) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        db.collection("users")
            .get().addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { document ->
                    document.toObject(User::class.java)?.copy(deviceID = document.id)
                }.toMutableStateList()
                onSuccess(users)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }//end onFailureListener
    }//end getUsers


    /**
     * fetches one user from firebase
     * @param userId: the firebase document ID of the user (their deviceID)
     * @param onSuccess: a callback function that is called when the user is retrieved
     * @param onFailure: a callback function that is called when there is an error retrieving the user
     */
    fun getUser(
        userId: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ){

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)?.copy(deviceID = document.id)
                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure(Exception("User $userId does not exist"))
                }
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }//end onFailureListener
    }//end getUser


    /**
     * Adds a reservation to a user's currentReservations in the firebase
     * @param userId: the firebase document ID of the user (their deviceID)
     * @param reservation: the reservation to add
     */
    fun addCurrentReservation(userId: String, reservation: Reservation){
        db.collection("users")
            .document(userId)
            .update("currentReservations", FieldValue.arrayUnion(reservation))

    }//end add current reservation


    /**
     * Deletes a user's reservation in the firebase
     * @param userId: the firebase document ID of the user (their deviceID)
     * @param reservationId: the reservation id of the reservation
     * @param onSuccess: callback function that is called if the reservation is successfully deleted
     * @param onFailure:  callback function that is called if the reservation is not deleted successfully
     */

    fun deleteCurrentReservation(
        userId: String,
        reservationId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDoc = db.collection("users").document(userId)

        userDoc.get()
            .addOnSuccessListener { snapshot ->

                val array = snapshot.get("currentReservations") as?  List<Map<String, Any?>>  //get the array field

                if (array == null) {
                    onFailure(IllegalStateException("reservations field missing or wrong type"))
                    return@addOnSuccessListener
                }

                val target = array.firstOrNull { it["reservationId"] == reservationId } //Find the map whose "reservationId" matches

                if (target == null) {
                    onFailure(NoSuchElementException("Reservation $reservationId not found"))
                    return@addOnSuccessListener
                }
                //remove the map which is the reservation in firebase
                userDoc.update("currentReservations", FieldValue.arrayRemove(target))
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    } //end deleteCurrentReservation
}//end user repository