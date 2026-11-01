package com.example.utopia_campsite.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

/*
* Sign-up screen
*
* Remaining things that need to be implemented:
* - When a user creates an account, the firebase authentication will create that document for the user and have the device ID linked.
* */

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onCreateAccount: (
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    ) -> Unit
) {
    var userFirstName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPhoneNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Button(
            modifier = modifier.align(Alignment.Start),
            onClick = { navController.popBackStack() },

        ) {
            Text("Back to Welcome")
        }

        Spacer(modifier = modifier.padding(16.dp))

        Text(
            text = "Create Your Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = modifier.padding(24.dp))
        Column(modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)) {

            // First name
            OutlinedTextField(
                value = userFirstName,
                onValueChange = { userFirstName = it},
                label = { Text("First Name")},
                singleLine = true
                )

            // Last name
            OutlinedTextField(
                value = userLastName,
                onValueChange = { userLastName = it},
                label = { Text("Last Name")},
                singleLine = true
            )

            // Email Address
            OutlinedTextField(
                value = userEmail,
                onValueChange = { userEmail = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true
            )

            // Phone number
            OutlinedTextField(
                value = userPhoneNumber,
                onValueChange = { userPhoneNumber = it },
                label = { Text("eg. 444-444-4444") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true
            )

        }

        Spacer(modifier = modifier.padding(32.dp))

        if (showError) {
            Text(
                text = "Please fill in all required fields.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {
                if (userFirstName.isBlank() || userLastName.isBlank() || userEmail.isBlank() || userPhoneNumber.isBlank()) {
                    showError = true
                } else {
                    showError = false
                    onCreateAccount(
                        userFirstName,
                        userLastName,
                        userEmail,
                        userPhoneNumber
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C8B3),
                contentColor = Color.White
            )
        ) {
            Text("Create Account")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    SignUpScreen(modifier = Modifier, navController = navController, onCreateAccount = { _, _, _, _ -> })
}