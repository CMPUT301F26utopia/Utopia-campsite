/**
 * Displays the user's campsite list as well as provides campsite search and filtering
 *
 * This is a thin wrapper around CampsiteListContent, which holds the shared list UI.
 * The only thing specific to users is where a campsite card goes when clicked.
 *
 * Users can search campsites by name, or click the filter icon and open the filter screen.
 *
 * Users can click on any individual campsite card and it will take them to that campsite's details page
 *
 * Outstanding issues:
 * see CampsiteListContent (sorting campsites by distance from the user's location)
 *
 */

package com.example.utopia_campsite.user

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.utopia_campsite.user.util.CampsiteListContent


/**
 *
 * Displays the user's campsite list, with search and filtering provided by CampsiteListContent
 * Clicking a campsite navigates to that campsite's user details page
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
    CampsiteListContent(
        filterViewModel = filterViewModel,
        onFilterClick = onFilterClick,
        onCampsiteClick = { campsite ->
            navController.navigate("user/campsite/${campsite.id}")
        }
    )

}//end UserCampsiteListScreen