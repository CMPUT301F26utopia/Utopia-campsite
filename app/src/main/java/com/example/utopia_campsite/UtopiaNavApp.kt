package com.example.utopia_campsite

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.user.CampsiteDetailsScreen
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.UserCampsiteListScreen
import com.example.utopia_campsite.user.UserFilterScreen
import com.example.utopia_campsite.user.UserFilterResultScreen

/**
 * Defines the navigation routes between the application's screens
 * Initializes a shared campsite filtering ViewModel for the filter and results screens
 * Uses NavController to handle navigation between registered destinations
 */
@Composable
fun UtopiaNavApp() {
    val navController = rememberNavController()
    val filterViewModel: UserCampsiteFilterViewModel = viewModel() // Shared instance of filter-view-model for filter screens

    NavHost(navController = navController, startDestination = "user/campsites") {
        //composable routers for campsite list and campsite details screen
        composable("user/campsites"){ UserCampsiteListScreen(filterViewModel = filterViewModel, onFilterClick = { navController.navigate("user/filter") }, navController = navController) }
        composable("user/campsite/{campsiteId}") { backStackEntry ->
            val campsiteId = backStackEntry.arguments?.getString("campsiteId")
            val campsite = filterViewModel.allCampsitesList.find { it.id == campsiteId }

            if(campsite != null){
                CampsiteDetailsScreen(
                    campsite = campsite,
                    user = User(
                        deviceID = "device_001",
                        email = "user@utopia.com",
                        firstName = "Default",
                        lastName = "User",
                        notificationsEnabled = true,
                        phone = "555-555-5555",
                        currentReservations = emptyList(),
                        pastReservations = emptyList()
                    )
                )
            } else {
                Text("Campsite not found")
            }
            }//end composable

        // Composable routes for user filter
        composable("user/filter") { UserFilterScreen(modifier = Modifier, filterViewModel = filterViewModel, navController = navController,) }
        composable("user/filter/result") { UserFilterResultScreen(modifier = Modifier, filterViewModel = filterViewModel, navController = navController) }
    }
}