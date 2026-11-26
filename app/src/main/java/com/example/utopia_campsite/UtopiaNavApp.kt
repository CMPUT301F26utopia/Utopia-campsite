package com.example.utopia_campsite

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.utopia_campsite.admin.AdminCampsiteReviewsScreen
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
import com.example.utopia_campsite.owner.CampsiteScreens
import com.example.utopia_campsite.owner.RegisterCampsiteScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Defines the navigation routes between the application's screens
 * Initializes a shared campsite filtering ViewModel for the filter and results screens
 * Uses NavController to handle navigation between registered destinations
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UtopiaNavApp() {
    var showUserNavigation by remember { mutableStateOf(false) }
    var showOwnerNavigation by remember { mutableStateOf(true) }
    var showAdminNavigation by remember { mutableStateOf(false) }

    // Tracks the authenticated user's role for admin switching
    var userRole by remember { mutableStateOf("user") }
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

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid -> userRepository.getUser(uid, onSuccess = { userRole = it.role }, onFailure = {}) }
    }

    Scaffold(
        bottomBar = {
            if (showBottomNavigationBar) {
                BottomNavigationBar(
                    navController = navController,
                    homeScreenRoute = homeScreenRoute,
                    profileScreenRoute = profileScreenRoute,
                    onSwitchRole = if (userRole == "admin") { newRole ->
                        showUserNavigation = newRole == "user"
                        showOwnerNavigation = newRole == "owner"
                        showAdminNavigation = newRole == "admin"
                        // fresh back stack so Back doesn't return to the previous role's screens
                        navController.navigate("$newRole/campsites") { popUpTo(navController.graph.id) { inclusive = true } }
                    } else null
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

            val uid = currentUser?.uid

            var owner by remember(uid) { mutableStateOf<Owner?>(null) }
            var loading by remember(uid) { mutableStateOf(true) }

            var error by remember(uid) { mutableStateOf<String?>(null) }

            LaunchedEffect(uid) {
                if (uid == null) {
                    error = "No user is signed in."
                    loading = false
                } else {
                    FirebaseFirestore.getInstance()
                        .collection("owners")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->

                            if (!document.exists()) {
                                error = "Owner profile not found."
                            } else {
                                owner = Owner(
                                    deviceID = document.id,

                                    postedCampsitesId =
                                        (document.get("postedCampsitesId") as? List<*>)
                                            ?.filterIsInstance<String>()
                                            ?.toMutableList()
                                            ?: mutableListOf(),

                                    firstName = document.getString("firstName") ?: "",
                                    lastName = document.getString("lastName") ?: "",
                                    email = document.getString("email") ?: "",
                                    phone = document.getString("phone") ?: "",
                                    photoUrl = document.getString("photoUrl"),
                                    notificationsEnabled = document.getBoolean("notificationsEnabled") ?: false,
                                    policyViolated = document.getBoolean("policyViolated") ?: false
                                )
                            }
                            loading = false
                        }
                        .addOnFailureListener { exception ->
                            error = exception.message ?: "Failed to load owner."
                            loading = false
                        }
                }
            }
            when {
                loading -> CircularProgressIndicator()
                error != null -> Text(error!!)
                owner != null -> OwnerCampsiteListScreen(
                    modifier = Modifier,
                    owner = owner!!,
                    onCampsiteClick = { campsite ->
                        navController.navigate("owner/campsite/${campsite.id}")
                    },
                    onFilterClick = {
                        navController.navigate("user/filter")
                    },
                    onWaitListClick = {},
                    onBookedSpotsClick = {},
                    onRegisterClick = {
                        navController.navigate("owner/register")
                    }
                )
            }
        }
            composable("owner/register") {
                RegisterCampsiteScreen(
                    onRegistered = {
                        navController.popBackStack()
                    }
                )
            }

            // display the selected campsite's owner details.
            composable("owner/campsite/{campsiteId}") { entry ->
                val campsiteId = entry.arguments?.getString("campsiteId")
                val repository = remember { CampsiteRepository() }

                var campsite by remember(campsiteId) {
                    mutableStateOf<Campsites?>(null)
                }

                var error by remember(campsiteId) {
                    mutableStateOf<String?>(null)
                }

                // Load the selected campsite when this route opens.
                LaunchedEffect(campsiteId) {
                    if (campsiteId.isNullOrBlank()) {
                        error = "Campsite not found."
                    } else {
                        repository.getCampsites(
                            onSuccess = { campsites ->
                                campsite = campsites.find { it.id == campsiteId }

                                if (campsite == null) {
                                    error = "Campsite not found."
                                }
                            },
                            onFailure = {
                                error = it.message ?: "Couldn't load campsite."
                            }
                        )
                    }
                }

                when {
                    campsite != null -> CampsiteScreens(
                        campsite = campsite!!,

                        onBack = {
                            navController.popBackStack()
                        },

                        // Connect the screen's edit form to Firestore.
                        onSave = { updated, finished ->
                            repository.updateCampsiteDetails(
                                campsite = updated,
                                onSuccess = {
                                    campsite = updated
                                    finished(null)
                                },
                                onFailure = {
                                    finished(it.message ?: "Couldn't save campsite.")
                                }
                            )
                        },
                        onSaveLottery = { updated, finished ->
                            repository.updateLotteryListing(
                                campsite = updated,
                                onSuccess = {
                                    campsite = updated
                                    finished(null)
                                },
                                onFailure = {
                                    finished(it.message ?: "Couldn't update lottery.")
                                }
                            )
                        }
                    )

                    error != null -> Text(error!!)

                    else -> CircularProgressIndicator()
                }
            }

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
                    campsite != null -> CampsiteDetailsScreen(
                        campsite = campsite!!,
                        onBack = { navController.popBackStack() },
                        onPostReview = { reviewText, onSuccess, onFailure ->
                            CampsiteRepository().addReview(
                                campsiteId = campsite!!.id,
                                reviewText = reviewText,
                                onSuccess = { review ->
                                    // show the new review right away without reloading from firebase
                                    campsite = campsite?.let { it.copy(reviews = it.reviews + review) }
                                    onSuccess()
                                },
                                onFailure = onFailure
                            )
                        },
                        onSubmitRating = { rating, onSuccess, onFailure ->
                            CampsiteRepository().addRating(
                                campsiteId = campsite!!.id,
                                rating = rating,
                                onSuccess = {
                                    // Update the displayed average after Firebase succeeds and put the user as a map, and the rating they gave as the value
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                                    if (uid != null) {
                                        campsite = campsite?.let {
                                            it.copy(ratings = it.ratings + (uid to rating))
                                        }
                                    }
                                    onSuccess()
                                },
                                onFailure = onFailure
                            )
                        }

                    )
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
            composable("admin/campsite/{campsiteId}/reviews") { backStackEntry ->
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
                    campsite != null -> AdminCampsiteReviewsScreen(campsite = campsite!!, navController = navController)
                    loadFailed -> Text("Couldn't load campsite")
                    else -> CircularProgressIndicator()
                }
            }
        }
    }

}