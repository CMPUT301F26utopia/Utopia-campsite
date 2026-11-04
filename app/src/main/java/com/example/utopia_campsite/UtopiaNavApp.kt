package com.example.utopia_campsite

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.admin.AdminCampsiteDetailScreen
import com.example.utopia_campsite.admin.AdminCampsiteListScreen
import com.example.utopia_campsite.auth.SignUpScreen
import com.example.utopia_campsite.auth.WelcomeScreen
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.CampsiteDetailsScreen
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.UserCampsiteListScreen
import com.example.utopia_campsite.user.UserFilterScreen
import com.example.utopia_campsite.user.UserFilterResultScreen
import com.example.utopia_campsite.user.UserProfileScreen
import com.example.utopia_campsite.user.UserRepository
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
    val userRepository = remember{ UserRepository() }
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
            var campsite by remember { mutableStateOf<Campsites?>(null) }
            var loadFailed by remember { mutableStateOf(false) }
            // loads fresh from the database each time this screen opens
            LaunchedEffect(campsiteId) {
                CampsiteRepository().getCampsites(
                    onSuccess = { campsites ->
                        val found = campsites.find { it.id == campsiteId }
                        if (found != null) campsite = found else loadFailed = true
                    },
                    onFailure = { loadFailed = true }
                )
            }//end launchedeffect
            when {
                campsite != null -> CampsiteDetailsScreen(campsite = campsite!!)
                loadFailed -> Text("Campsite not found")
                else -> CircularProgressIndicator()
            }
        }

        // Composable route for the user profile
        composable("user/profile") {
            var profileUser by remember { mutableStateOf<User?>(null) }
            var loadFailed by remember { mutableStateOf(false) }
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            LaunchedEffect(uid) {
                if (uid != null) {
                    userRepository.getUser(
                        userId = uid,
                        onSuccess = { profileUser = it },
                        onFailure = { loadFailed = true }
                    )
                } else {
                    loadFailed = true
                }
            }

            val loaded = profileUser
            when {
                loaded != null -> UserProfileScreen(
                    user = loaded,
                    saveUser = { updatedUser, onSuccess, onFailure -> userRepository.updateUser(updatedUser, onSuccess, onFailure) }
                )
                loadFailed -> Text("Couldn't load your profile")
                else -> CircularProgressIndicator()
            }
        }

        // Composable routes for user filter
        composable("user/filter") { UserFilterScreen(modifier = Modifier, filterViewModel = filterViewModel, navController = navController) }
        composable("user/filter/result") { UserFilterResultScreen(modifier = Modifier, filterViewModel = filterViewModel, navController = navController, onCampsiteClick = { navController.navigate("user/campsite/${it.id}") }) }

        composable("admin/campsites") {
            AdminCampsiteListScreen(
                onFilterClick = { navController.navigate("admin/filter") },
                navController = navController
            )
        }
        composable("admin/filter") {
            UserFilterScreen(
                modifier = Modifier,
                filterViewModel = filterViewModel,
                navController = navController,
                resultRoute = "admin/filter/result"
            )
        }

        composable("admin/filter/result") {
            UserFilterResultScreen(
                modifier = Modifier,
                filterViewModel = filterViewModel,
                navController = navController,
                onCampsiteClick = { navController.navigate("admin/campsite/${it.id}") }
            )
        }

        composable("admin/campsite/{campsiteId}") { backStackEntry ->
            val campsiteId = backStackEntry.arguments?.getString("campsiteId")
            var campsite by remember { mutableStateOf<Campsites?>(null) }
            var loadFailed by remember {mutableStateOf(false)}
            // loads fresh from the database each time this screen opens
            LaunchedEffect(campsiteId) {
                CampsiteRepository().getCampsites(
                    onSuccess = { campsites ->
                        val found = campsites.find { it.id == campsiteId }
                        if (found != null) campsite = found else loadFailed = true
                    },
                    onFailure = { loadFailed = true }
                )
            }//end launchedeffect
            when {
                campsite != null -> AdminCampsiteDetailScreen(campsite = campsite!!, navController = navController)
                loadFailed -> Text("Couldn't load campsite")
                else -> CircularProgressIndicator()
            }
        }
    }
}