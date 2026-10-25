package com.example.utopia_campsite.user

// Utopia 01.01.04
// Shows the summary of one campsite in a list: name, location, maximum occupancy and rating
// Outstanding issues: not shown on any screen yet, needs the campsite list (01.01.03)、

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.utopia_campsite.owner.Campsites

// Utopia 01.01.04

@Composable
fun CampsiteSummaryRow(campsite: Campsites){
    val rating = if (campsite.ratings.isEmpty()) "No ratings yet" else campsite.ratings.average().toString()
    // Show the average of all ratings, or a message if the campsite has none yet
    // (average() of an empty list gives NaN)

    // Stack the four pieces of summary information from top to bottom
    Column {
        Text(text = campsite.name)
        Text(text = campsite.address)
        Text(text = "Max occupancy: ${campsite.maximumOccupants}")
        Text(text = "Rating: $rating")
    }
} //end CampsiteSummaryRow