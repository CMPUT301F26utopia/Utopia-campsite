/**
 * Shows the summary of one reservation in a list
 * campsite name, campsite photo, Date Reserved and number of guests
 * Outstanding Issues:
 * button to cancel reservation
 * no reservation list yet
 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.Campsites

@Composable
fun ReservationSummaryRow(reservation: Reservation){

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

    }
} //end ReservationSummaryRow