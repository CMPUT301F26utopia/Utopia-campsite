package com.example.utopia_campsite.user

import com.example.utopia_campsite.owner.MultipleLotteryDates
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

/** Checks inclusive dates against the booking period and validates guest count. */
fun validateMultipleLotteryChoice(start: String, end: String, bookingPeriod: String, guests: Int, capacity: Long): String? {
    if (!MultipleLotteryDates.isValidPeriod(start, end)) return "Enter valid dates in DD/MM/YYYY format."
    val period = bookingPeriod.split("-")
    if (period.size != 2 || !MultipleLotteryDates.isValidPeriod(period[0].trim(), start) ||
        !MultipleLotteryDates.isValidPeriod(end, period[1].trim())) return "Choose dates within the booking period."
    if (guests <= 0 || guests.toLong() > capacity) return "Enter a guest count within the campsite capacity."
    return null
}

/**
 * Saves a winner's choice for a two-campsite lottery in one Firestore transaction.
 *
 * This demonstration uses the selected campsite's `combinedReservations` array as
 * the shared occupancy record. Only reservations made through this flow participate;
 * use new test campsites without pre-existing reservations.
 *
 * @param lotteryId ID of the combined lottery document.
 * @param campsiteId ID of the selected original campsite.
 * @param start First reserved day, in DD/MM/YYYY format.
 * @param end Last reserved day, in DD/MM/YYYY format; dates are inclusive.
 * @param guests Number of guests, between one and the campsite capacity.
 * @param onSuccess Called after the reservation is committed.
 * @param onFailure Called if validation, eligibility, or saving fails.
 */
fun reserveMultipleLotteryCampsite(
    lotteryId: String,
    campsiteId: String,
    start: String,
    end: String,
    guests: Int,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    if (uid.isNullOrBlank() || lotteryId.isBlank() || campsiteId.isBlank() ||
        !MultipleLotteryDates.isValidPeriod(start, end) || guests <= 0) {
        onFailure(IllegalArgumentException("Please check your selection, dates and guest count."))
        return
    }
    val db = FirebaseFirestore.getInstance()
    val lotteryRef = db.collection("campsites").document(lotteryId)
    val campsiteRef = db.collection("campsites").document(campsiteId)
    val userRef = db.collection("users").document(uid)
    val reservationId = UUID.randomUUID().toString()

    db.runTransaction { tx ->
        val lottery = tx.get(lotteryRef)
        val campsite = tx.get(campsiteRef)
        val user = tx.get(userRef)
        require(lottery.exists() && campsite.exists() && user.exists()) { "Lottery, campsite or user not found." }
        require(lottery.getBoolean("isCombinedLottery") == true && lottery.getBoolean("lotteryDrawn") == true) { "This lottery is not ready." }
        val winners = (lottery.get("lotteryWinnerList") as? List<*>)?.filterIsInstance<DocumentReference>() ?: emptyList()
        require(userRef in winners) { "You no longer have an active winning offer." }
        val campsiteIds = (lottery.get("campsiteIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        require(campsiteIds.size == 2 && campsiteIds.distinct().size == 2 && campsiteId in campsiteIds) { "Choose a campsite in this lottery." }
        val capacity = campsite.getLong("maxOccupancy") ?: 0
        val validationError = validateMultipleLotteryChoice(start, end, lottery.getString("bookingDate") ?: "", guests, capacity)
        require(validationError == null) { validationError ?: "Invalid reservation." }
        val existing = campsite.get("combinedReservations") as? List<*> ?: emptyList<Any>()
        require(existing.none { entry ->
            val item = entry as? Map<*, *> ?: return@none true
            val otherStart = item["startReservedDate"] as? String ?: return@none true
            val otherEnd = item["endReservedDate"] as? String ?: return@none true
            MultipleLotteryDates.overlaps(start, end, otherStart, otherEnd) != false
        }) { "These dates are already reserved. Please choose other dates or a different campsite." }

        val reservation = mapOf(
            "reservationId" to reservationId, "campsiteId" to campsiteId,
            "startReservedDate" to start, "endReservedDate" to end,
            "numOfGuests" to guests, "appPolicyRead" to false
        )
        val occupancy = mapOf(
            "reservationId" to reservationId, "userId" to uid,
            "startReservedDate" to start, "endReservedDate" to end
        )
        tx.update(campsiteRef, "combinedReservations", FieldValue.arrayUnion(occupancy), "usersBooked", FieldValue.arrayUnion(userRef))
        tx.update(userRef, "currentReservations", FieldValue.arrayUnion(reservation))
        tx.update(lotteryRef, "lotteryWinnerList", FieldValue.arrayRemove(userRef))
        null
    }.addOnSuccessListener { onSuccess() }.addOnFailureListener(onFailure)
}
