package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Owner form for a shared two-campsite lottery (US 02.04.02). */
/**
 * As a campsite owner, I want to be able to list multiple campsites in a lottery for a certain period.

 * Displays the owner screen for creating a shared lottery for two campsites.
 *
 * The screen loads the signed-in owner's campsites and lets the owner select
 * two campsites, enter the registration dates, and enter the number of winners.
 * When the owner submits the form, the screen calls createCombinedLottery.
 * It also displays loading, success, and error messages.
 *
 * @param onBack Callback called when the owner presses the Back button.
 *               It returns the owner to the previous screen.
 */
@Composable
fun MultipleLotteryScreen(onBack: () -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var campsites by remember { mutableStateOf<List<Campsites>?>(null) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var winners by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var created by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (uid == null) {
            message = "Please sign in as an owner."
        } else {
            CampsiteRepository().getCampsites(
                onSuccess = { campsites = it.filter { campsite -> campsite.ownerId == uid } },
                onFailure = { message = "Could not load campsites: ${it.message}" }
            )
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack, enabled = !saving) { Text("Back") }
        Text("Create two-campsite lottery", style = MaterialTheme.typography.headlineSmall)
        Text("Select two campsites with matching booking dates and capacity.")
        if (campsites == null && message == null) CircularProgressIndicator()
        else if (campsites != null) {
            LazyColumn(Modifier.weight(1f, fill = false)) {
                items(campsites ?: emptyList(), key = { it.id }) { campsite ->
                    Row {
                        Checkbox(
                            checked = campsite.id in selected,
                            enabled = !saving && !created,
                            onCheckedChange = { checked ->
                                selected = if (checked) selected + campsite.id else selected - campsite.id
                            }
                        )
                        Text("${campsite.name}\nBooking: ${campsite.bookingDate}\nCapacity: ${campsite.maximumOccupants}")
                    }
                }
            }
        }
        OutlinedTextField(start, { start = it }, label = { Text("Registration start DD/MM/YYYY") }, enabled = !saving && !created)
        OutlinedTextField(end, { end = it }, label = { Text("Registration end DD/MM/YYYY") }, enabled = !saving && !created)
        OutlinedTextField(winners, { winners = it }, label = { Text("Number of winners") }, enabled = !saving && !created)
        message?.let { Text(it) }
        Button(enabled = !saving && !created, onClick = {
            val count = winners.toIntOrNull()
            if (selected.size != 2 || count == null || count <= 0 || !MultipleLotteryDates.isValidPeriod(start, end)) {
                message = "Select exactly two campsites and enter valid dates and a positive winner count."
            } else {
                saving = true
                message = null
                val ids = selected.toList()
                createCombinedLottery(ids[0], ids[1], start, end, count,
                    onSuccess = { saving = false; created = true; message = "Lottery created." },
                    onFailure = { saving = false; message = it.message ?: "Could not create lottery." }
                )
            }
        }) { Text(if (saving) "Creating..." else "Create lottery") }
    }
}

/**
 * Creates one shared lottery record for two existing campsites in Firestore.
 *
 * The function checks that the owner is signed in, the campsite IDs are
 * different, the registration dates are valid, and the winner count is positive.
 * It then checks that both campsites belong to the signed-in owner and have
 * matching booking dates and maximum capacity.
 *
 * If the checks pass, a Firestore transaction creates a combined record named
 * "Campsite A & Campsite B" and adds its ID to the owner's posted campsites.
 *
 * @param firstId Firestore document ID of the first campsite.
 * @param secondId Firestore document ID of the second campsite.
 * @param registrationStart First day entrants can register, in DD/MM/YYYY format.
 * @param registrationEnd Last day entrants can register, in DD/MM/YYYY format.
 * @param winnerCount Number of winners to select from the shared waitlist.
 * @param onSuccess Callback called after the transaction succeeds.
 *                  It receives the new combined lottery's document ID.
 * @param onFailure Callback called when validation or the Firestore transaction
 *                  fails. It receives an exception describing the error.
 */

