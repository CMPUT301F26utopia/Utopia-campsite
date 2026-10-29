/**
 * Displays the list of Reservations if there are current reservations
 *  Displays message if no current reservations
 *  Utopia 02.07.01
 */

package com.example.utopia_campsite.owner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun OwnerProfileCurrentReservationScreen(owner: Owner) {
    var currentReservations = remember { mutableStateListOf(*owner.currentReservations.toTypedArray()) }

    if (owner.currentReservations.isEmpty()) {
        Text("No Current Reservations")
    }
    else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(currentReservations) { reservation ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    OwnerReservationSummaryRow(reservation,
                        onClickCancelReservation = {
                            owner.cancelReservation(owner.currentReservations, it) //deletes the reservation in the list
                            currentReservations.remove(reservation) //updates the screen by removing the deleted reservation
                        }
                    )
                }
            }
        } //end LazyColumn
    } //end else statement
}// end OwnerProfileCurrentReservationScreen