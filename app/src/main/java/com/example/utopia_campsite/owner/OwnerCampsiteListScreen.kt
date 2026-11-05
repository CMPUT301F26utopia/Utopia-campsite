package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

fun determineCampsiteStatus(campsites: Campsites): String {

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
    onBookedSpotsClick: (Campsites) -> Unit // TODO: Implement when section is ready
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
            modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
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
            edgePadding = 0.dp,
            divider = {}
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
                modifier = Modifier.fillMaxSize(),
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
                        OwnerCampsiteCard(
                            campsite = campsite,
                            status = status,
                            buttonText = {}
                            onActionClick = { {} },
                            onCampsiteClick = { {} }
                        )

                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun OwnerCampsiteCard(
    campsite: Campsites,
    status: String,
    buttonText: String,
    onActionClick: () -> Unit,
    onCampsiteClick: () -> Unit
) {}


@Preview(showBackground = true)
@Composable
fun OwnerCampsiteListScreenPreview() {
    OwnerCampsiteListScreen(modifier = Modifier, {}, {})
}