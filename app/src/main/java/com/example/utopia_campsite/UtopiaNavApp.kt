package com.example.utopia_campsite

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.admin.AdminCampsiteDetailScreen
import com.example.utopia_campsite.admin.AdminCampsiteListScreen
import com.example.utopia_campsite.auth.SignUpScreen
import com.example.utopia_campsite.auth.WelcomeScreen
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.owner.Owner
import com.example.utopia_campsite.owner.OwnerCampsiteListScreen
import com.example.utopia_campsite.user.CampsiteDetailsScreen
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.UserCampsiteListScreen
import com.example.utopia_campsite.user.UserFilterScreen
import com.example.utopia_campsite.user.UserFilterResultScreen
import com.example.utopia_campsite.user.UserProfileScreen
import com.example.utopia_campsite.user.UserRepository
import com.example.utopia_campsite.user.util.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth

/**
 * Defines the navigation routes between the application's screens
 * Initializes a shared campsite filtering ViewModel for the filter and results screens
 * Uses NavController to handle navigation between registered destinations
 */
@Composable
fun UtopiaNavApp() {
    var showUserNavigation by remember { mutableStateOf(false) }
    var showOwnerNavigation by remember { mutableStateOf(false) }
    var showAdminNavigation by remember { mutableStateOf(true) }
    val navController = rememberNavController()

    // Initializing home screen routes and profile routes for navigation bar
    val homeScreenRoute = when {
        showUserNavigation -> "user/campsites"
        showOwnerNavigation -> "owner/campsites"
        showAdminNavigation -> "admin/campsites"
        else -> "user/campsites"
    }
    val profileScreenRoute = "user/profile" // For now, profile screen is set to already implemented user/profile route

    // Observing the current Screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomNavigationBar = currentRoute == homeScreenRoute || currentRoute == profileScreenRoute


    val filterViewModel: UserCampsiteFilterViewModel = viewModel() // Shared instance of filter-view-model for filter screens
    val userRepository = remember{ UserRepository() }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startPage = if (currentUser != null) homeScreenRoute else "welcome"

    Scaffold(
        bottomBar = {
            if (showBottomNavigationBar) {
                BottomNavigationBar(
                    navController = navController,
                    homeScreenRoute = homeScreenRoute,
                    profileScreenRoute = profileScreenRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = startPage, modifier = Modifier.padding(innerPadding)){
        composable("welcome") { WelcomeScreen(modifier = Modifier, onSignUp = { navController.navigate("sign_up") })}

        // Composable routers for Campsite Owner
        // TODO: Retrieve the owner object from Firestore when we have owner profiles
        // Constructing a temporary "owner" to allow for navigation
        composable("owner/campsites") {
            if (currentUser != null) {

                val tempOwner = Owner(
                    postedCampsitesId = mutableListOf(),
                    deviceID = currentUser.uid,
                    photoUrl = currentUser.photoUrl?.toString(),
                    email = currentUser.email ?: "",
                    firstName = currentUser.displayName ?: "",
                    lastName = "",
                    notificationsEnabled = false,
                    phone = currentUser.phoneNumber ?: "",
                    policyViolated = false
                )

                OwnerCampsiteListScreen(
                    modifier = Modifier,
                    owner = tempOwner,

                    onCampsiteClick = { campsite ->
                        navController.navigate("user/campsite/${campsite.id}")
                    },

                    onFilterClick = {
                        navController.navigate("user/filter")
                    },

                    // TODO: Connect to owner waitlist screen
                    onWaitListClick = {},

                    // TODO: Connect to owner booked spots screen
                    onBookedSpotsClick = {}
                )

            } else {
                Text("Error Occured")
            }
        }


//        //composable routers for campsite list and campsite details screen
//        composable("user/campsites"){ UserCampsiteListScreen(filterViewModel = filterViewModel, onFilterClick = { navController.navigate("user/filter") }, navController = navController) }
//        composable("user/campsite/{campsiteId}") { backStackEntry ->
//            val campsiteId = backStackEntry.arguments?.getString("campsiteId")
//            var campsite by remember { mutableStateOf<Campsites?>(null) }
//            var loadFailed by remember { mutableStateOf(false) }
//            // loads fresh from the database each time this screen opens
//            LaunchedEffect(campsiteId) {
//                CampsiteRepository().getCampsites(
//                    onSuccess = { campsites ->
//                        val found = campsites.find { it.id == campsiteId }
//                        if (found != null) campsite = found else loadFailed = true
//                    },
//                    onFailure = { loadFailed = true }
//                )
//            }

            composable("sign_up") {
                SignUpScreen(
                    modifier = Modifier,
                    navController = navController,
                    onCreateAccount = { firstName, lastName, email, phone ->
                        // TODO: Firebase account creation will go here
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
                        saveUser = { updatedUser, onSuccess, onFailure -> userRepository.updateUser(updatedUser, onSuccess, onFailure) },
                        deleteUser = { _, onFailure ->
                            userRepository.deleteUser(
                                userId = loaded.deviceID,
                                onSuccess = {
                                    FirebaseAuth.getInstance().currentUser?.delete()
                                    navController.navigate("welcome")
                                },
                                onFailure =  onFailure
                            )
                        }
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

}