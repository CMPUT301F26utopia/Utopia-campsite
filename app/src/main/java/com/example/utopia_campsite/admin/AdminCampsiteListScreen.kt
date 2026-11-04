/**
 * Displays the admin's campsite list as well as provides campsite search and filtering
 *
 * This is a wrapper around CampsiteListContent, which holds the shared list UI.
 * The things specific to admins are where a campsite card goes when clicked,
 * and the button for adding a new campsite.
 *
 * Admins can search campsites by name, or click the filter icon and open the filter screen.
 *
 * Admins can click on any individual campsite card and it will take them to that campsite's admin details page
 *
 * Admins can click the add button to go to the add campsite screen
 *
 * Outstanding issues:
 * Similar to UserCampsiteListScreen, currently the campsites are not sorted
 * Add button does not currently do anything
 * admin/campsite currently does not exist
 *
 */

package com.example.utopia_campsite.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.util.CampsiteListContent


/**
 *
 * Displays the admin's campsite list, with search and filtering provided by CampsiteListContent
 * Clicking a campsite navigates to that campsite's admin details page
 * Clicking the add button navigates to the add campsite screen
 *
 * @param onFilterClick: a callback function that is called when the filter button is clicked
 * @param navController: the navigation controller used to navigate between screens
 */
@Composable
fun AdminCampsiteListScreen(
    onFilterClick: () -> Unit,
    navController: NavController
){
    Box(modifier = Modifier.fillMaxSize()){

        CampsiteListContent(
            onFilterClick = onFilterClick,
            onCampsiteClick = { campsite ->
                navController.navigate("admin/campsite/${campsite.id}")
            }
        )

        //add campsite button
        FloatingActionButton(
            onClick = { /*TODO add campsite*/ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Campsite"
            )
        }//end floatingActionButton

    }//end box

}//end AdminCampsiteListScreen

