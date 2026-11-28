package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utopia_campsite.user.Reservation
import com.example.utopia_campsite.user.User

/**
 *
 * */
@Composable
fun CampsiteCancelledReservationsScreen(owner: Owner) {
    var cancellations by remember {
        mutableStateOf<List<Pair<User, Reservation>>>(emptyList())
    }
    var campsites by remember {
        mutableStateOf<List<Campsites>>(emptyList())
    }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(owner.deviceID) {
        OwnerRepository().getCancelledReservations(
            owner = owner,
            onSuccess = { cancellations = it },
            onFailure = {
                error = it.message ?: "Failed to load cancellations."
            }
        )

        CampsiteRepository().getOwnerCampsites(
            owner = owner,
            onSuccess = { campsites = it },
            onFailure = { error = it.message }
        )
    }

    when {
        error != null -> Text(error!!)

        cancellations.isEmpty() ->
            Text("No cancelled reservations.")

        else -> LazyColumn {
            items(cancellations) { (user, reservation) ->

                val campsiteName = campsites
                    .find { it.id == reservation.campsiteId }
                    ?.name ?: reservation.campsiteId

                var expanded by remember {
                    mutableStateOf(false)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Campsite name
                    Text(
                        text = campsiteName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Reservation status
                    Text(
                        text = "Status: Cancelled",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Reservation summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Campsite image",
                            modifier = Modifier.size(42.dp)
                        )

                        Column {
                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                fontSize = 12.sp
                            )

                            Text(
                                text = "${reservation.startReservedDate} - ${reservation.endReservedDate}",
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Details button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { expanded = !expanded }
                        ) {
                            Text(
                                text = if (expanded) "Hide Details"
                                else "View Details",
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (expanded) {
                        Text("Guests: ${reservation.numOfGuests}")
                        Text("Reservation ID: ${reservation.reservationId}")
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}