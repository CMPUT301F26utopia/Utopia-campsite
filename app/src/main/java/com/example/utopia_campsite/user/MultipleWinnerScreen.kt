package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField

/**
 * Displays the two campsites in a multiple-campsite lottery opportunity.
 *
 * The screen loads the combined lottery using its document ID. It shows
 * the two original campsites and lets the winner select one campsite.
 *
 * The winner selects a campsite, dates, and guest count, then confirms a
 * reservation. The reservation is saved before success is displayed.
 *
 * @param lotteryId Firestore document ID of the combined A & B lottery.
 * @param onBack Called when the user presses the Back button.
 */
@Composable
fun MultipleLotteryWinnerScreen(
    lotteryId: String,
    onBack: () -> Unit
) {
    var opportunity by remember(lotteryId) { mutableStateOf<MultipleLotteryOpportunity?>(null) }
    var loading by remember(lotteryId) { mutableStateOf(true) }
    var errorMessage by remember(lotteryId) { mutableStateOf<String?>(null) }
    var selectedCampsiteId by remember(lotteryId) { mutableStateOf<String?>(null) }
    var startDate by remember(lotteryId) { mutableStateOf("") }
    var endDate by remember(lotteryId) { mutableStateOf("") }
    var guestCount by remember(lotteryId) { mutableStateOf("") }
    var inputError by remember(lotteryId) { mutableStateOf<String?>(null) }
    var saving by remember(lotteryId) { mutableStateOf(false) }
    var reserved by remember(lotteryId) { mutableStateOf(false) }

    // Load the combined lottery and check whether the current user is a winner.
    LaunchedEffect(lotteryId) {
        loading = true
        errorMessage = null

        loadMultipleLotteryOpportunity(
            lotteryId = lotteryId,
            onSuccess = { loadedOpportunity ->
                opportunity = loadedOpportunity
                loading = false
            },
            onFailure = { exception ->
                errorMessage = exception.message
                    ?: "Could not load the lottery opportunity."
                loading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack, enabled = !saving) {
            Text("Back")
        }

        Text(
            text = "Choose a campsite",
            style = MaterialTheme.typography.headlineSmall
        )

        when {
            reserved -> {
                Text("Reservation confirmed.", style = MaterialTheme.typography.titleMedium)
                Text("Your reservation is available in Current Reservations.")
            }
            loading -> {
                CircularProgressIndicator()
                Text("Loading your lottery opportunity...")
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            opportunity != null -> {
                val loaded = opportunity!!

                Text(
                    text = "You won the ${loaded.lotteryName} lottery!",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Booking period: ${loaded.bookingPeriod}")
                Text("Choose one campsite:")

                loaded.campsites.forEach { campsite ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCampsiteId == campsite.id,
                            enabled = !saving,
                            onClick = {
                                selectedCampsiteId = campsite.id
                            }
                        )

                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = campsite.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Address: ${campsite.address}")
                            Text("Maximum occupants: ${campsite.maximumOccupants}")
                        }
                    }
                }

                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start date (DD/MM/YYYY)") },
                    enabled = !saving,
                    singleLine = true
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End date (DD/MM/YYYY)") },
                    enabled = !saving,
                    singleLine = true
                )

                OutlinedTextField(
                    value = guestCount,
                    onValueChange = { guestCount = it },
                    label = { Text("Number of guests") },
                    enabled = !saving,
                    singleLine = true
                )

                inputError?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Button(
                    enabled = selectedCampsiteId != null && !saving,
                    onClick = {
                        val selected = loaded.campsites.find { it.id == selectedCampsiteId }
                        val guests = guestCount.toIntOrNull()
                        inputError = when {
                            selected == null -> "Please choose a campsite."
                            guests == null -> "Enter a positive whole number of guests."
                            else -> validateMultipleLotteryChoice(startDate, endDate, loaded.bookingPeriod, guests, selected.maximumOccupants.toLong())
                        }
                        if (inputError == null && selected != null && guests != null) {
                            saving = true
                            reserveMultipleLotteryCampsite(
                                lotteryId, selected.id, startDate, endDate, guests,
                                onSuccess = { saving = false; reserved = true },
                                onFailure = { saving = false; inputError = it.message ?: "Could not save reservation." }
                            )
                        }
                    }
                ) {
                    Text(if (saving) "Saving..." else "Reserve campsite")
                }
            }
        }
    }
}
