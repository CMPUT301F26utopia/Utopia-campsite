/**
 * Shows the complete details of the campsite
 * With reservation details
 * Shows a button to cancel a reservation
 * Utopia 01.07.01
 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites
import com.google.apps.card.v1.Divider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailsScreen(reservation: Reservation, onBackClick: () -> Unit, onCancelReservation : () -> Unit) {
    var loadedCampsite by remember(reservation.campsiteId) { mutableStateOf<Campsites?>(null)}
    LaunchedEffect(reservation.campsiteId) {
        CampsiteRepository().getCampsite(
            campsiteId = reservation.campsiteId,
            onSuccess = { loadedCampsite = it},
            onFailure = { loadedCampsite = null }
        )
    }

    val campsite = loadedCampsite ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            //Campsite Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = rememberAsyncImagePainter(model = campsite.photoUrl)

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (campsite.photoUrl.isNullOrBlank()) {
                        //default image
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "No Image Available",
                            tint = Color.Gray,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Loaded Image
                        Image(
                            painter = painter,
                            contentDescription = "Campsite Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } //end box column

                Spacer(modifier = Modifier.width(16.dp))

                // Campsite Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = campsite.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    //rating details
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107), // Amber color for star
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Rating: ${campsite.ratings.average()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    //location details
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp) //nudges the icon down slightly to align with text line
                        )
                        Text(
                            text = "Address: ${campsite.address}",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    //max occupancy details
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.People, // Icon for occupancy
                            contentDescription = "Max Occupancy",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Max Occupancy: ${campsite.maximumOccupants}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    //button to message owner
                    Button(
                        onClick = { /* to be implemented */},
                        modifier = Modifier.height(36.dp), // Slightly smaller to fit nicely in the column
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF), // Blue color
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Message",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Message", fontSize = 12.sp)
                    }
                } //end campsite details column
            } //end campsite header row

            Spacer(modifier = Modifier.height(24.dp))

            //description details
            Text(
                text = "Description",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = campsite.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            //reservation details card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Reservation Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(label = "Start Date:", value = reservation.startReservedDate)
                    DetailRow(label = "End Date:", value = reservation.endReservedDate)
                    DetailRow(label = "Number of Guests:", value = "${reservation.numOfGuests}")

                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //cancel reservation button
            Button(
                onClick = { onCancelReservation() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Cancel Reservation", color = Color.White)
            }
        } //end outside column
    } //end scaffold
} //end ReservationDetailsScreen

/**
 * used to display reservation details by line
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
