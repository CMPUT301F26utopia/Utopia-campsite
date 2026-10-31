package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.owner.Campsites
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Displays a lottery winner's reservation opportunity.
 *
 * Implements:
 * - US 01.04.01: Display the remaining dates the winner can reserve.
 * - US 01.04.03: Allow the winner to decline their opportunity.
 *
 * This file handles the UI and temporary screen state. The caller supplies
 * the lottery result and available dates, and saves declines through
 * [onDecline]. This screen does not access Firebase directly.
 *
 * Decline flow:
 * 1. The user selects "Decline opportunity".
 * 2. A dialog asks the user to confirm.
 * 3. The screen calls [onDecline] and waits for its result.
 * 4. Success displays a confirmation message; failure displays an error
 *    and allows the user to try again.
 *
 * @param campsite The campsite associated with the lottery opportunity.
 * @param opportunityId Unique ID of this lottery invitation, rather than
 * just the campsite ID. A new invitation must have its own ID.
 * @param isWinner Whether the current user has an active winning opportunity.
 * @param availableDates Display-ready dates or date ranges that can still
 * be reserved. The caller must exclude unavailable or booked dates.
 * @param onBack Called when the user selects Back.
 * @param onDecline Suspended function that saves the decline for the supplied
 * opportunity ID. Returns null on success or an error message on failure.
 * The repository must verify the current user's ownership and eligibility.
 * @param isLoading Whether the caller is loading the opportunity.
 * @param loadError Error from loading the opportunity, or null if none.
 */
@Composable
fun LotteryWinnerScreen(
    campsite: Campsites,
    opportunityId: String,
    isWinner: Boolean,
    availableDates: List<String>,
    onBack: () -> Unit,
    onDecline: suspend (String) -> String?,
    isLoading: Boolean = false,
    loadError: String? = null
) {
    // Keep state separate for each campsite and lottery invitation.
    // Switching to another invitation resets this part of the UI.
    key(campsite.id, opportunityId) {
        // Controls whether the decline confirmation dialog is visible.
        var showConfirmation by remember { mutableStateOf(false) }
        // Prevents repeated submissions while saving is in progress.
        var declining by remember { mutableStateOf(false) }
        // Retains the successful decline state during screen recreation.
        // The database remains the source of truth when reopening the screen.
        var declined by rememberSaveable { mutableStateOf(false) }
        // Stores a decline error so the user can see it and retry.
        var declineError by remember { mutableStateOf<String?>(null) }
        // Runs the suspend callback without blocking the UI.
        // The coroutine is cancelled when this part of the UI is removed.
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                onClick = onBack,
                enabled = !declining
            ) {
                Text("Back")
            }

            Text(
                text = "Lottery result",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(campsite.name)

            // Show one screen state at a time.
            when {
                declined -> { Text("You have declined this reservation opportunity.") }
                isLoading -> { Text("Loading your lottery opportunity...") }

                loadError != null -> {
                    Text(text = loadError, color = MaterialTheme.colorScheme.error)
                }

                !isWinner || opportunityId.isBlank() -> {
                    Text("You do not have an active winning opportunity.")
                }

                else -> {
                    Text("You won the lottery!")

                    // US 01.04.01:
                    // Display the remaining dates supplied by the caller.
                    Text(
                        text = "Available reservation dates",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (availableDates.isEmpty()) {
                        Text("There are no remaining dates available.")
                    } else {
                        availableDates.forEach { date ->
                            Text(date)
                        }
                    }

                    // Keep the opportunity visible after a failed decline.
                    declineError?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // US 01.04.03:
                    // Open a confirmation dialog before submitting a decline.
                    // Winners can decline even when no dates remain.
                    Button(
                        enabled = !declining,
                        onClick = { showConfirmation = true }
                    ) {
                        Text(
                            if (declining) "Declining..."
                            else "Decline opportunity"
                        )
                    }
                }
            }
        }

        // Only display the dialog for an active, loaded opportunity.
        if (
            showConfirmation && isWinner && !declined &&
            !isLoading && loadError == null &&
            opportunityId.isNotBlank()
        ) {
            AlertDialog(
                // Tapping outside the dialog or pressing system Back
                // closes it without submitting a decline.
                onDismissRequest = { showConfirmation = false },

                title = {
                    Text("Decline opportunity?")
                },

                text = {
                    Text(
                        "You will give up this opportunity to reserve " +
                                "${campsite.name}."
                    )
                },

                confirmButton = {
                    TextButton(
                        enabled = !declining,
                        onClick = {
                            showConfirmation = false
                            declining = true
                            declineError = null

                            scope.launch {
                                try {
                                    // Ask the caller to persist the decline.
                                    // Do not mark it successful before saving.
                                    val error = onDecline(opportunityId)

                                    if (error == null) {
                                        declined = true
                                    } else {
                                        declineError = error
                                    }
                                } catch (cancelled: CancellationException) {
                                    // Preserve coroutine cancellation when
                                    // the user leaves this screen.
                                    throw cancelled
                                } catch (_: Exception) {
                                    // Handle unexpected failures and allow retry.
                                    declineError =
                                        "Unable to decline. Please try again."
                                } finally {
                                    // Restore controls after the request ends.
                                    declining = false
                                }
                            }
                        }
                    ) {
                        Text("Yes, decline")
                    }
                },

                dismissButton = {
                    TextButton(
                        // Close the dialog and keep the opportunity.
                        onClick = { showConfirmation = false }
                    ) {
                        Text("Keep opportunity")
                    }
                }
            )
        }
    }
}