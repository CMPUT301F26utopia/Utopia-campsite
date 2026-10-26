/**
 * Displays the user's campsite list as well as provides campsite search and filtering
 *
 * Users can search campsites by name, or click the filter icon and open the filter screen.
 *
 * Users can click on any individual campsite card and it will take them to that campsite's details page
 *
 * Outstanding issues:
 * currently does not automatically sort campsites by distance from the user's location
 * TBD if this will end up in the final product; it's not part of the spec, but could be added for WOW
 *
 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import com.example.utopia_campsite.user.CampsiteSummaryRow


/**
 *
 * Displays all campsites, or only campsites containing the submitted search query if one is provided
 * Shows loadinga nd error messages when campsites cannot be displayed
 * Shows an empty message when no campsites are found
 *
 * @param filterViewModel: the ViewModel containing the list of campsites to display
 * @param onFilterClick: a callback function that is called when the filter button is clicked
 * @param navController: the navigation controller used to navigate between screens
 */
@Composable
fun UserCampsiteListScreen(
    filterViewModel: UserCampsiteFilterViewModel,
    onFilterClick: () -> Unit,
    navController: NavController
){
    var searchQuery by remember { mutableStateOf("") }
    var submittedQuery by remember{mutableStateOf("")}


    val campsites = filterViewModel.allCampsitesList.filter { campsite ->
        campsite.name.contains(submittedQuery.trim(), ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)){

        //search bar and filter button
        TextField(
            modifier = Modifier.fillMaxWidth(),
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
                IconButton(onClick = onFilterClick) {

                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Filter Icon"
                    )
                }//end iconButton
            },//end trailing icon
            singleLine = true

        )//end textField
        Text(
            text = "Campsites (${campsites.size})",
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if(filterViewModel.campsiteLoadStatus == "loading"){
            Text("Loading campsites...")
        } else if(filterViewModel.campsiteLoadStatus == "error"){
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
                                navController.navigate("user/campsite/${campsite.id}")
                            }

                        ){
                            CampsiteSummaryRow(campsite = campsite)
                        }
                    }


                }//end lazyColumn

            }//end inner else statement
        }//end outer else statement

    }//end column



}//end UserCampsiteListScreen