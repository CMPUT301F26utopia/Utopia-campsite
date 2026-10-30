/**
 * Displays the campsite details such as name, address
 * And number of users booked
 * Also displays the list of users who booked the campsites
 *
 * Outstanding issues:
 * View other listings navigation not implemented
 * line 203 implementation may change depending on how number of reservation available is implemented
 */

package com.example.utopia_campsite.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteReservedUsersScreen(campsite: Campsites) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)) {
                // 1. "< View other listings" on the top left
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {})
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "View other listings",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                // 2. "Booked Slots" centered and bold below it
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Booked Slots",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            } //end column
        } //end topBar
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Slightly tighter spacing for lists
        ) {
            item {
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    // Aligns the image and the text to the top of the row
                    verticalAlignment = Alignment.Top
                ) {
                    // --- LEFT HALF: IMAGE ---
                    if (campsite.photoUrl != null) {
                        AsyncImage(
                            model = campsite.photoUrl,
                            contentDescription = "Campsite Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp) // Square size to match the wireframe
                                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                        )
                    } else {
                        // display a default image
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "No Image",
                                tint = Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // --- RIGHT HALF: TEXT DETAILS ---
                    Column(
                        modifier = Modifier
                            .weight(1f) // Takes up remaining horizontal space
                    ) {
                        // Campsite Name
                        Text(
                            text = campsite.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Address Row
                        Row(
                            verticalAlignment = Alignment.Top, // Align to top in case address wraps to 2 lines
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Location",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp), // Slight adjustment to align with text
                                tint = Color.Black
                            )
                            Text(
                                text = "Address: ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                lineHeight = 18.sp // Good for multi-line addresses
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = campsite.address,
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            )
                        }// end Row

                        //Number of users booked row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group, // The group of people icon
                                contentDescription = "Reserved Users",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Winners Booked: ${campsite.usersBooked.size}/${campsite.lotteryWinners}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        } //end users booked row
                    } //end column
                } //end outer row
            } //end item

            items(campsite.usersBooked) { user ->
                UserBookedSummaryRow(user = user)
            }
        } //end LazyColumn
    } //end Scaffold
} //end CampsiteReservedUSersScreen

