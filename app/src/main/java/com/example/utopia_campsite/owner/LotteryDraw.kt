/**
 * Utopia 02.05.01
 * Draws a campsite's lottery winners at random once its registration period is over
 * and saves them to the firebase firestore
 *
 * Outstanding Issues:
 * Nothing calls drawEndedLotteries yet, it needs to run when the app starts (e.g. in MainActivity)
 * There is no server, so the draw happens the first time the app runs after the registration period ends
 * Winners are not notified yet (US 01.03.01, 01.03.02)
 */

package com.example.utopia_campsite.owner

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
 * Draws one campsite's winners from its waitlist and saves them to the firebase firestore
 * Runs in a transaction, so if two phones try at the same time the winners are only drawn once
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
        } //end if
    }
} //end drawWinners

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