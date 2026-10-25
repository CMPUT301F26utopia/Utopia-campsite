package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserFilterResultScreen(modifier: Modifier, filterViewModel: UserCampsiteFilterViewModel) {
    // Method for showcasing the result of the filtered list
    val filteredCampsites = filterViewModel.filteredCampsites

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Button(
            onClick = {  } // use navController to go back the previous page
        ) {
            Text("Back to Filters")
        }

        Text("Matching Campsites: ${filteredCampsites.size}")

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
                        Text(campsite.location)
                        Text("Maximum Guests: ${campsite.maximumOccupants}")
                        Text("Available: ${campsite.availableStartDate} - ${campsite.availableEndDate}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterResultScreenPreview() {
    UserFilterResultScreen(modifier = Modifier, filterViewModel = UserCampsiteFilterViewModel())
}