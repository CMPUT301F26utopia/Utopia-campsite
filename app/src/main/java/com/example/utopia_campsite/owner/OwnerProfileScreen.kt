/**
 * Displays the Owner Profile as the starting view and
 *  Can toggle between Owner profile,current and past reservations
 *  Outstanding Issues:
 *  highlight button when selected not implemented
 *  Owner profile not implemented
 *  Owner Past Reservations not implemented
 */

package com.example.utopia_campsite.owner

import android.R.attr.maxLines
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.CampsiteSummaryRow

@Composable
fun OwnerProfileScreen(owner: Owner) {
    var currentScreen by remember { mutableStateOf("Me") } //display user profile as the starting view

    Column(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Button(
                    modifier = Modifier.weight(.1f),
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black,
                    )
                ) {
                    Text(
                        text = "Me",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.weight(.1f).testTag("currentReservationsButton"),
                    onClick = { currentScreen = "Current Reservations" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White, // Background color
                        contentColor = Black,  // Text/icon color
                    )
                ) {
                    Text(
                        text = "Current Reservations",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier.weight(.1f),
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White, // Background color
                        contentColor = Black,  // Text/icon color
                    )
                ) {
                    Text(
                        text = "Past Reservations",
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
        } //end lazyColumn

        when (currentScreen) { //toggles partial ui composition depending on button selected
            "Current Reservations" -> OwnerProfileCurrentReservationScreen(owner)
        }
    } //end Column
} //end OwnerProfileScreen