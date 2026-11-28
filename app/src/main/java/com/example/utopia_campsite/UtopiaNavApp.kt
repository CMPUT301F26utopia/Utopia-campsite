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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.utopia_campsite.admin.AdminCampsiteDetailScreen
import com.example.utopia_campsite.admin.AdminCampsiteListScreen
import com.example.utopia_campsite.admin.AdminCampsiteReviewsScreen
import com.example.utopia_campsite.auth.SignUpScreen
import com.example.utopia_campsite.auth.WelcomeScreen
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.CampsiteReservedUsersScreen
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.owner.Owner
import com.example.utopia_campsite.owner.OwnerCampsiteListScreen
import com.example.utopia_campsite.owner.OwnerRepository
import com.example.utopia_campsite.user.CampsiteDetailsScreen
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserCampsiteFilterViewModel
import com.example.utopia_campsite.user.UserCampsiteListScreen
import com.example.utopia_campsite.user.UserFilterScreen
import com.example.utopia_campsite.user.UserFilterResultScreen
import com.example.utopia_campsite.user.UserProfileScreen
import com.example.utopia_campsite.user.UserNotificationsScreen
import com.example.utopia_campsite.user.UserRepository

import com.example.utopia_campsite.messaging.Conversation
import com.example.utopia_campsite.messaging.ChatRoute
import com.example.utopia_campsite.user.util.BottomNavigationBar
import com.example.utopia_campsite.owner.CampsiteScreens
import com.example.utopia_campsite.owner.CampsiteWaitlistScreen
import com.example.utopia_campsite.owner.RegisterCampsiteScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.pipeline.Expression.Companion.type
import com.example.utopia_campsite.user.LotteryWinnerScreen
import com.example.utopia_campsite.owner.MultipleLotteryScreen
import com.example.utopia_campsite.user.MultipleLotteryWinnerScreen

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
    val notificationsScreenRoute = "user/notifications"
    val showBottomNavigationBar = currentRoute == homeScreenRoute || currentRoute == profileScreenRoute || currentRoute == notificationsScreenRoute


    val filterViewModel: UserCampsiteFilterViewModel = viewModel() // Shared instance of filter-view-model for filter screens
    val userRepository = remember { UserRepository() }
    val ownerRepository = remember { OwnerRepository() }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startPage = if (currentUser != null) homeScreenRoute else "welcome"

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            userRepository.getUser(uid, onSuccess = { userRole = it.role }, onFailure = {
                // owners live in the "owners" collection, not "users"
                ownerRepository.getOwner(uid, onSuccess = {
                    userRole = "owner"
                    showOwnerNavigation = true
                    navController.navigate("owner/campsites") { popUpTo(navController.graph.id) { inclusive = true } }
                }, onFailure = {})
            })
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomNavigationBar) {
                BottomNavigationBar(
                    navController = navController,
                    homeScreenRoute = homeScreenRoute,
                    profileScreenRoute = profileScreenRoute,
                    notificationsScreenRoute = notificationsScreenRoute,
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

        //composable route for booked slots and past users booked in a non-active campsites
        composable("owner/usersBooked/{campsiteId}") { backStackEntry ->
            val campsiteId = backStackEntry.arguments?.getString("campsiteId")
            var campsite by remember { mutableStateOf<Campsites?>(null) }
            var loadFailed by remember { mutableStateOf(false) }
            // loads fresh from the database each time this screen opens
            LaunchedEffect(campsiteId) {
                CampsiteRepository().getCampsite(
                    campsiteId = campsiteId!!,
                    onSuccess = {campsite = it },
                    onFailure = { loadFailed = true }
                )
            }//end launched effect
            when {
                campsite != null -> CampsiteReservedUsersScreen(
                    campsite = campsite!!,
                    onBack = {navController.popBackStack()},
                    onMessageClick = { userId -> navController.navigate("chat/${campsite!!.id}/$userId") }

                )
                loadFailed -> Text("Campsite not found")
                else -> CircularProgressIndicator()
            }

        } //end composable route for booked slots

        composable("owner/waitlist/{campsiteId}") { entry ->
            val campsiteId = entry.arguments?.getString("campsiteId")

            var campsite by remember(campsiteId) {
                mutableStateOf<Campsites?>(null)
            }

            LaunchedEffect(campsiteId) {
                if (campsiteId != null) {
                    CampsiteRepository().getCampsites(
                        onSuccess = { campsites ->
                            campsite = campsites.find { it.id == campsiteId }
                        },
                        onFailure = {}
                    )
                }
            }

            campsite?.let {
                CampsiteWaitlistScreen(
                    campsite = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Composable routers for Campsite Owner
        composable("owner/campsites") {

            var profileOwner by remember { mutableStateOf<Owner?>(null) }
            var loadFailed by remember { mutableStateOf(false) }
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            LaunchedEffect(uid) {
                if (uid != null) {
                    ownerRepository.getOwner(
                        ownerId = uid,
                        onSuccess = { profileOwner = it },
                        onFailure = { loadFailed = true }
                    )
                } else {
                    loadFailed = true
                }
            }

            val loaded = profileOwner

            when {
                loaded != null -> OwnerCampsiteListScreen(
                    modifier = Modifier,
                    owner = loaded,

                    onMultipleLotteryScreen = {
                        navController.navigate("owner/combined-lottery/create")
                    },

                    onCampsiteClick = { campsite ->
                        navController.navigate("owner/campsite/${campsite.id}")
                    },
                    onFilterClick = {
                        navController.navigate("user/filter")
                    },
                    onWaitListClick = { campsite ->
                        navController.navigate("owner/waitlist/${campsite.id}")
                    },

                    onBookedSpotsClick = { campsite ->
                        navController.navigate("owner/usersBooked/${campsite.id}")
                    },

                    onViewUsersBookedClick = { campsite ->
                        navController.navigate("owner/usersBooked/${campsite.id}")
                    },

                    onRegisterClick = {
                        navController.navigate("owner/register")
                    }
                )
                loadFailed -> Text("Couldn't load campsites")

                else -> CircularProgressIndicator()
            }
        }
            composable("owner/register") {
                RegisterCampsiteScreen(
                    onRegistered = {
                        navController.popBackStack()
                    }
                )
            }

            composable("owner/combined-lottery/create") {
                MultipleLotteryScreen (
                    onBack = {
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
                }//end launched effect
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
                        },
                        onMessageClick = { campsiteId -> navController.navigate("chat/$campsiteId/${loaded.deviceID}") }
                    )
                    loadFailed -> Text("Couldn't load your profile")
                    else -> CircularProgressIndicator()
                }
            }

            // Composable route for the user's notifications
            composable("user/notifications") {
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (uid != null) {
                    UserNotificationsScreen(
                        userId = uid,
                        onWinnerClick = { campsiteId ->
                            navController.navigate("user/lottery-winner/${android.net.Uri.encode(campsiteId)}"
                            )
                        }
                    )
                } else { Text("Couldn't load your notifications") }
            }

            composable("user/lottery-winner/{campsiteId}") { backStackEntry ->
                val campsiteId = backStackEntry.arguments?.getString("campsiteId")
                var campsite by remember(campsiteId) { mutableStateOf<Campsites?>(null) }
                var loadError by remember(campsiteId) { mutableStateOf<String?>(null) }
                var isLotteryWinner by remember(campsiteId) { mutableStateOf<Boolean?>(null) }
                var isCombinedLottery by remember(campsiteId) { mutableStateOf<Boolean?>(null) }

                LaunchedEffect(campsiteId) {
                    if (campsiteId.isNullOrBlank()) {
                        loadError = "Campsite not found"
                    } else {
                        CampsiteRepository().getCampsites(
                            onSuccess = { campsites ->
                                val found = campsites.find { it.id == campsiteId }

                                if (found != null) {
                                    campsite = found
                                } else {
                                    loadError = "Campsite not found"
                                }
                            },
                            onFailure = {
                                loadError = "Couldn't load the campsite"
                            }
                        )
                    }
                }

                when {
                    loadError != null -> {
                        Text(loadError!!)
                    }

                    campsite == null || isLotteryWinner == null || isCombinedLottery == null -> {
                        CircularProgressIndicator()
                    }

                    isLotteryWinner == true && isCombinedLottery == true -> {
                        MultipleLotteryWinnerScreen(
                            lotteryId = campsiteId!!,

                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    isLotteryWinner == true -> {
                        LotteryWinnerScreen(
                            campsite = campsite!!,
                            opportunityId = "test-${campsite!!.id}",
                            isWinner = true,
                            availableDates = if (campsite!!.bookingDate.isNotBlank()) {
                                listOf(campsite!!.bookingDate)
                            } else {
                                emptyList()
                            },
                            onBack = { navController.popBackStack() },
                            onDecline = {
                                CampsiteRepository().declineLotteryOffer(campsite!!.id)
                            }
                        )
                    }

                    else -> {
                        Text("You are not a winner for this campsite")
                    }
                }


                LaunchedEffect(campsiteId) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid

                    if (campsiteId.isNullOrBlank() || uid == null) {
                        loadError = "Couldn't check lottery result"
                    } else {
                        FirebaseFirestore.getInstance()
                            .collection("campsites")
                            .document(campsiteId)
                            .get()
                            .addOnSuccessListener { document ->
                                isCombinedLottery = document.getBoolean("isCombinedLottery") == true
                                val winners = document.get("lotteryWinnerList") as? List<*>

                                isLotteryWinner = winners
                                    ?.filterIsInstance<com.google.firebase.firestore.DocumentReference>()
                                    ?.any { it.path == "users/$uid" }
                                    ?: false
                            }
                            .addOnFailureListener {
                                loadError = "Couldn't check lottery result"
                            }
                    }
                }
            }

            // Composable route for a conversation between a campsite's owner and an entrant who reserved it
            // chat/{campsiteId}/{userId}: userId is the entrant, both people open the same route
            composable("chat/{campsiteId}/{userId}") { backStackEntry ->
                val campsiteId = backStackEntry.arguments?.getString("campsiteId")
                val userId = backStackEntry.arguments?.getString("userId")
                val myId = FirebaseAuth.getInstance().currentUser?.uid
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
                }//end launched effect
                val loaded = campsite
                when {
                    myId == null || userId == null -> Text("Couldn't open the conversation")
                    loaded != null -> {
                        val conversation = Conversation.forCampsite(loaded, userId)
                        // only the owner and the entrant can open it
                        if (myId != conversation.ownerId && myId != userId) {
                            Text("You can't view this conversation")
                        } else {
                            ChatRoute(
                                conversation = conversation,
                                myId = myId,
                                onBack = { navController.popBackStack() },
                                // read only once the entrant is no longer booked at the campsite
                            )
                        }
                    }
                    loadFailed -> Text("Campsite not found")
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
                }//end launched effect
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