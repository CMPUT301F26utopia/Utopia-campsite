package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.owner.Campsites

/**
 * Displays the list of campsites matching the user's search criteria
 * Retrieves the filtered campsite list from the shared ViewModel
 * Displays each campsite's location, maximum occupancy, and available dates
 * Allows the user to navigate back to the filtering screen
 *
 * @param modifier: the Modifier used to configure the screen layout
 * @param filterViewModel: the ViewModel containing the filtered campsite results
 * @param navController: the navigation controller used to return to the previous screen
 */
@Composable
fun UserFilterResultScreen(modifier: Modifier, filterViewModel: UserCampsiteFilterViewModel, navController: NavController) {
    // Method for showcasing the result of the filtered list
    val filteredCampsites = filterViewModel.filteredCampsitesList

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = modifier.padding(16.dp))
        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Back to Filters")
            }
        }
        Spacer(modifier = modifier.padding(16.dp))
        Text("Matching Campsites: ${filteredCampsites.size}", fontSize = 24.sp)

        LazyColumn (
            modifier = modifier.weight(1f)
        ){
            items(filteredCampsites) { campsite ->
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = modifier.padding(16.dp)
                    ) {
                        Text(campsite.name)
                        Text("Address: ${campsite.address}")
                        Text("Maximum Guests: ${campsite.maximumOccupants}")
                        Text("Available: ${campsite.availableStartDate} - ${campsite.availableEndDate}")
                    }
                }
            }
        }
    }
}

/**
 * Provides a preview of the filtered campsite results screen
 * Initializes a ViewModel with sample campsite data to display the results layout
 */
@Preview(showBackground = true)
@Composable
fun FilterResultScreenPreview() {
    val navController = rememberNavController()
    val filterViewModel = remember { UserCampsiteFilterViewModel() }

    val campsites = listOf(
        Campsites(
            id = "camp_001",
            name = "Banff Mountain Campground",
            photoUrl = null,
            address = "Banff, Alberta, Canada",
            latitude = 51.1784,
            longitude = -115.5708,
            ratings = listOf(5, 4, 5, 4),
            tags = listOf("Hiking"),
            lotteryWinners = 10,
            lotteryWaitlist = 5,
            description = "Mountain campsite",
            registrationDate = "01/10/2026-10/10/2026",
            bookingDate = "15/10/2026-20/10/2026",
            availableStartDate = "15/10/2026",
            availableEndDate = "20/10/2026",
            maximumOccupants = 4
        ),
        Campsites(
            id = "camp_002",
            name = "Jasper Lakeside Campground",
            photoUrl = null,
            address = "Jasper, Alberta, Canada",
            latitude = 52.8737,
            longitude = -118.0814,
            ratings = listOf(4, 5, 4, 5),
            tags = listOf("Fishing"),
            lotteryWinners = 15,
            lotteryWaitlist = 3,
            description = "Lakeside campsite",
            registrationDate = "01/11/2026-10/11/2026",
            bookingDate = "15/11/2026-20/11/2026",
            availableStartDate = "15/11/2026",
            availableEndDate = "20/11/2026",
            maximumOccupants = 6
        ),
        Campsites(
            id = "camp_003",
            name = "Elk Island Forest Campground",
            photoUrl = null,
            address = "Elk Island National Park, Alberta, Canada",
            latitude = 53.6067,
            longitude = -112.8638,
            ratings = listOf(5, 5, 4, 3),
            tags = listOf("Wildlife"),
            lotteryWinners = 8,
            lotteryWaitlist = 2,
            description = "Forest campsite",
            registrationDate = "01/12/2026-10/12/2026",
            bookingDate = "15/12/2026-20/12/2026",
            availableStartDate = "15/12/2026",
            availableEndDate = "20/12/2026",
            maximumOccupants = 8
        )
    )

    filterViewModel.updateFilteredCampsites(campsites)

    UserFilterResultScreen(
        modifier = Modifier,
        filterViewModel = filterViewModel,
        navController = navController
    )
}