package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.owner.Campsites
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class UserFilterScreen {

    private val availableCampsites = ArrayList<Campsites>() // This will be retrieved from firebase

    private fun convertStringDate(date: String): Date? {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            formatter.parse(date)
        } catch (e: ParseException) {
            null
        }
    }

    private fun filterCampsites(userLocation: String, userGuests: String, userStartDate: String, userEndDate: String): List<Campsites> {

        val guests = userGuests.toIntOrNull()

        val startDate = if (userStartDate.isNotBlank()) {
            convertStringDate(userStartDate)
        } else null

        val endDate = if (userEndDate.isNotBlank()) {
            convertStringDate(userEndDate)
        } else null

        // Reject invalid inputs, but allow empty fields
        if (userGuests.isNotBlank() && (guests == null || guests <= 0)) return emptyList()
        if (userStartDate.isNotBlank() && startDate == null) return emptyList()
        if (userEndDate.isNotBlank() && endDate == null) return emptyList()
        if (startDate != null && endDate != null && startDate.after(endDate)) return emptyList()

        return availableCampsites.filter { campsite ->

            val matchesLocation = userLocation.isBlank() ||
                    campsite.location.equals(userLocation, ignoreCase = true)

            val matchesNumGuests = guests == null || campsite.maximumOccupants >= guests

            // Guaranteed to be true
            val availableStart = convertStringDate(campsite.availableStartDate)
            val availableEnd = convertStringDate(campsite.availableEndDate)

            val matchesStartDate = startDate == null || (availableStart != null && !startDate.before(availableStart))

            val matchesEndDate = endDate == null || (availableEnd != null && !endDate.after(availableEnd))

            matchesLocation && matchesNumGuests && matchesStartDate && matchesEndDate
        }
    }

    @Composable
    fun FilterScreen(modifier: Modifier) {
        var inputAddress by remember { mutableStateOf("") }
        var inputGuests by remember { mutableStateOf("") }
        var inputStartDate by remember { mutableStateOf("") }
        var inputEndDate by remember { mutableStateOf("") }

        // Create a date state, for the case when a user presses on the calendar button to show the calendar, the returned value is a Long?
        var showDatePicker by remember { mutableStateOf(false)}


        Column(modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = modifier.padding(16.dp))
            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {} // This will be used to go back to the user HOME page
                ) {
                    Text("Back")
                }
            }

            Spacer(modifier = modifier.padding(16.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = modifier.size(32.dp),
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )

                Icon(
                    modifier = modifier.size(32.dp),
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "Filter Icon"
                )
            }

            // Input Field Section
            Column(verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Input address field inner-column
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("Location")
                    TextField(
                        value = inputAddress,
                        onValueChange = {inputAddress = it},
                        placeholder = { Text("Enter a city of address")},
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.AddLocation,
                                contentDescription = "Pin Icon"
                            )
                        }
                    )
                }

                // Input guests field inner-column
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("Number of Guests")
                    TextField(
                        value = inputGuests,
                        onValueChange = {inputGuests = it},
                        placeholder = { Text("Number of Guests")},
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.PersonOutline,
                                contentDescription = "Pin Icon"
                            )
                        }
                    )
                }

                // Input start date field inner-column
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("Start Date")
                    TextField(
                        value = inputStartDate,
                        onValueChange = {inputStartDate = it},
                        placeholder = { Text("DD/MM/YYYY")},
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Start Date Icon"
                            )
                        }
                    )
                }

                // Input end date field inner-column
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("End Date")
                    TextField(
                        value = inputEndDate,
                        onValueChange = {inputEndDate = it},
                        placeholder = { Text("DD/MM/YYYY")},
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "End Date Icon"
                            )
                        }
                    )
                }
            }

            Text("Main Content Body")

            // On click, it should use navController to go back to home
            Button(
                onClick = { }
            ) {
                Text("Back")
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun FilterScreenPreview() {
    UserFilterScreen().FilterScreen(modifier = Modifier)
}