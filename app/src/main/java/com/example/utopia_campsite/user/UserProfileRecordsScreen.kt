/**
 * Displays a table of the user's reservations (current and past) and a table of their cancelled reservations
 *  Each table can be downloaded as a CSV file
 *  Utopia 01.05.02, 01.07.02
 */

package com.example.utopia_campsite.user

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utopia_campsite.owner.CampsiteRepository

/**
 * Shows the reservations table and the cancellations table
 * @param user: the user whose reservations are shown
 */
@Composable
fun UserProfileRecordsScreen(user: User) {
    var campsiteNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    //load campsite names once so the tables and CSVs show names instead of ids
    LaunchedEffect(Unit) {
        CampsiteRepository().getCampsites(
            onSuccess = { campsites -> campsiteNames = campsites.associate { it.id to it.name } },
            onFailure = {} //ids are shown instead of names
        )
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ReservationTable(
            title = "Reservations",
            reservations = user.currentReservations + user.pastReservations,
            campsiteNames = campsiteNames,
            fileName = "reservations.csv"
        )
        ReservationTable(
            title = "Cancellations",
            reservations = user.cancelledReservations,
            campsiteNames = campsiteNames,
            fileName = "cancellations.csv"
        )
    } //end Column
} //end UserProfileRecordsScreen

/**
 * Shows one table of reservations with a button to download it as a CSV file
 * @param title: the heading shown above the table
 * @param reservations: the reservations shown in the table
 * @param campsiteNames: campsite id to campsite name
 * @param fileName: the suggested name of the downloaded CSV file
 */
@Composable
private fun ReservationTable(
    title: String,
    reservations: List<Reservation>,
    campsiteNames: Map<String, String>,
    fileName: String
) {
    val context = LocalContext.current

    //opens the system "save as" screen, so no storage permission is needed
    val saveCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult //user backed out
        try {
            context.contentResolver.openOutputStream(uri)?.use {
                it.write(reservationsToCsv(reservations, campsiteNames).toByteArray())
            }
            Toast.makeText(context, "Saved $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Couldn't save $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        TableRow(listOf("Campsite", "Start", "End", "Guests"), bold = true)
        HorizontalDivider()
        if (reservations.isEmpty()) {
            Text("No ${title.lowercase()}")
        }
        for (r in reservations) {
            TableRow(listOf(campsiteNames[r.campsiteId] ?: r.campsiteId, r.startReservedDate, r.endReservedDate, r.numOfGuests.toString()))
        }
        Button(onClick = { saveCsv.launch(fileName) }) {
            Text("Download CSV")
        }
    } //end Column
} //end ReservationTable

/**
 * Shows one row of a table with equal width columns
 * @param cells: the text of each column
 * @param bold: whether the text is bold, used for the header row
 */
@Composable
private fun TableRow(cells: List<String>, bold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (cell in cells) {
            Text(
                text = cell,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
} //end TableRow
