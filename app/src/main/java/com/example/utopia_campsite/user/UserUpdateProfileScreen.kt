/**
 * This is where the user will enter details to update

 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.ui.theme.UtopiacampsiteTheme

/**
 * This screen will allow the user to update their profile details.
 * @param user: the user to update
 * @param onSave: a callback function to save the updated user
 * @param onCancel: a callback function to cancel the update
 */
@Composable
fun UserUpdateProfileScreen(
    user: User,
    onSave: (User) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    // Text fields start empty; the user's current values show as placeholders
    var firstName by remember(user) { mutableStateOf("") }
    var lastName by remember(user) { mutableStateOf("") }
    var email by remember(user) { mutableStateOf("") }
    var phone by remember(user) { mutableStateOf("") }
    var notificationsEnabled by remember(user) { mutableStateOf(user.notificationsEnabled) }
    var photoUrl by remember(user) { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First name") },
            placeholder = { Text(user.firstName) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("firstNameTextField")
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last name") },
            placeholder = { Text(user.lastName) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("lastNameTextField")
        )
        OutlinedTextField(
            value = photoUrl,
            onValueChange = { photoUrl = it },
            label = { Text("Photo URL") },
            placeholder = { Text(user.photoUrl ?: "") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("photoUrlTextField")
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text(user.email) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("emailTextField")
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone number") },
            placeholder = { Text(user.phone) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("phoneTextField")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notifications", modifier = Modifier.weight(1f))
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
                modifier = Modifier.testTag("notificationsSwitch")
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    onSave(
                        user.copy(
                            // blank field = keep the existing value
                            firstName = firstName.ifBlank { user.firstName },
                            lastName = lastName.ifBlank { user.lastName },
                            photoUrl = photoUrl.ifBlank { user.photoUrl },
                            email = email.ifBlank { user.email },
                            phone = phone.ifBlank { user.phone },
                            notificationsEnabled = notificationsEnabled
                        )
                    )
                },
                modifier = Modifier.testTag("applyChangesButton")
            ) { Text("Apply Changes") }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.testTag("cancelButton")
            ) { Text("Cancel") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserUpdateProfileScreenPreview() {
    val sampleUser = User(
        deviceID = "sample_device_id",
        photoUrl = "",
        email = "john.doe@example.com",
        firstName = "John",
        lastName = "Doe",
        notificationsEnabled = true,
        phone = "123-456-7890",
        currentReservations = mutableListOf(),
        pastReservations = emptyList()
    )
    UtopiacampsiteTheme {
        UserUpdateProfileScreen(user = sampleUser)
    }
}