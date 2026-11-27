/**
 * Utopia 01.01.04
 * Shows the summary of one campsite in the campsite list: name, location, maximum occupancy and rating
 *
 * Outstanding Issues:
 * None
 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.Campsites

/**
 * Shows one campsite's photo (if it has one), name, address, maximum occupancy and average rating
 *
 * @param campsite: the campsite to summarize
 */

@Composable
fun CampsiteSummaryRow(campsite: Campsites){
    val rating = if (campsite.ratings.isEmpty()) "No ratings yet" else "%.2f".format(campsite.ratings.values.average()) //format rounds the rating, returns a string
    // Show the average of all ratings, or a message if the campsite has none yet
    // (average() of an empty list gives NaN)

    // Stack the photo and the summary information from top to bottom
    Column {
        if(!campsite.photoUrl.isNullOrBlank()){
            AsyncImage(
                model =campsite.photoUrl,
                contentDescription = "Image of ${campsite.name}",
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
        }
        // Stack the four pieces of summary information from top to bottom,
        // with 16dp around them so they don't touch the card's edges, and 4dp between the lines
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = campsite.name,
                style = MaterialTheme.typography.titleLarge, // bigger than the other lines
                fontWeight = FontWeight.Bold // so the name reads as the card's title
            )
            Text(text = campsite.address)
            Text(text = "Max occupancy: ${campsite.maximumOccupants}")
            Text(text = "Rating: $rating")
        } //end Column
    }
} //end CampsiteSummaryRow