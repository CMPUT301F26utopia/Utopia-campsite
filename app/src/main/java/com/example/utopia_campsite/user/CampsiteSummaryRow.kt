package com.example.utopia_campsite.user

// Utopia 01.01.04
// Shows the summary of one campsite in a list: name, location, maximum occupancy and rating
// Outstanding issues: not shown on any screen yet, needs the campsite list (01.01.03)、

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

// Utopia 01.01.04

@Composable
fun CampsiteSummaryRow(campsite: Campsites){
    val rating = if (campsite.ratings.isEmpty()) "No ratings yet" else "%.2f".format(campsite.ratings.average()) //format rounds the rating, returns a string
    // Show the average of all ratings, or a message if the campsite has none yet
    // (average() of an empty list gives NaN)

    // Stack the four pieces of summary information from top to bottom
    Column {
        if(!campsite.photoUrl.isNullOrBlank()){
            AsyncImage(
                model =campsite.photoUrl,
                contentDescription = "Image of ${campsite.name}",
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(text = campsite.name, modifier=Modifier.padding(start = 12.dp))
        Text(text = campsite.address, modifier=Modifier.padding(start = 12.dp))
        Text(text = "Max occupancy: ${campsite.maximumOccupants}", modifier=Modifier.padding(start = 12.dp))
        Text(text = "Rating: $rating", modifier=Modifier.padding(start = 12.dp))
    }
} //end CampsiteSummaryRow