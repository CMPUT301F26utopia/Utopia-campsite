package com.example.utopia_campsite.user

// Utopia 01.01.05
// Shows the details of one campsite
// Outstanding issues: not reachable from the list yet (needs navigation)

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.Campsites

@Composable
fun CampsiteDetailsScreen(campsite: Campsites){
    // Show the average of all ratings, or a message if the campsite has none yet
    // (average() of an empty list gives NaN)
    val rating = if (campsite.ratings.isEmpty()) "No ratings yet" else "%.2f".format(campsite.ratings.average())

    // Stack the campsite's details from top to bottom
    Column {
        // Only show the image if the owner added one (photoUrl is null otherwise)
        if (campsite.photoUrl != null) {
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
        Text(text = "Tags: ${campsite.tags.joinToString(", ")}") // all tags on one line, separated by commas
        Text(text = "Max occupancy: ${campsite.maximumOccupants}") // most people the campsite can hold
        Text(text = "Rating: $rating") // average rating, or "No ratings yet"
        Text(text = "Available: ${campsite.availableStartDate} - ${campsite.availableEndDate}") // dates the campsite can be booked
        /*
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (user.deviceID in campsite.lotteryWaitlist){
                Button(
                    onClick = { /* leave the waitlist */}
                ) {
                    Text("Leave waitlist")
                } // end button
            } else {
                Button(
                    onClick = { /* join the waitlist */}
                ) {
                    Text("Join waitlist")
                } // end button
            } // end if-else block

        } // end row*/
    } //end Column
} //end CampsiteDetailsScreen