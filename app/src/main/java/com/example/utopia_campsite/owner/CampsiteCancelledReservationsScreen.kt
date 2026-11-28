package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.user.Reservation
import com.example.utopia_campsite.user.User

/**
 *
 * */
@Composable
fun CampsiteCancelledReservationsScreen(
    owner: Owner,
) {
    var cancellations by remember { mutableStateOf<List<Pair<User, Reservation>>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(owner.deviceID) {
        OwnerRepository().getCancelledReservations(
            owner = owner,
            onSuccess = {
                cancellations = it
            },
            onFailure = {
                error = it.message ?: "Failed to load cancellations."
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        when {
            error != null -> Text(error!!)

            cancellations.isEmpty() ->
                Text("No cancelled reservations.")

            else -> LazyColumn {
                items(cancellations) { (user, reservation) ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("User: ${user.firstName} ${user.lastName}")
                        Text("Campsite ID: ${reservation.campsiteId}")
                        Text("Start: ${reservation.startReservedDate}")
                        Text("End: ${reservation.endReservedDate}")
                        Text("Guests: ${reservation.numOfGuests}")
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}