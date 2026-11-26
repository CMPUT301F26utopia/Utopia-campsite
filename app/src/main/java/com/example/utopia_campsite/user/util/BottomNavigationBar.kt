package com.example.utopia_campsite.user.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavController,
    homeScreenRoute: String,
    profileScreenRoute: String,
    notificationsScreenRoute: String = "user/notifications",
    onSwitchRole: ((String) -> Unit)? = null) { // null hides the switch (non-admins)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route


    NavigationBar{
        // Navigation button for home screen
        NavigationBarItem(
            selected = currentRoute == homeScreenRoute,
            onClick = {
                navController.navigate(homeScreenRoute)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") }
        )

        // Navigation button for notifications screen
        NavigationBarItem(
            selected = currentRoute == notificationsScreenRoute,
            onClick = {
                navController.navigate(notificationsScreenRoute)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            },
            label = { Text("Notifications") }
        )

        // Navigation button for profile screen
        NavigationBarItem(
            selected = currentRoute == profileScreenRoute,
            onClick = {
                navController.navigate(profileScreenRoute)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") }
        )

        // Admin-only: cycles entrant -> owner -> admin (US 03.11.01)
        if (onSwitchRole != null) {
            val roles = listOf("user", "owner", "admin")
            val role = homeScreenRoute.substringBefore("/") // home routes are "<role>/campsites"
            NavigationBarItem(
                modifier = Modifier.testTag("switchRoleButton"),
                selected = false,
                onClick = { onSwitchRole(roles[(roles.indexOf(role) + 1) % roles.size]) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch role"
                    )
                },
                label = { Text(mapOf("user" to "Entrant", "owner" to "Owner", "admin" to "Admin").getValue(role)) }
            )
        }
    }
}