package com.example.utopia_campsite

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.auth.SignUpScreen
import com.example.utopia_campsite.auth.WelcomeScreen
import com.example.utopia_campsite.user.CampsiteDetailsScreen
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.UserCampsiteListScreen
import com.example.utopia_campsite.user.UserFilterScreen
import com.example.utopia_campsite.user.UserFilterResultScreen
import com.google.firebase.auth.FirebaseAuth

/**
 * Defines the navigation routes between the application's screens
 * Initializes a shared campsite filtering ViewModel for the filter and results screens
 * Uses NavController to handle navigation between registered destinations
 */
@Composable
fun UtopiaNavApp() {
    val navController = rememberNavController()
    val filterViewModel: UserCampsiteFilterViewModel = viewModel() // Shared instance of filter-view-model for filter screens

    val currentUser = FirebaseAuth.getInstance().currentUser
    val startPage = if (currentUser != null) "user/campsites" else "welcome"

    NavHost(navController = navController, startDestination = startPage) { // TODO: when firebase authentication is working, logic needs to be placed here depending on new user or not. if new user -> welcome, otherwise -> home-screen
        // Composable routers for authentication

        composable("welcome") {
            WelcomeScreen(
                modifier = Modifier,
                onSignUp = {
                    navController.navigate("sign_up")
                }
            )
        }

        composable("sign_up") {
            SignUpScreen(
                modifier = Modifier,
                navController = navController,
                onCreateAccount = { firstName, lastName, email, phone ->
                    // Firebase account creation will go here
                }
            )
        }


        //composable routers for campsite list and campsite details screen
        composable("user/campsites"){ UserCampsiteListScreen(filterViewModel = filterViewModel, onFilterClick = { navController.navigate("user/filter") }, navController = navController) }
        composable("user/campsite/{campsiteId}") { backStackEntry ->
            val campsiteId = backStackEntry.arguments?.getString("campsiteId")
            val campsite = filterViewModel.allCampsitesList.find { it.id == campsiteId }

            if(campsite != null){
                CampsiteDetailsScreen(campsite = campsite)
            } else {
                Text("Campsite not found")
            }
            }//end composable

        // Composable routes for user filter
        composable("user/filter") { UserFilterScreen(modifier = Modifier, filterViewModel = filterViewModel, navController = navController) }
        composable("user/filter/result") { UserFilterResultScreen(modifier = Modifier, filterViewModel = filterViewModel, navController = navController) }
    }
}