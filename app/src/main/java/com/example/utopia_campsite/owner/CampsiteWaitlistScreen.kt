package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserRepository

@Composable
fun CampsiteWaitlistScreen(
    campsite: Campsites,
    onBack: () -> Unit
) {
    var entrants by remember(campsite.id) { mutableStateOf<List<User>>(emptyList()) }
    var error by remember(campsite.id) { mutableStateOf<String?>(null) }

    LaunchedEffect(campsite.id, campsite.lotteryWaitlist.toList()) {
        entrants = emptyList()
        error = null

        campsite.lotteryWaitlist.forEach { userId ->
            UserRepository().getUser(
                userId = userId,
                onSuccess = { user ->
                    entrants = entrants + user
                },
                onFailure = {
                    error = it.message ?: "Failed to load entrant."
                }
            )
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextButton(onClick = onBack) {
            Text("Back")
        }

        Text("Lottery Waitlist: ${campsite.name}")
        Text("Total Entrants: ${campsite.lotteryWaitlist.size}")

        HorizontalDivider()

        when {
            error != null -> Text(error!!)

            campsite.lotteryWaitlist.isEmpty() ->
                Text("No entrants have joined the waitlist.")

            else -> LazyColumn {
                items(entrants, key = { it.deviceID }) { user ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("${user.firstName} ${user.lastName}")
                        Text(user.email)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}