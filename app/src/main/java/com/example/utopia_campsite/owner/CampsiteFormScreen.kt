/**
 * Utopia 02.01.02, 02.04.05
 * Lets a campsite owner provide a campsite's details (name, location, description,
 * maximum occupancy, map location, optional image, tags) and set the number of lottery winners
 *
 * Outstanding Issues:
 * Not reachable yet, needs an owner route in UtopiaNavApp
 * The image is a URL and the map location is typed in as latitude and longitude
 * The saved campsite is not linked to its owner yet (Campsites has no owner ID)
 */

package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shows the form a campsite owner fills in to provide a campsite's details
 *
 * @param onSubmit: called with the new campsite when every required field is filled in correctly
 * @param modifier: optional layout changes from the screen that uses the form
 */
@Composable
fun CampsiteFormScreen(onSubmit: (Campsites) -> Unit, modifier: Modifier = Modifier){
    // What the owner has typed so far
    // remember keeps the text when the screen redraws, mutableStateOf redraws the screen when it changes
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var maxOccupancy by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var lotteryWinners by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize() // take up the whole screen
            .padding(16.dp) // keep the fields away from the screen edges
            .verticalScroll(rememberScrollState()) // scroll so the save button can be reached on a small screen
    ) {
        // Each FormField is one text box: FormField(label, current text) { save the new text }
        // { name = it } means "when the owner types, store the new text (it) in name"

        // US 02.01.02: the campsite's details
        FormField("Name", name) { name = it }
        FormField("Location (address)", address) { address = it }
        FormField("Description (amenities)", description) { description = it }
        FormField("Maximum occupancy", maxOccupancy) { maxOccupancy = it }
        FormField("Map location: latitude", latitude) { latitude = it }
        FormField("Map location: longitude", longitude) { longitude = it }
        FormField("Image URL (optional)", photoUrl) { photoUrl = it }
        FormField("Tags (separate with commas)", tags) { tags = it }

        // US 02.04.05: how many entrants win the lottery
        FormField("Number of lottery winners", lotteryWinners) { lotteryWinners = it }

        Button(
            onClick = {
                // Check what was typed and turn it into a campsite
                // name = name means "the name parameter gets the name text box's text", and so on
                val campsite = buildCampsite(
                    name = name,
                    address = address,
                    description = description,
                    maxOccupancy = maxOccupancy,
                    latitude = latitude,
                    longitude = longitude,
                    photoUrl = photoUrl,
                    tags = tags,
                    lotteryWinners = lotteryWinners
                )
                // buildCampsite gives back null when a required field is missing or not a valid number,
                // so nothing is saved until every field is right
                if (campsite != null) {
                    onSubmit(campsite) // hand the campsite to whoever shows this form, e.g. to save it
                }
            }
        ) {
            Text("SAVE CAMPSITE")
        }
    } //end Column
} //end CampsiteFormScreen

/**
 * Shows one labelled text box of the form, as wide as the screen
 *
 * @param label: the text shown on the box
 * @param value: what the owner has typed so far
 * @param onValueChange: called with the new text each time the owner types
 */
@Composable
private fun FormField(label: String, value: String, onValueChange: (String) -> Unit){
    OutlinedTextField(
        value = value, // the text shown in the box
        onValueChange = onValueChange, // pass each new text back to the form so it can store it
        label = { Text(label) }, // the small title on the box
        modifier = Modifier.fillMaxWidth() // as wide as the screen
    )
} //end FormField

/**
 * Turns what the owner typed into a new campsite
 * Only the image is optional, so every other field must be filled in
 * and the number fields must hold valid numbers
 *
 * @return the new campsite, or null if a required field is missing or not a valid number
 */
fun buildCampsite(
    name: String,
    address: String,
    description: String,
    maxOccupancy: String,
    latitude: String,
    longitude: String,
    photoUrl: String,
    tags: String,
    lotteryWinners: String
): Campsites?{
    // Turn the typed text into numbers
    // toIntOrNull / toDoubleOrNull give null instead of crashing when the text is not a number
    val occupancyNumber = maxOccupancy.toIntOrNull()
    val latitudeNumber = latitude.toDoubleOrNull()
    val longitudeNumber = longitude.toDoubleOrNull()
    val winnersNumber = lotteryWinners.toIntOrNull()

    // Split the tags at each comma, remove the spaces around each tag, and drop empty pieces
    // e.g. " lake, , hiking " becomes ["lake", "hiking"]
    val tagList = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    // true if any required text field is empty (tags made of only commas count as empty)
    val textMissing = name.isBlank() || address.isBlank() || description.isBlank() || tagList.isEmpty()
    // true if any number field could not be read as a number
    val numberInvalid = occupancyNumber == null || latitudeNumber == null || longitudeNumber == null || winnersNumber == null
    if (textMissing || numberInvalid) {
        return null
    }
    // After this check Kotlin knows the four numbers are not null, so they can be used directly below

    return Campsites(
        id = "", // Firestore makes the ID when the campsite is saved
        name = name,
        photoUrl = photoUrl.ifBlank { null }, // no image if left empty
        address = address,
        latitude = latitudeNumber,
        longitude = longitudeNumber,
        ratings = mutableListOf(), // a new campsite has no ratings
        tags = tagList.toMutableList(), // Campsites stores tags as a MutableList
        lotteryWinners = winnersNumber,
        lotteryWaitlist = mutableListOf(), // nobody has joined yet
        description = description,
        registrationDate = "", // set by US 02.04.03
        bookingDate = "", // set by US 02.04.04
        maximumOccupants = occupancyNumber,
        availableStartDate = "", // not part of this story
        availableEndDate = "", // not part of this story
        usersBooked = mutableListOf() //// not part of this story
    )
} //end buildCampsite