/**
 * Displays the list of Reservations if there are past reservations
 *  Displays message if no past reservations
 *  Utopia 01.06.02
 */

package com.example.utopia_campsite.user
import android.R.attr.onClick
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.auth.onCreateAccount

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserProfilePastReservationScreen(user: User) {
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }  //track state for switching screens
    val pastReservations = remember { mutableStateListOf(*user.pastReservations.toTypedArray()) }

    // If a reservation is selected,show details
    if (selectedReservation != null) {
        ReservationDetailsScreen(
            reservation = selectedReservation!!,
            onBackClick = { selectedReservation = null }, //go back to currentReservation screen
            onButtonClick = {
                pastReservations.remove(selectedReservation) //removes the reservation and updates screen
                user.pastReservations.remove(selectedReservation) //remove the reservation locally

                //function below deletes the reservation in firebase
                UserRepository().deletePastReservation(
                    userId = user.deviceID,
                    reservationId = selectedReservation!!.reservationId,
                    onSuccess = {},
                    onFailure = {}
                )
                selectedReservation = null
            }
        )
        return //makes sure the reservationDetailsScreen and UserProfilePastReservationScreen don't overlap
    } //end if statement

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(pastReservations) { reservation ->
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                UserReservationSummaryRow(reservation, onClick = {selectedReservation = reservation})
            }
        }
    } //end LazyColumn
}// end UserProfilePastReservationScreen