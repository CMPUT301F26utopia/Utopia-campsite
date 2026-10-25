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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Method to showcase the calander screen for both start date and end date button
@Composable
fun CalendarViewScreen(label: String, date: String, onDateChange: (String) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label)
        TextField(
            value = date,
            onValueChange = onDateChange,
            placeholder = { Text("DD/MM/YYYY") },
            trailingIcon = {
                IconButton(
                    onClick = { showDatePicker = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select $label"
                    )
                }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            onDateChange(formatter.format(Date(millis)))
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// The main screen of this page
@Composable
fun UserFilterScreen(modifier: Modifier, filterViewModel: UserCampsiteFilterViewModel, navController: NavController) {
    var inputLocation by remember { mutableStateOf("") }
    var inputGuests by remember { mutableStateOf("") }
    var inputStartDate by remember { mutableStateOf("") }
    var inputEndDate by remember { mutableStateOf("") }

    var noMatchesFound by remember { mutableStateOf(false) }

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
                    value = inputLocation,
                    onValueChange = {inputLocation = it},
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
            CalendarViewScreen("Start Date", date = inputStartDate, onDateChange = { inputStartDate = it })
            // Input end date field inner-column
            CalendarViewScreen("End Date", date = inputEndDate, onDateChange = { inputEndDate = it })
        }

        Spacer(modifier = modifier.padding(32.dp))

        // On click, it should use navController to go to UserFilterResultScreen
        Button(
            onClick = {
                val results = filterViewModel.filterCampsites(
                    userLocation = inputLocation,
                    userGuests = inputGuests,
                    userStartDate = inputStartDate,
                    userEndDate = inputEndDate
                )

                if (results.isEmpty()) {
                    noMatchesFound = true
                } else {
                    filterViewModel.updateFilteredCampsites(results)
                    noMatchesFound = false
                    navController.navigate("user/filter/result")
                }

            }
        ) {
            Text("Apply Filters")
        }

        // Display message if no filtered results found
        if (noMatchesFound) {
            Text("No matching campsites found")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterScreenPreview() {
    val navController = rememberNavController()
    val filterViewModel = remember { UserCampsiteFilterViewModel() }

    UserFilterScreen(
        modifier = Modifier,
        filterViewModel = filterViewModel,
        navController = navController
    )
}
