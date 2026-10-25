package com.example.utopia_campsite

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.utopia_campsite.user.UserFilterScreen

// Method designed for the navigations between screens

@Composable
fun UtopiaNavApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // Composable routes for user filter
        composable("user/filter") { UserFilterScreen.FilterScreen(navController) }
    }
}