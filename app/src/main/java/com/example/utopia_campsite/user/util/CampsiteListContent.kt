/**
 * Shared campsite list UI used by both the user and admin campsite list screens
 *
 * Users can search campsites by name, or click the filter icon and open the filter screen.
 *
 * Clicking a campsite card calls onCampsiteClick, so each screen decides where it goes.
 *
 * Outstanding issues:
 * currently does not automatically sort campsites by distance from the user's location
 * TBD if this will end up in the final product; it's not part of the spec, but could be added for WOW
 *
 */

package com.example.utopia_campsite.user.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.testTag
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.user.CampsiteSummaryRow
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.owner.qrScanner


/**
 *
 * Displays all campsites, or only campsites containing the submitted search query if one is provided
 * Shows loading and error messages when campsites cannot be displayed
 * Shows an empty message when no campsites are found
 *
 * @param onFilterClick: a callback function that is called when the filter button is clicked
 * @param onCampsiteClick: a callback function that is called with the campsite that was clicked
 */
@Composable
fun CampsiteListContent(
    onFilterClick: () -> Unit,
    onCampsiteClick: (Campsites) -> Unit
){
    var searchQuery by remember { mutableStateOf("") }
    var submittedQuery by remember{mutableStateOf("")}
    var allCampsites by remember { mutableStateOf<List<Campsites>>(emptyList()) }
    val campsiteRepository = remember { CampsiteRepository() }
    var loadStatus by remember { mutableStateOf("loading") }
    var showQrScanner by remember { mutableStateOf(false) }
    var showQrFailedAlert by remember { mutableStateOf(false) }

    // runs each time this screen appears, including when coming back from a detail screen
    LaunchedEffect(Unit) {
        campsiteRepository.getCampsites(
            onSuccess = { campsites ->
                allCampsites = campsites
                loadStatus = "success"
            },
            onFailure = {
                loadStatus = "error"
            }
        )
    }//end LaunchedEffect

    val campsites = allCampsites.filter { campsite ->
        campsite.name.contains(submittedQuery.trim(), ignoreCase = true)
    }



    if (showQrScanner) {
        Dialog(
            onDismissRequest = { showQrScanner = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                qrScanner(
                    onScanResult = { scannedText ->
                        showQrScanner = false
                        val matched = allCampsites.find { it.id == scannedText }
                        if (matched != null) {
                            onCampsiteClick(matched)
                        } else {
                            showQrFailedAlert = true
                        }
                    }
                )
                IconButton(
                    onClick = { showQrScanner = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .testTag("closeQrScanner")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Scanner",
                        tint = Color.White
                    )
                }
            }
        }
    }

    if (showQrFailedAlert) {
        AlertDialog(
            onDismissRequest = { showQrFailedAlert = false },
            title = { Text(text = "Error") },
            text = { Text("Campsite not found.") },
            confirmButton = {
                TextButton(onClick = { showQrFailedAlert = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)){

        //search bar and filter button
        TextField(
            modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search campsites by name") },
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
                }//end iconButton
            },//end leading icon
            trailingIcon = {
                Row {
                    IconButton(onClick = { showQrScanner = true }) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR Code"
                        )
                    }
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "Filter Icon"
                        )
                    }
                }
            },//end trailing icon
            singleLine = true

        )//end textField
        Text(
            text = "Campsites (${campsites.size})",
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if(loadStatus== "loading"){
            Text("Loading campsites...")
        } else if(loadStatus == "error"){
            Text(
                text = "Error loading campsites",
                color = Color.Red
            )
        } else {
            if (campsites.isEmpty()) {
                Text("No campsites found")
            } else{

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = campsites,
                        key = { campsite -> campsite.id }
                    ){
                            campsite ->
                        Card(modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable{
                                onCampsiteClick(campsite)
                            }

                        ){
                            CampsiteSummaryRow(campsite = campsite)
                        }
                    }


                }//end lazyColumn

            }//end inner else statement
        }//end outer else statement

    }//end column

}//end CampsiteListContent