/**
 * Shared campsite details UI used by both the user and admin campsite detail screens
 *
 * Shows the photo, name, location, description, tags, maximum occupancy, rating, available dates and the campsite's reviews. Entrants can also post a review.
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.utopia_campsite.user.UserCampsiteRating
import com.google.firebase.auth.FirebaseAuth

/**
 * Shows every detail of one campsite, followed by its reviews
 *
 * @param campsite: the campsite to show
 * @param onBack: called when the back button is pressed, the back button is hidden if this is null
 * @param onPostReview: called with the review text plus success/failure callbacks.
 *                      If this is null (e.g. the admin screen), reviews are shown but cannot be posted
 */
@Composable
fun CampsiteDetailsContent(
    campsite: Campsites,
    onBack: (() -> Unit)? = null,
    onPostReview: ((reviewText: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) -> Unit)? = null,
    onSubmitRating: ((Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) -> Unit)? = null
) {
    // Show the average of all ratings, or a message if the campsite has none yet
    // (average() of an empty list gives NaN)
    val rating = if (campsite.ratings.isEmpty()) "No ratings yet" else "%.2f".format(campsite.ratings.values.average())
    var checkedState by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }
    var postError by remember { mutableStateOf<String?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var isSubmittingRating by remember { mutableStateOf(false) }
    var ratingError by remember { mutableStateOf<String?>(null) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val previousRating = uid?.let { campsite.ratings[it] } ?: 0
    val hasRated = previousRating > 0

    // Stack the campsite's details from top to bottom
    Column(modifier = Modifier.fillMaxSize()) {
        // Back button stays at the top while the rest of the page scrolls
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        } //end if

        // The whole page is one LazyColumn so the details and the reviews scroll together
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Only show the image if the owner added one (photoUrl can be null or blank)
            if (!campsite.photoUrl.isNullOrBlank()) {
                item {
                    AsyncImage(
                        model = campsite.photoUrl, // loads the image from its URL
                        contentDescription = "Image of ${campsite.name}", // read out by screen readers
                        modifier = Modifier.fillMaxWidth(), // as wide as the screen
                        contentScale = ContentScale.FillWidth // keeps the image's own shape, nothing cut off
                    )
                }
            } //end if

            // Campsite details, padded so the text doesn't touch the edge of the phone
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = campsite.name, style = MaterialTheme.typography.headlineSmall) // campsite name
                    Text(text = campsite.address, style = MaterialTheme.typography.bodyMedium) // location of the campsite
                    Text(text = campsite.description) // description of the campsite and its amenities
                    Text(text = "Tags: ${campsite.tags.joinToString(", ")}") // all tags on one line
                    Text(text = "Max occupancy: ${campsite.maximumOccupants}") // most people the campsite can hold

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Rating: $rating")
                        if (onSubmitRating != null) {
                            Button(
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("addRatingButton"),
                                onClick = {
                                    showRatingDialog = true
                                },
                                contentPadding = PaddingValues(
                                    horizontal = 10.dp,
                                    vertical = 0.dp
                                )
                            ) {
                                Text(
                                    text = if (hasRated) "Edit Rating" else "Add Rating",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    ratingError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(text = "Available: ${campsite.availableStartDate} - ${campsite.availableEndDate}") // dates the campsite can be booked

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedState,
                            onCheckedChange = { checkedState = it }
                        )
                        Text(text = "I confirm that I have read the terms and conditions.")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { /*TODO*/ },
                            enabled = checkedState
                        ) {
                            Text(text = "Join Lottery")
                        }
                    }
                }
            }

            // Reviews header and, for entrants, the box to write a new review
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Reviews", style = MaterialTheme.typography.titleMedium)

                    if (onPostReview != null) {
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it; postError = null },
                            label = { Text("Write a review") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            maxLines = 5
                        )
                        postError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    isPosting = true
                                    onPostReview(
                                        reviewText.trim(),
                                        { isPosting = false; reviewText = "" },
                                        { isPosting = false; postError = "Couldn't post your review, try again" }
                                    )
                                },
                                enabled = reviewText.isNotBlank() && !isPosting
                            ) {
                                Text(text = "Post Review")
                            }
                        }
                    } //end if
                }
            }

            // The reviews themselves, newest first
            if (campsite.reviews.isEmpty()) {
                item {
                    Text(
                        text = "No reviews yet.",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(campsite.reviews.asReversed()) { review ->
                    // reviews are stored as "Name: review text", so split at the first ": " to style them separately
                    val reviewerName = review.substringBefore(": ", missingDelimiterValue = "")
                    val reviewBody = review.substringAfter(": ")

                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (reviewerName.isNotBlank()) {
                                Text(
                                    text = reviewerName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            } //end if
                            Text(text = reviewBody, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } //end if
        } //end LazyColumn
    } //end Column

    // Adding Rating Button
    if (showRatingDialog && onSubmitRating != null) {
        UserCampsiteRating(
            modifier = Modifier,
            initialRating = previousRating,
            onDismiss = {
                if (!isSubmittingRating) {
                    showRatingDialog = false
                }
            },
            onSubmit = { selectedRating ->
                if (!isSubmittingRating) {
                    isSubmittingRating = true
                    ratingError = null

                    onSubmitRating(
                        selectedRating,
                        // On success
                        {
                            isSubmittingRating = false
                            showRatingDialog = false
                        },
                        // On Failure
                        {
                            isSubmittingRating = false
                            ratingError = "Couldn't submit rating"
                        }
                    )
                }
            }
        )
    }
} //end CampsiteDetailsContent