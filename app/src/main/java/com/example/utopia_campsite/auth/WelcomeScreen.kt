package com.example.utopia_campsite.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays the welcome screen when users first open the Utopia Campsite application
 * Provides an introduction to the application with a welcome message
 * Allows new users to navigate to the Sign Up Screen to create an account
 *
 * @param modifier: the Modifier used to configure the screen layout
 * @param onSignUp: a callback function that navigates the user to the Sign Up Screen
 * when the Create Account button is pressed
 */
@Composable
fun WelcomeScreen(
    modifier: Modifier,
    onSignUp: () -> Unit,
){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Text(
            text = "Welcome to\nUtopia\nCampsite",
            fontSize = 40.sp,
            lineHeight = 60.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = modifier.padding(30.dp))


        Button(
            onClick = onSignUp,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C8B3),
                contentColor = Color.White
            )
        ) {
            Text("CREATE ACCOUNT")
        }

    }
}

/**
 * Provides a preview of the welcome screen in Android Studio
 * Passes an empty onSignUp callback to display the UI without performing navigation
 */
@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(modifier = Modifier, {})
}