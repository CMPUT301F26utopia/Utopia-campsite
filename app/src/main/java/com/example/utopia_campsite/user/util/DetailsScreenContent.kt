/**
 * Shared campsite details UI used by both the user and admin campsite detail screens
 *
 * Shows the photo, name, location, description, tags, maximum occupancy, rating and available dates.
 *
 * Outstanding Issues:
 * None known
 */

package com.example.utopia_campsite.user.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.Campsites

/**
 * Shows every detail of one campsite
 *
 * @param campsite: the campsite to show
 */
@Composable
fun CampsiteDetailsContent(campsite: Campsites) {
    // Show the average of all ratings, or a message if the campsite has none yet
    // (average() of an empty list gives NaN)
    val rating = if (campsite.ratings.isEmpty()) "No ratings yet" else "%.2f".format(campsite.ratings.average())
    var checkedState by remember { mutableStateOf(false) }

    // Stack the campsite's details from top to bottom
    Column {
        // Only show the image if the owner added one (photoUrl can be null or blank)
        if (!campsite.photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = campsite.photoUrl, // loads the image from its URL
                contentDescription = "Image of ${campsite.name}", // read out by screen readers
                modifier = Modifier.fillMaxWidth(), // as wide as the screen
                contentScale = ContentScale.FillWidth // keeps the image's own shape, nothing cut off
            )
        } //end if
        Text(text = campsite.name) // campsite name
        Text(text = campsite.address) // location of the campsite
        Text(text = campsite.description) // description of the campsite and its amenities
        Text(text = "Tags: ${campsite.tags.joinToString(", ")}") // all tags on one line
        Text(text = "Max occupancy: ${campsite.maximumOccupants}") // most people the campsite can hold
        Text(text = "Rating: $rating") // average rating, or "No ratings yet"
        Text(text = "Available: ${campsite.availableStartDate} - ${campsite.availableEndDate}") // dates the campsite can be booked

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Checkbox(
                checked = checkedState,
                onCheckedChange = { checkedState = it }
            )
            Text(text = "I confirm that I have read the terms and conditions.")
        }

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            Button(onClick = { /*TODO*/ },
                    enabled = checkedState
            ) {
                Text(text = "Join Lottery")
            }
        }

    } //end Column
} //end CampsiteDetailsContent