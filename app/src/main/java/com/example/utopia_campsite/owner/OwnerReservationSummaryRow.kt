/**
 * Shows the summary of one owner reservation in a list
 * campsite name, campsite photo, Date Reserved and number of guests
 * shows a button below each reservation to cancel reservation
 */

package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.utopia_campsite.user.Reservation

@Composable
fun OwnerReservationSummaryRow(reservation: Reservation, onClickCancelReservation : (Reservation)-> Unit) {
    // Stack the four pieces of summary information from top to bottom
    Column {
        if(!reservation.photoUrl.isNullOrBlank()){
            AsyncImage(
                model =reservation.photoUrl,
                contentDescription = "Image of ${reservation.campsiteName}",
                modifier = Modifier.fillMaxWidth().height(90.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(text = reservation.campsiteName, modifier=Modifier.padding(start = 12.dp))
        Text(text = "Dates: ${reservation.startReservedDate} to ${reservation.endReservedDate}", modifier=Modifier.padding(start = 12.dp))
        Text(text = "Number of Guests: ${reservation.numOfGuests}", modifier=Modifier.padding(start = 12.dp))

        Button(
            onClick = { onClickCancelReservation(reservation) }, //callback to be implemented
            modifier = Modifier.fillMaxWidth().testTag("cancelReservationButton"),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White)
        ) {
            Text(text = "Cancel Reservation")
        } //end button
    } //end column
} //end OwnerReservationSummaryRow

