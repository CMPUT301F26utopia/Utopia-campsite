/**
 * Displays the list of Reservations if there are current reservations
 *  Displays message if no current reservations
 *  Utopia 01.06.01
 *  highlight button when selected not implemented
 *  user profile not implemented
 *  Past Reservations not implemented
 */

package com.example.utopia_campsite.user
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun UserProfileCurrentReservationScreen(reservations: List<Reservation>) {
    if (reservations.isEmpty()) {
        Text("No Current Reservations")
    }
    else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(reservations) { reservation ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    ReservationSummaryRow(reservation)
                }
            }
        }
    }
}