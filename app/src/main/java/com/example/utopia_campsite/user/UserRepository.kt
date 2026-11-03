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

    fun deleteCurrentReservation(userID: String, reservation: Reservation) {
        //to be implemented
    }


    /**
     * find the start and end date of the reservation the user booked and return it
     * as a string
     * @param user: the user object
     * @param campsiteName: the name of the campsite that the user reserved
     */
    fun getReservationDate(user: User, campsiteName: String) : String {
        for (reservation in user.currentReservations ) {
            if (reservation.campsiteName == campsiteName) {
                return "Reserved Date: ${reservation.startReservedDate} to ${reservation.endReservedDate}"
            }
        }
        return "gay"
    }

    /**
     * Updates a user's editable profile fields in firebase
     * @param user: the updated user (user.deviceID is the firebase document ID)
     * @param onSuccess: called when the update has been saved
     * @param onFailure: called when there is an error saving the update
     */
    fun updateUser(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updates = mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "email" to user.email,
            "phone" to user.phone,
            "photoUrl" to user.photoUrl,
            "notificationsEnabled" to user.notificationsEnabled
        )

        db.collection("users")
            .document(user.deviceID)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    } //end updateUser
}//end user repository