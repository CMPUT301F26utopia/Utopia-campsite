package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun determineCampsiteStatus(campsite: Campsites): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    dateFormat.isLenient = false
    val today = dateFormat.parse(dateFormat.format(Date()))!!

    // Split registration and booking date ranges, handling edge case
    val registrationDates = campsite.registrationDate.split("-")
    val bookingDates = campsite.bookingDate.split("-")
    if (registrationDates.size != 2 || bookingDates.size != 2) {
        return "Invalid Dates"
    }

    // Convert strings into Date objects
    val registrationStart = dateFormat.parse(registrationDates[0].trim())
    val registrationEnd = dateFormat.parse(registrationDates[1].trim())
    val bookingStart = dateFormat.parse(bookingDates[0].trim())
    val bookingEnd = dateFormat.parse(bookingDates[1].trim())

    if (registrationStart == null || registrationEnd == null ||
        bookingStart == null || bookingEnd == null) {
        return "Invalid Dates"
    }

    // Determine current status
    if (!today.before(registrationStart) &&
        !today.after(registrationEnd)) {
        return "Registration Period"
    }
    else if (!today.before(bookingStart) &&
        !today.after(bookingEnd)) {
        return "Booking Period"
    } else {
        return "" // Returning nothing as this campsite is not applicable to have a status string
    }
}
@Composable
fun OwnerCampsiteListScreen(
    modifier: Modifier,
    onCampsiteClick: (Campsites) -> Unit,
    onFilterClick: () -> Unit,
    activeCampsites: List<Campsites> = emptyList(), // Retrieve from repository
    pastCampsites: List<Campsites> = emptyList(),
    cancelledCampsites: List<Campsites> = emptyList(),
    statusForCampsite: (Campsites) -> String,
    onWaitListClick: (Campsites) -> Unit, // TODO: Implement when section is ready
    onBookedSpotsClick: (Campsites) -> Unit, // TODO: Implement when section is ready
) {
    var searchQuery by remember { mutableStateOf("") }
    var submittedQuery by remember{ mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var tabs = listOf("Active Reservations", "Past Reservations", "Cancelled Reservations")

    var selectedCampsites = when (selectedTab) {
        0 -> activeCampsites
        1 -> pastCampsites
        else -> cancelledCampsites
    }

    var searchedCampsites = selectedCampsites.filter {
        it.name.contains(submittedQuery, ignoreCase = true)
    }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Search Bar
        TextField(
            modifier = modifier.fillMaxWidth().padding(top = 50.dp),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search") },
            leadingIcon = {
                IconButton(
                    onClick = {
                        submittedQuery = searchQuery
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = onFilterClick) {

                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Filter Icon"
                    )
                }
            },
            singleLine = true

        )

        Spacer(modifier = modifier.padding(16.dp))
        Text(
            text = "Your Listings",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = modifier.padding(16.dp))

        // Active, Past, Cancelled Reservations

        SecondaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        // Campsite List
        if (searchedCampsites.isEmpty()) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No campsites found")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = searchedCampsites,
                    key = { it.id }
                ) { campsite ->

                    val status = determineCampsiteStatus(campsite)
                    OwnerCampsiteCard(
                        modifier = modifier,
                        campsite = campsite,
                        status = status,
                        buttonText = when (status) {
                            "Registration Period" -> "View Waitlist"
                            else -> "View Booked Slots"
                        },
                        onActionClick = {
                            when (status) {
                                "Registration Period" -> onWaitListClick(campsite)
                                else -> onBookedSpotsClick(campsite)
                            }
                        },
                        onCampsiteClick = {
                            onCampsiteClick(campsite)
                        }
                    )

                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun OwnerCampsiteCard(
    modifier: Modifier,
    campsite: Campsites,
    status: String,
    buttonText: String,
    onActionClick: () -> Unit,
    onCampsiteClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TextButton(
            onClick = onCampsiteClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = campsite.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "Status: $status",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = modifier.height(8.dp))

        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Placeholder for campsite image
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Campsite image",
                modifier = modifier.size(42.dp)
            )

            Text(
                text = campsite.description,
                fontSize = 12.sp,
                maxLines = 3,
                lineHeight = 15.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier.weight(1f)
            )
        }

        Spacer(modifier = modifier.height(8.dp))

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onActionClick,
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 4.dp
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = 11.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OwnerCampsiteListScreenPreview() {
    OwnerCampsiteListScreen(
        modifier = Modifier,
        onCampsiteClick = {},
        onFilterClick = {},
        activeCampsites = emptyList(),
        pastCampsites = emptyList(),
        cancelledCampsites = emptyList(),
        statusForCampsite = { "" },
        onWaitListClick = {},
        onBookedSpotsClick = {},
    )
}