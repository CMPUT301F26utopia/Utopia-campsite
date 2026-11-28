package com.example.utopia_campsite.user

import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Information needed to show one multiple-campsite lottery opportunity.
 *
 * @property lotteryId Firestore document ID of the combined A & B lottery.
 * @property lotteryName Display name of the combined lottery.
 * @property bookingPeriod Shared booking period, for example
 *                         "01/12/2026-10/12/2026".
 * @property campsites The two original campsites the winner can choose from.
 */
data class MultipleLotteryOpportunity(
    val lotteryId: String,
    val lotteryName: String,
    val bookingPeriod: String,
    val campsites: List<Campsites>
)

/**
 * Loads a combined lottery and checks whether the signed-in user is a winner.
 *
 * The existing lottery draw stores lotteryWinnerList as Firestore user
 * document references. This function checks whether the current user's
 * reference appears in that list, then loads the two original campsites.
 *
 * @param lotteryId Firestore document ID of the combined A & B record.
 * @param onSuccess Called with the opportunity after it has been loaded.
 * @param onFailure Called if loading fails, the record is not a completed
 *                  combined lottery, or the signed-in user is not a winner.
 */
fun loadMultipleLotteryOpportunity(
    lotteryId: String,
    onSuccess: (MultipleLotteryOpportunity) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    if (uid.isNullOrBlank()) {
        onFailure(IllegalStateException("Please sign in first."))
        return
    }

    if (lotteryId.isBlank()) {
        onFailure(IllegalArgumentException("Lottery ID is missing."))
        return
    }

    val db = FirebaseFirestore.getInstance()
    val currentUserRef = db.collection("users").document(uid)

    db.collection("campsites")
        .document(lotteryId)
        .get()
        .addOnSuccessListener { document ->

            if (!document.exists()) {
                onFailure(IllegalStateException("Lottery not found."))
                return@addOnSuccessListener
            }

            if (document.getBoolean("isCombinedLottery") != true) {
                onFailure(
                    IllegalStateException(
                        "This is not a multiple-campsite lottery."
                    )
                )
                return@addOnSuccessListener
            }

            if (document.getBoolean("lotteryDrawn") != true) {
                onFailure(
                    IllegalStateException(
                        "The lottery has not been drawn yet."
                    )
                )
                return@addOnSuccessListener
            }

            val winnerReferences = (document.get("lotteryWinnerList") as? List<*>)
                ?.filterIsInstance<DocumentReference>()
                ?: emptyList()

            if (currentUserRef !in winnerReferences) {
                onFailure(
                    IllegalStateException(
                        "You are not a winner of this lottery."
                    )
                )
                return@addOnSuccessListener
            }

            val campsiteIds = (document.get("campsiteIds") as? List<*>)
                ?.filterIsInstance<String>()
                ?: emptyList()

            if (campsiteIds.size != 2 || campsiteIds.toSet().size != 2) {
                onFailure(
                    IllegalStateException(
                        "This lottery does not contain two valid campsites."
                    )
                )
                return@addOnSuccessListener
            }

            val lotteryName = document.getString("name").orEmpty()
            val bookingPeriod = document.getString("bookingDate").orEmpty()

            // Reuse the existing repository to load the original campsites.
            CampsiteRepository().getCampsites(
                onSuccess = { allCampsites ->

                    val selectedCampsites = campsiteIds.mapNotNull { id ->
                        allCampsites.find { campsite -> campsite.id == id }
                    }

                    if (selectedCampsites.size != 2) {
                        onFailure(
                            IllegalStateException(
                                "One or both campsites could not be found."
                            )
                        )
                    } else {
                        onSuccess(
                            MultipleLotteryOpportunity(
                                lotteryId = lotteryId,
                                lotteryName = lotteryName,
                                bookingPeriod = bookingPeriod,
                                campsites = selectedCampsites
                            )
                        )
                    }
                },
                onFailure = { exception ->
                    onFailure(exception)
                }
            )
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}