/**
 * Utopia 01.01.05
 * Shows the details of one campsite: photo, name, location, description, tags,
 * maximum occupancy, rating and available dates
 *
 * Outstanding Issues:
 * The join/leave waitlist buttons (US 01.01.06) are commented out, they need the current user which this screen does not get yet
 */

package com.example.utopia_campsite.user

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
import com.example.utopia_campsite.user.util.CampsiteDetailsContent

/**
 * Shows every detail of one campsite, opened by tapping it in the campsite list
 *
 * @param campsite: the campsite to show
 * @param onBack: called when the back button is pressed
 * @param onPostReview: called when the post review button is pressed
 */
@Composable
fun CampsiteDetailsScreen(
    campsite: Campsites,
    onBack: (() -> Unit)? = null,
    onPostReview: ((String, () -> Unit, (Exception) -> Unit) -> Unit)? = null,
    onSubmitRating: ((Int, () -> Unit, (Exception) -> Unit) -> Unit)? = null
) {
    CampsiteDetailsContent(
        campsite = campsite,
        onBack = onBack,
        onPostReview = onPostReview,
        onSubmitRating = onSubmitRating
    )
}