/**
 * Utopia 02.04.01, 02.04.03
 * Lets a campsite owner list one of their campsites in a lottery
 * by setting its registration period (the dates entrants can join the waiting list)
 *
 * Outstanding Issues:
 * Not reachable yet, needs an owner route in UtopiaNavApp
 * The booking period (US 02.04.04) is not on this screen yet
 */

package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.user.CalendarViewScreen
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Shows the screen where the owner picks the registration period for a campsite's lottery
 *
 * @param campsite: the campsite being listed in a lottery
 * @param onSubmit: called with the registration period (e.g. "10/10/2026-20/10/2026") when both dates are valid
 * @param modifier: optional layout changes from the screen that uses this one
 */
@Composable
fun OpenLotteryScreen(campsite: Campsites, onSubmit: (String) -> Unit, modifier: Modifier = Modifier){
    // The dates the owner has picked so far, as DD/MM/YYYY text
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize() // take up the whole screen
            .padding(16.dp) // keep everything away from the screen edges
    ) {
        Text("List ${campsite.name} in a lottery")

        // US 02.04.03: entrants can join the waiting list between these two dates
        // CalendarViewScreen (from UserFilterScreen.kt) lets the owner type a date or pick one from a calendar
        CalendarViewScreen("Registration start", startDate) { startDate = it }
        CalendarViewScreen("Registration end", endDate) { endDate = it }

        Button(
            onClick = {
                // null means a date is missing, not a real date, or the start is after the end
                val registrationPeriod = buildRegistrationPeriod(startDate, endDate)
                if (registrationPeriod != null) {
                    onSubmit(registrationPeriod) // hand the period to whoever shows this screen, e.g. to save it
                }
            }
        ) {
            Text("OPEN LOTTERY")
        }
    } //end Column
} //end OpenLotteryScreen

/**
 * Turns the two picked dates into the registration period text stored in Campsites
 *
 * @param startDate: the first day entrants can join, as DD/MM/YYYY
 * @param endDate: the last day entrants can join, as DD/MM/YYYY
 * @return "start-end" (e.g. "10/10/2026-20/10/2026"), or null if a date is invalid or the start is after the end
 */
fun buildRegistrationPeriod(startDate: String, endDate: String): String?{
    val start = parseDate(startDate)
    val end = parseDate(endDate)
    if (start == null || end == null || start.after(end)) {
        return null
    }
    return "$startDate-$endDate" // same "start-end" format the Campsites class uses
} //end buildRegistrationPeriod

/**
 * Reads a DD/MM/YYYY date, the same way UserCampsiteFilterViewModel does
 *
 * @param date: the date text
 * @return the date, or null if the text is not a real date (e.g. 31/02/2026)
 */
private fun parseDate(date: String): Date?{
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formatter.isLenient = false // reject dates that don't exist instead of rolling them over
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        formatter.parse(date)
    } catch (e: ParseException) {
        null // not a valid date
    }
} //end parseDate