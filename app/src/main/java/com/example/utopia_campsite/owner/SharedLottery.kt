/**
 * Utopia 02.04.02: lets campsite owners list their campsites together in one shared lottery
 *
 * A shared lottery is one document in the "campsites" collection, in the same shape that
 * createCombinedLottery (MultipleLotteryScreen.kt) writes, so the existing draw and
 * notifications work on it unchanged. On top of that shape it adds:
 *  - invitedOwnerIds: owners the host invited who have not answered yet
 *  - memberOwnerIds: owners who have a campsite in the lottery (the host included)
 *  - winnerChoices: each winner's user ID mapped to the campsite ID they chose
 *
 * Only the host (ownerId) can change the registration and booking periods or invite owners
 *
 * Outstanding Issues:
 * If the host picks more winners than there are campsites, the last winners have nothing left to choose
 */

package com.example.utopia_campsite.owner

import com.example.utopia_campsite.user.Reservation
import com.example.utopia_campsite.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * The checks for shared lotteries, kept free of Firebase so they can be unit tested
 */
object SharedLotteryRules {

    /**
     * Splits a "start-end" period into its two trimmed dates
     *
     * @param period: the period, e.g. "10/10/2026-20/10/2026"
     * @return the start and end dates, or null if the period is not two valid dates
     */
    fun splitPeriod(period: String): Pair<String, String>? {
        val parts = period.split("-").map { it.trim() }
        if (parts.size != 2 || !MultipleLotteryDates.isValidPeriod(parts[0], parts[1])) return null
        return parts[0] to parts[1]
    } //end splitPeriod

    /**
     * Checks whether an owner can add one of their campsites to a shared lottery
     *
     * @param lottery: the shared lottery
     * @param campsite: the campsite the owner wants to add
     * @param ownerId: the owner adding the campsite
     * @param today: today's date as DD/MM/YYYY
     * @return null if the campsite can join, otherwise the reason it cannot
     */
    fun canJoin(lottery: Campsites, campsite: Campsites, ownerId: String, today: String): String? {
        val lotteryBooking = splitPeriod(lottery.bookingDate)
        return when {
            campsite.ownerId != ownerId -> "You can only add your own campsites."
            campsite.isSharedLottery() -> "Choose a campsite, not another shared lottery."
            campsite.id in lottery.campsiteIds -> "This campsite is already in the lottery."
            lottery.lotteryDrawn || isRegistrationOver(lottery.registrationDate, today) ->
                "This lottery's registration period is over."
            lotteryBooking == null || splitPeriod(campsite.bookingDate) != lotteryBooking ->
                "The booking period must be ${lottery.bookingDate.replace("-", " - ")}."
            campsite.maximumOccupants <= 0 || campsite.maximumOccupants != lottery.maximumOccupants ->
                "The maximum occupants must be ${lottery.maximumOccupants}."
            else -> null
        }
    } //end canJoin

    /**
     * Finds the owners shown on the invite screen
     *
     * @param owners: every owner
     * @param query: the search text, matched against the owner's full name and email
     * @param excludeIds: owners who should not be shown (the host, members, already invited)
     * @param limit: the most owners to return
     * @return up to limit matching owners
     */
    fun filterOwners(owners: List<Owner>, query: String, excludeIds: Collection<String>, limit: Int = 10): List<Owner> {
        val search = query.trim()
        return owners
            .filter { it.deviceID.isNotBlank() && it.deviceID !in excludeIds }
            .filter { owner ->
                search.isEmpty() ||
                        "${owner.firstName} ${owner.lastName}".contains(search, ignoreCase = true) ||
                        owner.email.contains(search, ignoreCase = true)
            }
            .take(limit)
    } //end filterOwners

    /**
     * Finds the campsites a winner can still choose
     *
     * @param lottery: the shared lottery
     * @param campsites: the campsites to pick from
     * @return the campsites in the lottery that no winner has chosen yet
     */
    fun availableChoices(lottery: Campsites, campsites: List<Campsites>): List<Campsites> {
        val taken = lottery.winnerChoices.values.toSet()
        return campsites.filter { it.id in lottery.campsiteIds && it.id !in taken }
    } //end availableChoices
} //end SharedLotteryRules

/**
 * Reads and saves shared lotteries in the firebase firestore
 */
class SharedLotteryRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Gives the signed-in user's uid
     */
    private fun currentUid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    /**
     * Reads the fields SharedLotteryRules needs from a campsite document
     * (inside a transaction there is no CampsiteRepository, so this is done by hand)
     *
     * @param document: the campsite or shared lottery document
     */
    private fun toRulesCampsite(document: DocumentSnapshot): Campsites {
        return Campsites(
            id = document.id,
            name = document.getString("name") ?: "",
            ownerId = document.getString("ownerId") ?: "",
            registrationDate = document.getString("registrationDate") ?: "",
            bookingDate = document.getString("bookingDate") ?: "",
            maximumOccupants = document.getLong("maxOccupancy")?.toInt() ?: 0,
            lotteryDrawn = document.getBoolean("lotteryDrawn") ?: false,
            campsiteIds = (document.get("campsiteIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        )
    } //end toRulesCampsite

    /**
     * Creates a shared lottery from one or more of the signed-in owner's campsites
     * Other owners can then be invited to add their campsites
     *
     * @param campsiteIds: the host's campsites to start the lottery with
     * @param registrationStart: the first day entrants can join, DD/MM/YYYY
     * @param registrationEnd: the last day entrants can join, DD/MM/YYYY
     * @param winnerCount: how many winners to draw
     * @param onSuccess: called with the new lottery's document ID
     * @param onFailure: called if a check or the save fails
     */
    fun createSharedLottery(
        campsiteIds: List<String>,
        registrationStart: String,
        registrationEnd: String,
        winnerCount: Int,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = currentUid()
        val ids = campsiteIds.distinct()
        if (uid == null || ids.isEmpty() || winnerCount <= 0 ||
            !MultipleLotteryDates.isValidPeriod(registrationStart, registrationEnd)
        ) {
            onFailure(IllegalArgumentException("Select a campsite and enter valid dates and a positive winner count."))
            return
        }

        val ownerRef = db.collection("owners").document(uid)
        val lotteryRef = db.collection("campsites").document()

        db.runTransaction { transaction ->
            // all reads happen before any writes in a transaction
            val owner = transaction.get(ownerRef)
            val campsites = ids.map { toRulesCampsite(transaction.get(db.collection("campsites").document(it))) }
            require(owner.exists()) { "Owner not found." }
            require(campsites.all { it.ownerId == uid }) { "Every campsite must be yours." }
            require(campsites.none { it.isSharedLottery() }) { "Choose campsites, not another shared lottery." }

            val first = campsites.first()
            val booking = SharedLotteryRules.splitPeriod(first.bookingDate)
            require(booking != null && campsites.all { SharedLotteryRules.splitPeriod(it.bookingDate) == booking }) {
                "Every campsite must have the same valid booking period."
            }
            require(first.maximumOccupants > 0 && campsites.all { it.maximumOccupants == first.maximumOccupants }) {
                "Every campsite must have the same maximum occupants."
            }

            transaction.set(lotteryRef, mapOf(
                // the same fields createCombinedLottery writes, so the draw works unchanged
                "name" to campsites.joinToString(" & ") { it.name },
                "ownerId" to uid,
                "isCombinedLottery" to true,
                "campsiteIds" to ids,
                "registrationDate" to "$registrationStart-$registrationEnd",
                "bookingDate" to "${booking.first}-${booking.second}",
                "maxOccupancy" to first.maximumOccupants.toLong(),
                "lotteryWinners" to winnerCount,
                "lotteryWaitlist" to emptyList<Any>(),
                "lotteryDrawn" to false,
                // the owner list reads these, and fails on blank dates
                "availableStartDate" to booking.first,
                "availableEndDate" to booking.second,
                "description" to "Shared lottery",
                // shared lottery fields
                "memberOwnerIds" to listOf(uid),
                "invitedOwnerIds" to emptyList<String>(),
                "winnerChoices" to emptyMap<String, String>()
            ))
            transaction.update(ownerRef, "postedCampsitesId", FieldValue.arrayUnion(lotteryRef.id))
            lotteryRef.id
        }.addOnSuccessListener(onSuccess).addOnFailureListener(onFailure)
    } //end createSharedLottery

    /**
     * Retrieves every owner, so the invite screen can list and search them
     * Searching happens on the phone, since Firestore has no "contains" search
     *
     * @param onSuccess: called with every owner
     * @param onFailure: called if the owners could not be loaded
     */
    fun getAllOwners(onSuccess: (List<Owner>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("owners").get()
            .addOnSuccessListener { result ->
                onSuccess(result.documents.mapNotNull { it.toObject(Owner::class.java)?.copy(deviceID = it.id) })
            }
            .addOnFailureListener(onFailure)
    } //end getAllOwners

    /**
     * Retrieves one shared lottery
     *
     * @param lotteryId: the lottery's document ID
     * @param onSuccess: called with the lottery
     * @param onFailure: called if it does not exist or could not be loaded
     */
    fun getLottery(lotteryId: String, onSuccess: (Campsites) -> Unit, onFailure: (Exception) -> Unit) {
        CampsiteRepository().getCampsites(
            onSuccess = { campsites ->
                val found = campsites.find { it.id == lotteryId && it.isSharedLottery() }
                if (found != null) onSuccess(found) else onFailure(Exception("Lottery not found."))
            },
            onFailure = onFailure
        )
    } //end getLottery

    /**
     * Retrieves the shared lotteries an owner has been invited to
     *
     * @param ownerId: the invited owner
     * @param onSuccess: called with the lotteries
     * @param onFailure: called if they could not be loaded
     */
    fun getInvitesFor(ownerId: String, onSuccess: (List<Campsites>) -> Unit, onFailure: (Exception) -> Unit) {
        CampsiteRepository().getCampsites(
            onSuccess = { campsites -> onSuccess(campsites.filter { ownerId in it.invitedOwnerIds }) },
            onFailure = onFailure
        )
    } //end getInvitesFor

    /**
     * Invites another owner to add a campsite to the signed-in host's shared lottery
     *
     * @param lotteryId: the lottery's document ID
     * @param ownerId: the owner to invite
     * @param onSuccess: called once the invite is saved
     * @param onFailure: called if a check or the save fails
     */
    fun inviteOwner(lotteryId: String, ownerId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = currentUid()
        if (uid == null || ownerId.isBlank() || ownerId == uid) {
            onFailure(IllegalArgumentException("Choose another owner to invite."))
            return
        }
        val lotteryRef = db.collection("campsites").document(lotteryId)
        db.runTransaction { transaction ->
            val lottery = transaction.get(lotteryRef)
            require(lottery.exists()) { "Lottery not found." }
            require(lottery.getString("ownerId") == uid) { "Only the host can invite owners." }
            require(lottery.getBoolean("lotteryDrawn") != true &&
                    !isRegistrationOver(lottery.getString("registrationDate") ?: "", todayText())) {
                "This lottery's registration period is over."
            }
            val members = (lottery.get("memberOwnerIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            require(ownerId !in members) { "This owner is already in the lottery." }
            transaction.update(lotteryRef, "invitedOwnerIds", FieldValue.arrayUnion(ownerId))
            null
        }.addOnSuccessListener { onSuccess() }.addOnFailureListener(onFailure)
    } //end inviteOwner

    /**
     * Adds one of the signed-in owner's campsites to a shared lottery they were invited to
     * The lottery is added to the owner's posted campsites so it shows in their listings
     *
     * @param lotteryId: the lottery's document ID
     * @param campsiteId: the owner's campsite to add
     * @param onSuccess: called once the campsite is added
     * @param onFailure: called if a check or the save fails
     */
    fun acceptInvite(lotteryId: String, campsiteId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = currentUid()
        if (uid == null) {
            onFailure(IllegalStateException("Please sign in again."))
            return
        }
        val lotteryRef = db.collection("campsites").document(lotteryId)
        val campsiteRef = db.collection("campsites").document(campsiteId)
        val ownerRef = db.collection("owners").document(uid)
        db.runTransaction { transaction ->
            val lotteryDocument = transaction.get(lotteryRef)
            val campsiteDocument = transaction.get(campsiteRef)
            require(lotteryDocument.exists() && campsiteDocument.exists()) { "Lottery or campsite not found." }
            val invited = (lotteryDocument.get("invitedOwnerIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            require(uid in invited) { "You no longer have this invite." }
            val problem = SharedLotteryRules.canJoin(
                toRulesCampsite(lotteryDocument), toRulesCampsite(campsiteDocument), uid, todayText()
            )
            require(problem == null) { problem ?: "" }

            transaction.update(lotteryRef, mapOf(
                "campsiteIds" to FieldValue.arrayUnion(campsiteId),
                "memberOwnerIds" to FieldValue.arrayUnion(uid),
                "invitedOwnerIds" to FieldValue.arrayRemove(uid)
            ))
            transaction.update(ownerRef, "postedCampsitesId", FieldValue.arrayUnion(lotteryId))
            null
        }.addOnSuccessListener { onSuccess() }.addOnFailureListener(onFailure)
    } //end acceptInvite

    /**
     * Turns down an invite to a shared lottery
     *
     * @param lotteryId: the lottery's document ID
     * @param onSuccess: called once the invite is removed
     * @param onFailure: called if it could not be removed
     */
    fun declineInvite(lotteryId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = currentUid()
        if (uid == null) {
            onFailure(IllegalStateException("Please sign in again."))
            return
        }
        db.collection("campsites").document(lotteryId)
            .update("invitedOwnerIds", FieldValue.arrayRemove(uid))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onFailure)
    } //end declineInvite

    /**
     * Lets the signed-in lottery winner choose which campsite in a shared lottery to stay at
     * Books the campsite for the lottery's booking period and adds it to the user's reservations
     * Each campsite can only be chosen by one winner
     *
     * @param lotteryId: the lottery's document ID
     * @param campsiteId: the campsite the winner chose
     * @return null on success, or an error message
     */
    suspend fun chooseCampsite(lotteryId: String, campsiteId: String): String? {
        val uid = currentUid() ?: return "Please sign in again."
        val lotteryRef = db.collection("campsites").document(lotteryId)
        val campsiteRef = db.collection("campsites").document(campsiteId)
        val userRef = db.collection("users").document(uid)

        return try {
            val reservation = db.runTransaction { transaction ->
                val lottery = transaction.get(lotteryRef)
                require(lottery.exists()) { "Lottery not found." }
                val winners = (lottery.get("lotteryWinnerList") as? List<*>)?.filterIsInstance<DocumentReference>() ?: emptyList()
                require(winners.any { it.id == uid }) { "You do not have a winning opportunity for this lottery." }
                val choices = (lottery.get("winnerChoices") as? Map<*, *>) ?: emptyMap<Any, Any>()
                require(!choices.containsKey(uid)) { "You have already chosen a campsite." }
                require(campsiteId !in choices.values) { "Another winner already chose this campsite." }
                val ids = (lottery.get("campsiteIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                require(campsiteId in ids) { "This campsite is not in the lottery." }
                val booking = SharedLotteryRules.splitPeriod(lottery.getString("bookingDate") ?: "")
                    ?: throw IllegalStateException("The lottery has no valid booking period.")

                val newReservation = Reservation(
                    campsiteId = campsiteId,
                    startReservedDate = booking.first,
                    endReservedDate = booking.second
                )
                transaction.update(lotteryRef, FieldPath.of("winnerChoices", uid), campsiteId)
                transaction.update(campsiteRef, "usersBooked", FieldValue.arrayUnion(userRef))
                transaction.update(userRef, "currentReservations", FieldValue.arrayUnion(newReservation))
                newReservation
            }.await()

            sendConfirmation(uid, reservation)
            null
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            e.message ?: "Couldn't choose the campsite."
        }
    } //end chooseCampsite

    /**
     * Sends the winner a reservation confirmation, like UserRepository.addCurrentReservation does
     *
     * @param userId: the winner
     * @param reservation: the reservation that was saved
     */
    private fun sendConfirmation(userId: String, reservation: Reservation) {
        UserRepository().getUser(
            userId,
            onSuccess = { user ->
                NotificationRepository().send(
                    ownerId = "", // the app sends the confirmation, not an owner
                    campsiteId = reservation.campsiteId,
                    title = "Reservation confirmed",
                    body = "Your reservation from ${reservation.startReservedDate} to ${reservation.endReservedDate} is confirmed.",
                    recipients = listOf(user)
                )
            },
            onFailure = { } // the reservation is saved either way
        )
    } //end sendConfirmation
} //end SharedLotteryRepository
