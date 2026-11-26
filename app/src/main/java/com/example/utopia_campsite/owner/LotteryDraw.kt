/**
 * Utopia 02.05.01, 01.03.01, 01.03.02
 * Draws a campsite's lottery winners at random once its registration period is over,
 * saves them to the firebase firestore, and notifies everyone on the waitlist whether they won or lost
 *
 * Outstanding Issues:
 * There is no server, so the draw and its notifications happen the first time the app runs after the registration period ends
 * A notification that fails to send is not tried again, since the lottery is already marked as drawn
 */

package com.example.utopia_campsite.owner

import com.example.utopia_campsite.user.UserRepository
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Checks if a campsite's registration period is over
 *
 * @param registrationDate: the registration period, e.g. "10/10/2026-20/10/2026"
 * @param today: today's date as DD/MM/YYYY
 * @return true if today is after the last day of the period, false if it is not over yet or there is no valid period
 */
fun isRegistrationOver(registrationDate: String, today: String): Boolean{
    val endDate = toDate(registrationDate.substringAfter("-")) // the part after "-" is the last day
    val todayDate = toDate(today)
    if (endDate == null || todayDate == null) {
        return false // no registration period, so the campsite is not in a lottery
    }
    return todayDate.after(endDate) // entrants can still join on the last day itself
} //end isRegistrationOver

/**
 * Picks the lottery winners at random from the waitlist
 *
 * @param waitlist: the user IDs of the entrants on the waitlist
 * @param winnerCount: how many winners the owner set (US 02.04.05)
 * @return the winners' user IDs; everyone wins if there are fewer entrants than winners
 */
fun pickWinners(waitlist: List<String>, winnerCount: Int): List<String>{
    // shuffled() puts the entrants in a random order, take() keeps the first winnerCount of them
    // coerceAtLeast(0) stops a negative winner count from crashing take()
    return waitlist.shuffled().take(winnerCount.coerceAtLeast(0))
} //end pickWinners

/**
 * Finds the entrants on the waitlist who did not win (US 01.03.02)
 *
 * @param waitlist: the user IDs of the entrants on the waitlist
 * @param winners: the user IDs that pickWinners picked
 * @return the user IDs that lost, in waitlist order
 */
fun pickLosers(waitlist: List<String>, winners: List<String>): List<String>{
    return waitlist.filter { it !in winners } // everyone on the waitlist except the winners
} //end pickLosers

/**
 * Gives today's date in the phone's time zone
 *
 * @return today's date as DD/MM/YYYY, e.g. "04/11/2026"
 */
fun todayText(): String{
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
} //end todayText

/**
 * Draws the winners of every campsite whose registration period is over and has not been drawn yet
 * Call this once when the app starts
 */
fun drawEndedLotteries(){
    val db = FirebaseFirestore.getInstance()
    val today = todayText()
    db.collection("campsites").get()
        .addOnSuccessListener { result ->
            for (document in result.documents) {
                val alreadyDrawn = document.getBoolean("lotteryDrawn") == true
                val registrationOver = isRegistrationOver(document.getString("registrationDate") ?: "", today)
                if (!alreadyDrawn && registrationOver) {
                    drawWinners(document.id, today)
                } //end if
            } //end for
        }
} //end drawEndedLotteries

/**
 * What one draw picked, kept so the notifications can be sent after the draw is saved
 *
 * @property campsiteName: the campsite's name, shown in the notifications
 * @property winners: the user IDs that won
 * @property losers: the user IDs on the waitlist that did not win
 */
private data class DrawResult(
    val campsiteName: String,
    val winners: List<String>,
    val losers: List<String>
)

/**
 * Draws one campsite's winners from its waitlist and saves them to the firebase firestore
 * Runs in a transaction, so if two phones try at the same time the winners are only drawn once
 * Only the phone whose transaction did the draw sends the notifications, so nobody is told twice
 *
 * @param campsiteId: the Firestore document ID of the campsite
 * @param today: today's date as DD/MM/YYYY
 */
private fun drawWinners(campsiteId: String, today: String){
    val db = FirebaseFirestore.getInstance()
    val campsiteRef = db.collection("campsites").document(campsiteId)
    db.runTransaction { transaction ->
        val document = transaction.get(campsiteRef)
        // check again inside the transaction, another phone may have just drawn
        val alreadyDrawn = document.getBoolean("lotteryDrawn") == true
        val registrationOver = isRegistrationOver(document.getString("registrationDate") ?: "", today)
        if (!alreadyDrawn && registrationOver) {
            // the waitlist is saved as user references, so take each reference's ID
            val waitlist = (document.get("lotteryWaitlist") as? List<*>)?.filterIsInstance<DocumentReference>()?.map { it.id } ?: emptyList()
            val winnerCount = document.getLong("lotteryWinners")?.toInt() ?: 0
            val winners = pickWinners(waitlist, winnerCount)
            transaction.update(campsiteRef, mapOf(
                "lotteryWinnerList" to winners.map { db.collection("users").document(it) }, // user references, like lotteryWaitlist
                "lotteryDrawn" to true // so the winners are never drawn again
            ))
            DrawResult(document.getString("name") ?: "", winners, pickLosers(waitlist, winners))
        } else {
            null // another phone already drew, so that phone sends the notifications
        } //end if
    }.addOnSuccessListener { result ->
        // result is only there if this phone's transaction did the draw
        if (result != null) {
            sendLotteryResults(campsiteId, result)
        } //end if
    }
} //end drawWinners

/**
 * Tells everyone on the waitlist whether they won (US 01.03.01) or lost (US 01.03.02)
 *
 * @param campsiteId: the Firestore document ID of the campsite
 * @param result: the winners and losers this phone just drew
 */
private fun sendLotteryResults(campsiteId: String, result: DrawResult){
    for (userId in result.winners) {
        notifyEntrant(userId, campsiteId, "You won the lottery!", "You won the lottery for ${result.campsiteName}.")
    } //end for
    for (userId in result.losers) {
        notifyEntrant(userId, campsiteId, "You did not win the lottery", "You did not win the lottery for ${result.campsiteName}.")
    } //end for
} //end sendLotteryResults

/**
 * Sends one entrant a notification through NotificationRepository
 * Loads the user first, since NotificationRepository needs the whole user
 * to skip anyone who turned notifications off
 *
 * @param userId: the Firestore document ID of the entrant
 * @param campsiteId: the Firestore document ID of the campsite
 * @param title: the notification's title
 * @param body: the notification's message
 */
private fun notifyEntrant(userId: String, campsiteId: String, title: String, body: String){
    UserRepository().getUser(
        userId,
        onSuccess = { user ->
            NotificationRepository().send(
                ownerId = "", // the app sends lottery results, not an owner
                campsiteId = campsiteId,
                title = title,
                body = body,
                recipients = listOf(user)
            )
        },
        onFailure = { } // the entrant's profile was deleted, so there is nobody to notify
    )
} //end notifyEntrant

/**
 * Reads a DD/MM/YYYY date, a copy of parseDate in CampsiteInputChecks
 * (that one is private, and making it public clashes with the private parseDate in UpdateLotteryListingScreen)
 *
 * @param date: the date text
 * @return the date, or null if the text is not a real date (e.g. 31/02/2026)
 */
private fun toDate(date: String): Date?{
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formatter.isLenient = false // reject dates that don't exist instead of rolling them over
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        formatter.parse(date)
    } catch (e: ParseException) {
        null // not a valid date
    }
} //end toDate