fun createCombinedLottery(
    firstId: String,
    secondId: String,
    registrationStart: String,
    registrationEnd: String,
    winnerCount: Int,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    if (uid.isNullOrBlank() || firstId.isBlank() || secondId.isBlank() || firstId == secondId ||
        winnerCount <= 0 || !MultipleLotteryDates.isValidPeriod(registrationStart, registrationEnd)
    ) {
        onFailure(IllegalArgumentException("Check the owner, campsites, dates and winner count."))
        return
    }

    val db = FirebaseFirestore.getInstance()
    val firstRef = db.collection("campsites").document(firstId)
    val secondRef = db.collection("campsites").document(secondId)
    val ownerRef = db.collection("owners").document(uid)
    val lotteryRef = db.collection("campsites").document()

    db.runTransaction { transaction ->
        val owner = transaction.get(ownerRef)
        val first = transaction.get(firstRef)
        val second = transaction.get(secondRef)
        require(owner.exists() && first.exists() && second.exists()) { "Owner or campsite not found." }
        require(first.getString("ownerId") == uid && second.getString("ownerId") == uid) {
            "Both campsites must belong to the signed-in owner."
        }
        require(first.getBoolean("isCombinedLottery") != true && second.getBoolean("isCombinedLottery") != true) {
            "Choose two physical campsites."
        }
        val capacity = first.getLong("maxOccupancy") ?: 0L
        require(capacity > 0 && capacity == second.getLong("maxOccupancy")) {
            "Both campsites must have the same positive capacity."
        }
        val booking = first.getString("bookingDate") ?: ""
        val periods = booking.split("-")
        val secondPeriods = (second.getString("bookingDate") ?: "").split("-")
        require(periods.size == 2 && secondPeriods.size == 2 &&
                MultipleLotteryDates.isValidPeriod(periods[0].trim(), periods[1].trim()) &&
                periods.map { it.trim() } == secondPeriods.map { it.trim() }) {
            "Both campsites must have the same valid booking period."
        }
        val firstName = first.getString("name").orEmpty()
        val secondName = second.getString("name").orEmpty()
        require(firstName.isNotBlank() && secondName.isNotBlank()) { "Campsite names are required." }
        transaction.set(lotteryRef, mapOf(
            "name" to "$firstName & $secondName",
            "ownerId" to uid,
            "isCombinedLottery" to true,
            "campsiteIds" to listOf(firstId, secondId),
            "registrationDate" to "$registrationStart-$registrationEnd",
            "bookingDate" to "${periods[0].trim()}-${periods[1].trim()}",
            "maxOccupancy" to capacity,
            "lotteryWinners" to winnerCount,
            "lotteryWaitlist" to emptyList<Any>(),
            "lotteryDrawn" to false
        ))
        transaction.update(ownerRef, "postedCampsitesId", FieldValue.arrayUnion(lotteryRef.id))
        lotteryRef.id
    }.addOnSuccessListener(onSuccess).addOnFailureListener(onFailure)
}

/**
 * Provides functions for checking and comparing lottery and reservation dates.
 *
 * Dates are entered as DD/MM/YYYY. Date comparisons use UTC, and both the
 * start and end dates are included when checking whether periods overlap.
 */
object MultipleLotteryDates {
    private fun parse(value: String): Long? {
        if (!Regex("\\d{2}/\\d{2}/\\d{4}").matches(value)) return null
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply {
            isLenient = false
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val position = ParsePosition(0)
        val result = format.parse(value, position) ?: return null
        return if (position.index == value.length) result.time else null
    }

    /** True when both dates exist and the start is no later than the end. */
    fun isValidPeriod(start: String, end: String): Boolean {
        val first = parse(start) ?: return false
        val last = parse(end) ?: return false
        return first <= last
    }

    /** Returns null for invalid input; otherwise true if the inclusive periods overlap. */
    fun overlaps(start: String, end: String, otherStart: String, otherEnd: String): Boolean? {
        val first = parse(start) ?: return null
        val last = parse(end) ?: return null
        val otherFirst = parse(otherStart) ?: return null
        val otherLast = parse(otherEnd) ?: return null
        if (first > last || otherFirst > otherLast) return null
        return first <= otherLast && otherFirst <= last
    }
}
