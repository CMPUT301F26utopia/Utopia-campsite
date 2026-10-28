package com.example.utopia_campsite.user

import androidx.compose.foundation.clickable
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
fun CurrentReservationsList(reservations: List<Reservation>) {
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