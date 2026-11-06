package com.example.utopia_campsite.owner

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 *
 * Determines whether a campsite is past its availability period
 * Compares the campsite's available end date against the current date
 *
 * @param campsite: the campsite whose availability date is being checked
 * @return true if the campsite's availability has ended, false otherwise
 */
fun isPastCampsite(campsite: Campsites): Boolean {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    dateFormat.isLenient = false

    return try {
        val today = dateFormat.parse(dateFormat.format(Date()))!!
        val endDate = dateFormat.parse(campsite.availableEndDate)!!

        today.after(endDate)
    } catch (e: Exception) {
        false
    }
}

/**
 *
 * Determines the current registration or booking status of a campsite
 * Compares the current date against the campsite's registration and booking date ranges
 *
 * @param campsite: the campsite whose status is being determined
 * @return the campsite's current status, an invalid date or an empty string if neither period is active
 */
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
/**
 * TODO: Cancelled Reservations need to be collectively decided on how it will be
 * Displays the owner's campsite listings with search and filtering functionality
 * Retrieves campsites belonging to the owner from the repository
 * Separates campsites into active, past, and cancelled reservation tabs
 * Allows the owner to view campsite details, waitlists, and booked spots
 * @param modifier: modifier used to customize the screen's layout
 * @param owner: the owner whose campsite listings are displayed
 * @param onCampsiteClick: callback function called when a campsite is selected
 * @param onFilterClick: callback function called when the filter button is clicked
 * @param onWaitListClick: callback function called when the view waitlist button is clicked
 * @param onBookedSpotsClick: callback function called when the view booked spots button is clicked
 */
@Composable
fun OwnerCampsiteListScreen(
    modifier: Modifier,
    owner: Owner,
    onCampsiteClick: (Campsites) -> Unit,
    onFilterClick: () -> Unit,
    onWaitListClick: (Campsites) -> Unit, // TODO: Implement when section is ready
    onBookedSpotsClick: (Campsites) -> Unit, // TODO: Implement when section is ready
) {
    var campsites by remember { mutableStateOf<List<Campsites>>(emptyList()) }
    val repository = remember { CampsiteRepository() }
    var searchQuery by remember { mutableStateOf("") }
    var submittedQuery by remember{ mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active Reservations", "Past Reservations", "Cancelled Reservations")

    LaunchedEffect(owner.deviceID, owner.postedCampsitesId.toList()) {
        repository.getOwnerCampsites(
            owner = owner,
            onSuccess = { campsites = it },
            onFailure = { exception ->
                Log.e("OwnerCampsiteListScreen", "Error Loading Campsites", exception)
            }
        )
    }

    val selectedCampsites = when (selectedTab) {
        0 -> campsites.filter { !isPastCampsite(it) }
        1 -> campsites.filter { isPastCampsite(it) }
        else -> emptyList()
    }

    val searchedCampsites = selectedCampsites.filter {
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
                            "Booking Period" -> "View Booked Slots"
                            else -> "View Details"
                        },
                        onActionClick = {
                            when (status) {
                                "Registration Period" -> onWaitListClick(campsite)
                                "Booking Period" -> onBookedSpotsClick(campsite)
                                else -> onCampsiteClick(campsite)
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

/**
 *
 * Displays an individual campsite's information within the owner's listing screen
 * Shows the campsite name, status, image placeholder, and description
 * Provides buttons to view campsite details and perform status-specific actions
 *
 * @param modifier: modifier used to customize the campsite card's layout
 * @param campsite: the campsite whose information is displayed
 * @param status: the current registration or booking status of the campsite
 * @param buttonText: the text displayed on the campsite's action button
 * @param onActionClick: callback function called when the action button is clicked
 * @param onCampsiteClick: callback function called when the campsite name is selected
 */
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

/**
 *
 * Provides an Android Studio preview of the owner's campsite listing screen
 * Uses a sample owner and empty callback functions for preview purposes
 */
@Preview(showBackground = true)
@Composable
fun OwnerCampsiteListScreenPreview() {
    OwnerCampsiteListScreen(
        modifier = Modifier,
        owner = Owner(
            postedCampsitesId = mutableListOf(),
            deviceID = "",
            photoUrl = null,
            email = "",
            firstName = "",
            lastName = "",
            notificationsEnabled = false,
            phone = "",
            policyViolated = false
        ),
        onCampsiteClick = {},
        onFilterClick = {},
        onWaitListClick = {},
        onBookedSpotsClick = {}
    )
}