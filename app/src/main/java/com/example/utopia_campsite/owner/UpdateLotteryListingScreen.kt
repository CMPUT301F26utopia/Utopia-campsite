package com.example.utopia_campsite.owner

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utopia_campsite.user.CalendarViewScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Validates dates in DD/MM/YYYY format.
private val DATE_FORMAT = Regex("""\d{2}/\d{2}/\d{4}""")

private fun parseDate(date: String): Date? {
    if (!DATE_FORMAT.matches(date)) return null

    return try {
        SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).apply {
            isLenient = false
        }.parse(date)
    } catch (e: Exception) {
        null
    }
}

/**
 * Helper method that Converts "10/10/2026-20/10/2026" into two dates.
 * @param range this is the date range that will be parsed
 * @return Pair<start date, end date>, using .first, .second to identify
 */
private fun splitDateRange(range: String): Pair<String, String> {
    val dates = range.split("-", limit = 2)

    return Pair(
        dates.getOrElse(0) { "" },
        dates.getOrElse(1) { "" }
    )
}


/**
 * Dedicated method to allowing the campsite owner to make updates to his campsite's lottery listing
 * Things such as registeration/booking dates,
 * @param modifier to style the screen
 * @param campsite the selected campsite whose details are displayed
 * @param onSave this is the callback method used for updatiing the db with the updated changes (if any)
 */
@Composable
fun UpdateLotteryListingScreen(
    modifier: Modifier = Modifier,
    campsite: Campsites,
    onSave: (Campsites) -> Unit = {}
) {
    // Read current lottery information.
    val registrationDates = remember(
        campsite.id,
        campsite.registrationDate
    ) {
        splitDateRange(campsite.registrationDate)
    }

    val bookingDates = remember(
        campsite.id,
        campsite.bookingDate
    ) {
        splitDateRange(campsite.bookingDate)
    }

    // Initialize the form with existing values.
    var lotteryWinners by remember(
        campsite.id,
        campsite.lotteryWinners
    ) {
        mutableStateOf(campsite.lotteryWinners.toString())
    }

    var regStart by remember(registrationDates) {
        mutableStateOf(registrationDates.first)
    }

    var regEnd by remember(registrationDates) {
        mutableStateOf(registrationDates.second)
    }

    var bookStart by remember(bookingDates) {
        mutableStateOf(bookingDates.first)
    }

    var bookEnd by remember(bookingDates) {
        mutableStateOf(bookingDates.second)
    }

    // Validate fields.
    val winners = lotteryWinners.toIntOrNull()
    val validWinners = winners != null && winners > 0

    val registrationStartDate = parseDate(regStart)
    val registrationEndDate = parseDate(regEnd)

    val bookingStartDate = parseDate(bookStart)
    val bookingEndDate = parseDate(bookEnd)

    val validRegistrationPeriod =
        registrationStartDate != null &&
                registrationEndDate != null &&
                !registrationStartDate.after(registrationEndDate)

    val validBookingPeriod =
        bookingStartDate != null &&
                bookingEndDate != null &&
                !bookingStartDate.after(bookingEndDate)

    val canSave =
        validWinners &&
                validRegistrationPeriod &&
                validBookingPeriod

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Update Lottery Listing",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Campsite: ${campsite.name}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = modifier.padding(2.dp))

        // Number of winners.
        OutlinedTextField(
            value = lotteryWinners,
            onValueChange = { lotteryWinners = it },
            label = { Text("Number of Winners") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = lotteryWinners.isNotEmpty() && !validWinners,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Existing waitlist is not modified by this form.
        Text(
            text = "Current Waitlist: ${campsite.lotteryWaitlist.size} entrants",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = modifier.padding(2.dp))

        // Registration period.
        Text(
            text = "Registration Period",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CalendarViewScreen(
                    label = "Start Date",
                    date = regStart,
                    onDateChange = { regStart = it },
                    testTag = ""
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                CalendarViewScreen(
                    label = "End Date",
                    date = regEnd,
                    onDateChange = { regEnd = it },
                    testTag = ""
                )
            }
        }

        if (!validRegistrationPeriod) {
            Text(
                text = "Enter a valid registration period.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = modifier.padding(2.dp))

        // Booking period.
        Text(
            text = "Booking Period",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CalendarViewScreen(
                    label = "Start Date",
                    date = bookStart,
                    onDateChange = { bookStart = it },
                    testTag = ""
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                CalendarViewScreen(
                    label = "End Date",
                    date = bookEnd,
                    onDateChange = { bookEnd = it },
                    testTag = ""
                )
            }
        }

        if (!validBookingPeriod) {
            Text(
                text = "Enter a valid booking period.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Submit updated campsite information.
        Button(
            onClick = {
                val updatedCampsite = campsite.copy(
                    lotteryWinners = winners!!,
                    registrationDate = "$regStart-$regEnd",
                    bookingDate = "$bookStart-$bookEnd"
                )

                onSave(updatedCampsite)
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}

/**
 * Method for Preview.
 */
@Preview(showBackground = true)
@Composable
fun UpdateLotteryListingPreview() {
    val sampleCampsite = Campsites(
        id = "1",
        name = "Elk Island Campsite",
        photoUrl = null,
        address = "Elk Island National Park, Alberta",
        latitude = 53.6,
        longitude = -112.8,
        ratings = listOf(4, 5, 5),
        tags = listOf("Nature", "Hiking", "Camping"),
        lotteryWinners = 10,
        lotteryWaitlist = mutableListOf("user1", "user2"),
        description = "A beautiful campsite surrounded by nature.",
        registrationDate = "10/10/2026-20/10/2026",
        bookingDate = "01/11/2026-15/11/2026",
        maximumOccupants = 6,
        availableStartDate = "01/11/2026",
        availableEndDate = "30/11/2026",
        usersBooked = mutableListOf()
    )

    MaterialTheme {
        UpdateLotteryListingScreen(
            campsite = sampleCampsite,
            onSave = { updatedCampsite ->
                // Connect to repository when implemented.
            }
        )
    }
}