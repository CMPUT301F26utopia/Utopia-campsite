/**
 * Displays the list of Reservations if there are current reservations
 *  Displays message if no current reservations
 *  Utopia 01.06.01
 *  Outstanding issues:
 *  the if block where ReservationDetailsScreen
 *  is called should be in navApp
 */

package com.example.utopia_campsite.user
import android.R.attr.onClick
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.auth.onCreateAccount

@Composable
fun UserProfileCurrentReservationScreen(user: User) {
    val currentReservations = remember { mutableStateListOf(*user.currentReservations.toTypedArray()) }
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }  //track state for switching screens

    // If a reservation is selected,show details
    if (selectedReservation != null) {
        ReservationDetailsScreen(
            reservation = selectedReservation!!,
            onBackClick = { selectedReservation = null }, //go back to currentReservation screen
            onCancelReservation = {
                currentReservations.remove(selectedReservation) //removes the reservation and updates screen
                user.currentReservations.remove(selectedReservation) //remove the reservation locally

                //function below deletes the reservation in firebase
                UserRepository().deleteCurrentReservation(
                    userId = user.deviceID,
                    reservationId = selectedReservation!!.reservationId,
                    onSuccess = {},
                    onFailure = {}
                )
                selectedReservation = null
            }
        )
        return //makes sure the reservationDetailsScreen and UserProfileCurrentReservationScreen don't overlap
    } //end if statement

    if (user.currentReservations.isEmpty()) {
        Text("No Current Reservations")
    }
    else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(currentReservations) { reservation ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    UserReservationSummaryRow(reservation, onClick = {selectedReservation = reservation})
                }
            }
        } //end LazyColumn
    } //end else statement
}// end UserProfileCurrentReservationScreen