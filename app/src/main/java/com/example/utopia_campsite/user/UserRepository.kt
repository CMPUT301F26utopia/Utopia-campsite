package com.example.utopia_campsite.user

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.toMutableStateList
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.QuerySnapshot

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
 


    /**
     * find the start and end date of the reservation the user booked and return it
     * as a string
     * @param user: the user object
     * @param campsiteName: the name of the campsite that the user reserved
     */
    /*
    fun getReservationDate(user: User, campsiteName: String) : String {
        for (reservation in user.currentReservations ) {
            if (reservation.reservationId == campsiteName) {
                return "Reserved Date: ${reservation.startReservedDate} to ${reservation.endReservedDate}"
            }
        }
        return "gay"
    }*/

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

    /**
     * Deletes a user's profile and everything tied to it in the firebase
     * Removes the user from every campsite's lottery waitlist and booked users,
     * then deletes the user document (which also deletes their current and past reservations)
     * Everything happens in one batch, so either all of it is deleted or none of it is
     *
     * @param userId: the firebase document ID of the user (their deviceID)
     * @param onSuccess: called once the user and everything tied to them is deleted
     * @param onFailure: called if anything fails, nothing is deleted in that case
     */
    fun deleteUser(
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userRef = db.collection("users").document(userId)

        // campsites store users as document references, so search for the reference
        val waitlisted = db.collection("campsites").whereArrayContains("lotteryWaitlist", userRef).get()
        val booked = db.collection("campsites").whereArrayContains("usersBooked", userRef).get()

        Tasks.whenAllSuccess<QuerySnapshot>(waitlisted, booked)
            .addOnSuccessListener { results ->
                val waitlistedCampsites = results[0].documents
                val bookedCampsites = results[1].documents
                val batch = db.batch()
                for (campsite in waitlistedCampsites) {
                    batch.update(campsite.reference, "lotteryWaitlist", FieldValue.arrayRemove(userRef))
                } //end for
                for (campsite in bookedCampsites) {
                    batch.update(campsite.reference, "usersBooked", FieldValue.arrayRemove(userRef))
                } //end for
                batch.delete(userRef) // then the user itself
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    } //end deleteUser
}//end user repository