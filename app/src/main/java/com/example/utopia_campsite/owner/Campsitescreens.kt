/**
 * Owner details UI for US 02.01.04, based on the owner-specific Figma view.
 */

package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays the selected campsite's details and switches to the editing form
 * when the owner clicks "Edit campsite".
 *
 * Implements US 02.01.04 and provides access to US 02.01.03.
 * Updates the displayed details only after saving succeeds.
 *
 * @param campsite the selected campsite whose details are displayed
 * @param onBack called when the owner clicks the Back button
 * @param onSave called with the edited campsite and a completion callback;
 *               the caller saves the changes and invokes the callback with
 *               null on success or an error message on failure
 */

@Composable
fun CampsiteScreens(
    campsite: Campsites,
    onBack: () -> Unit,
    onSave: (Campsites, (String?) -> Unit) -> Unit
) {
    // Switch between the details screen and the editing form.
    var editing by rememberSaveable(campsite.id) { mutableStateOf(false) }

    // Keep the displayed details updated after a successful save.
    var displayedCampsite by remember(campsite) { mutableStateOf(campsite) }

    if (editing) {
        EditCampsiteScreen(
            campsite = displayedCampsite,
            onCancel = { editing = false },
            onSave = { updated, finished ->
                onSave(updated) { error ->
                    // Close the editor only when saving succeeds.
                    if (error == null) {
                        displayedCampsite = updated
                        editing = false
                    }
                    // Pass the result back to the editor.
                    finished(error)
                }
            }
        )
    } else {
        // Display the campsite information in a scrollable column.
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("Back")
            }

            Text("Campsite details")
            Text("Name: ${displayedCampsite.name}")
            Text("Address: ${displayedCampsite.address}")
            Text("Description: ${displayedCampsite.description}")
            Text("Maximum occupants: ${displayedCampsite.maximumOccupants}")
            Text("Tags: ${displayedCampsite.tags.joinToString(", ")}")
            Text("Latitude: ${displayedCampsite.latitude}")
            Text("Longitude: ${displayedCampsite.longitude}")

            Text(
                "Available: ${displayedCampsite.availableStartDate}" +
                        " - ${displayedCampsite.availableEndDate}"
            )

            Button(onClick = { editing = true }) { Text("Edit campsite")}
        }
    }
}

/**
 * Shows a form pre-filled with the selected campsite's existing details.
 *
 * Implements US 02.01.03.
 * Validates required text, maximum occupancy, and geographic coordinates
 * before submitting changes. Preserves fields that are not being edited.
 * Disables the inputs and buttons while a save request is pending.
 *
 * @param campsite the existing campsite used to populate the input fields
 * @param onCancel called when the owner cancels editing without saving
 * @param onSave called with the updated campsite and a completion callback;
 *               the callback stops the saving state and supplies an error
 *               message if the save fails
 */

@Composable
private fun EditCampsiteScreen(
    campsite: Campsites,
    onCancel: () -> Unit,
    onSave: (Campsites, (String?) -> Unit) -> Unit
) {
    // Pre-fill the inputs and retain their values during screen recreation.
    var name by rememberSaveable(campsite.id) { mutableStateOf(campsite.name) }
    var address by rememberSaveable(campsite.id) { mutableStateOf(campsite.address) }
    var description by rememberSaveable(campsite.id) { mutableStateOf(campsite.description) }

    // Text fields require Strings, so convert numeric values to text.
    var occupancy by rememberSaveable(campsite.id) { mutableStateOf(campsite.maximumOccupants.toString()) }
    var tags by rememberSaveable(campsite.id) { mutableStateOf(campsite.tags.joinToString(", ")) }
    var latitude by rememberSaveable(campsite.id) { mutableStateOf(campsite.latitude.toString()) }
    var longitude by rememberSaveable(campsite.id) { mutableStateOf(campsite.longitude.toString()) }

    // Track the pending save request and any error message.
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Edit campsite")

        // Disable inputs while saving to prevent further changes.
        CampsiteInput("Name", name, !saving) { name = it }
        CampsiteInput("Address", address, !saving) { address = it }
        CampsiteInput("Description", description, !saving) { description = it }
        CampsiteInput("Maximum occupants", occupancy, !saving) { occupancy = it }
        CampsiteInput("Tags, separated by commas", tags, !saving) { tags = it }
        CampsiteInput("Latitude", latitude, !saving) { latitude = it }
        CampsiteInput("Longitude", longitude, !saving) { longitude = it }

        // Display an error only when a message exists.
        error?.let { Text(it) }

        Button(
            enabled = !saving,
            onClick = {
                // Convert numeric inputs safely; invalid text produces null.
                val number = occupancy.trim().toIntOrNull()
                val lat = latitude.trim().toDoubleOrNull()
                val lon = longitude.trim().toDoubleOrNull()

                // Check required fields, occupancy, and coordinate ranges.
                val validationError = when {
                    name.isBlank() || address.isBlank() ||
                            description.isBlank() ->
                        "Please enter a name, address and description."

                    number == null || number <= 0 ->
                        "Maximum occupants must be a positive whole number."

                    lat == null || !lat.isFinite() ||
                            lat !in -90.0..90.0 ->
                        "Latitude must be between -90 and 90."

                    lon == null || !lon.isFinite() ||
                            lon !in -180.0..180.0 ->
                        "Longitude must be between -180 and 180."

                    else -> null
                }

                if (validationError != null) {
                    error = validationError
                } else {
                    // Replace editable fields while preserving all other data.
                    val updated = campsite.copy(
                        name = name.trim(),
                        address = address.trim(),
                        description = description.trim(),
                        maximumOccupants = number!!,

                        // Convert comma-separated tags into a clean, unique list.
                        tags = tags.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .distinct(),

                        latitude = lat!!,
                        longitude = lon!!
                    )

                    saving = true
                    error = null

                    // Request saving and wait for the caller's result.
                    onSave(updated) { saveError ->
                        saving = false
                        error = saveError
                    }
                }
            }
        ) {
            Text(if (saving) "Saving..." else "Save changes")
        }

        // Cancel editing without submitting the changes.
        TextButton(
            enabled = !saving,
            onClick = onCancel
        ) {
            Text("Cancel")
        }
    }
}

/**
 * Shows one labelled text box using the available screen width.
 *
 * Displays the supplied value and reports text changes to the caller.
 * The caller is responsible for storing the updated value.
 *
 * @param label the label shown on the text box
 * @param value the current text displayed in the text box
 * @param enabled whether the owner can edit the text box
 * @param onChange called with the new text whenever the owner changes it
 */

@Composable
private fun CampsiteInput(
    label: String,
    value: String,
    enabled: Boolean,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    )
}