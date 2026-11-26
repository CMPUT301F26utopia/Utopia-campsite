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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Displays the sign-up screen for users creating a new Utopia Campsite account
 * Allows users to enter their first name, last name, email address, and phone number
 * Validates that all required fields have been completed before attempting account creation
 * Displays an error message when one or more required fields are empty
 * Calls the onCreateAccount callback to pass the user's information for account registration
 * Provides a back button to return to the Welcome Screen
 *
 * @param db: the database we are storing the data
 * @param uid: the unique identifier of the user which is their device id
 * @param firstName: first name of the user
 * @param lastName: last name of the user
 * @param email: email address of the user
 * @param phone: phone number of the user in format : 111-111-1111
 * @param role: role of the account being created
 * @param onSuccess: callback function for a success creation
 * @param onError: callback function if creation failed
 */

fun createUser(
    db: FirebaseFirestore,
    uid: String,
    firstName: String,
    lastName: String,
    email: String,
    phone: String,
    role: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val user = hashMapOf("firstName" to firstName, "lastName" to lastName, "email" to email, "phone" to phone, "role" to role)

    if (role == "user") {
        db.collection("users").document(uid).set(user, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Could not create user") }
    }

    else {
        db.collection("owners").document(uid).set(user, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Could not create owner") }
    }

}

fun onCreateAccount(
    firstName: String,
    lastName: String,
    email: String,
    phone: String,
    role: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val currentUser = auth.currentUser

    if (currentUser != null) {
        if (currentUser.isAnonymous) {
            createUser(db, currentUser.uid, firstName, lastName, email, phone, role, onSuccess, onError)
        } else {
            onError("Already signed in")
        }
    } else {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid

                    if (uid != null) {
                        createUser(db, uid, firstName, lastName, email, phone, role, onSuccess, onError)
                    } else {
                        onError("Could not retrieve user ID.")
                    }
                } else {
                    onError(
                        task.exception?.message
                            ?: "Authentication failed."
                    )
                }
            }
    }
}


/**
* Displays the sign-up screen for users creating a new Utopia Campsite account
* Allows users to enter their first name, last name, email address, and phone number
* Validates that all required fields have been completed before attempting account creation
* Displays an error message when one or more required fields are empty
* Calls the onCreateAccount callback to pass the user's information for account registration
* Provides a back button to return to the Welcome Screen
 *
 * @param modifier: modifier for ui customization
 * @param navController: object for screen navigation
 * @param onCreateAccount: function for adding the user data fields in database
 *
*/
@OptIn(ExperimentalMaterial3Api::class)
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
    var loadingState by remember { mutableStateOf(false) }
    var firebaseError by remember { mutableStateOf("") }


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
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true,
                supportingText = { Text("Eg. 444-444-4444")}
            )

        }

        val roleOptions = listOf("User", "Owner")

        //track if the drop-down menu is open or closed
        var expanded by remember { mutableStateOf(false) }

        //track the currently selected role
        var selectedRole by remember { mutableStateOf("") }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 4. The TextField that acts as the clickable trigger
            OutlinedTextField(
                value = selectedRole,
                onValueChange = {}, // Empty because this is a read-only selection field
                readOnly = true,    // Prevents the keyboard from popping up
                label = { Text("Role") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),

                //This modifier anchors the menu to the text field
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            // 5. The dropdown menu that appears below
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                roleOptions.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            expanded = false // Close the menu after selection
                        }
                    )
                }
            }
        }

        Spacer(modifier = modifier.padding(32.dp))

        if (showError) {
            Text(
                text = "Please fill in all required fields.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (firebaseError.isNotEmpty()) {
            Text(
                text = firebaseError,
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
                    firebaseError = ""
                    loadingState = true
                    onCreateAccount(
                        userFirstName,
                        userLastName,
                        userEmail,
                        userPhoneNumber
                    )

                    onCreateAccount(
                        userFirstName,
                        userLastName,
                        userEmail,
                        userPhoneNumber,
                        role = selectedRole,
                        onSuccess = {
                            loadingState = false
                            navController.navigate("user/campsites")
                        },
                        onError = { errorMsg ->
                            loadingState = false
                            firebaseError = errorMsg
                        }
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

/**
 * Provides a preview of the sign-up screen
 */
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    SignUpScreen(modifier = Modifier, navController = navController, onCreateAccount = { _, _, _, _ -> })
}
