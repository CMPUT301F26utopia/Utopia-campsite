/**
 * Shows the details of one campsite to an admin, using the shared CampsiteDetailsContent,
 * plus buttons to remove the campsite's image and to delete the whole campsite
 *
 * Outstanding Issues:
 * Reservations are matched to the campsite by name, which breaks if two campsites share a name
 */

package com.example.utopia_campsite.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.util.CampsiteDetailsContent

/**
 * Shows every detail of one campsite, opened by clicking the card in AdminCampsiteDetailsScreen
 * Admins can remove the campsite's image or delete the whole campsite
 *
 * @param campsite: the campsite to show
 * @param navController: the navigation controller, used to go back to the list after a delete
 */
@Composable
fun AdminCampsiteDetailScreen(
    campsite: Campsites,
    navController: NavController
) {
    val campsiteRepository = remember { CampsiteRepository() }

    // Whether the "are you sure?" popup for deleting is showing
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Set once the photo has been removed in the database, so this screen stops showing it
    var photoRemoved by remember(campsite.id) { mutableStateOf(false) }
    val shownCampsite = if (photoRemoved) campsite.copy(photoUrl = null) else campsite

    // Message shown if removing the image or deleting fails
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column {
        CampsiteDetailsContent(campsite = shownCampsite)

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        } //end if

        // Admin-only actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nothing to remove if the campsite has no image
            if (!shownCampsite.photoUrl.isNullOrBlank()) {
                Button(onClick = {
                    errorMessage = null
                    campsiteRepository.removeCampsitePhoto(
                        campsiteId = campsite.id,
                        onSuccess = { photoRemoved = true },
                        onFailure = { errorMessage = "Could not remove image" }
                    )
                }) {
                    Text("Remove image")
                } // end button
            } //end if
            Button(onClick = { showDeleteDialog = true }) {
                Text("Delete campsite")
            } // end button
        } // end row
    } //end Column

    // Ask for confirmation first, since deleting a campsite can't be undone
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false }, // tapping outside cancels
            title = { Text("Delete campsite?") },
            text = { Text("This will permanently delete ${campsite.name} and cancel its reservations.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    errorMessage = null
                    campsiteRepository.deleteCampsite(
                        campsite = campsite,
                        onSuccess = { navController.popBackStack() },
                        onFailure = { errorMessage = "Could not delete campsite" }

                    )
                }) {
                    Text("Delete")
                } // end button
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                } // end button
            }
        ) // end AlertDialog
    } //end if
} //end AdminCampsiteDetailScreen