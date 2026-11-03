/**
 * Shows the summary of one user reservation in a list
 * campsite name, campsite photo, Date Reserved and number of guests
 * when user clicked a row the reservation details is shown
 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites

@Composable
fun UserReservationSummaryRow(reservation: Reservation, onClick: ()-> Unit) {
    var loadedCampsite by remember(reservation.campsiteId) { mutableStateOf<Campsites?>(null)}
    LaunchedEffect(reservation.campsiteId) {
        CampsiteRepository().getCampsite(
            campsiteId = reservation.campsiteId,
            onSuccess = { loadedCampsite = it},
            onFailure = { loadedCampsite = null }
        )
    }

    val campsite = loadedCampsite ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Campsite Name at the very top
        Text(
            text = campsite.name,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp)) //Space between name and the row below

        // Row containing the Image and the Dates
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image / Default Image
            if (campsite.photoUrl!= null) {
                AsyncImage(
                    model = campsite.photoUrl,
                    contentDescription = "Campsite Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red)
                )
            } else {

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),

                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "No Image",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                }
            } //end else

            Spacer(modifier = Modifier.width(16.dp)) // Space between image and text

            // 3. Date Details and number of guests
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Dates: ${reservation.startReservedDate} to ${reservation.endReservedDate}",
                    fontSize = 14.sp,
                )

                Text(
                    text = "Number of Guests: ${reservation.numOfGuests}",
                    fontSize = 14.sp,
                )
            } //end column
        } //end row
    } //end outer column
} //end UserReservationSummaryRow

