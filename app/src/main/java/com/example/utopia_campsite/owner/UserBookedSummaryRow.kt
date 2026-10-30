/**
 * Shows the summary of one user details in a list
 * full name, dates reserved is shown per user
 * shows a message and delete icon per user
 * Utopia 02.06.01
 * Utopia 02.07.01
 * Outstanding Issues:
 * Reserved date text not implemented(should come from user)
 * Message not implemented
 * Delete not implemented
 */

package com.example.utopia_campsite.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.utopia_campsite.user.User

@Composable
fun UserBookedSummaryRow(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp), // Slightly more padding for the two-line text
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Photo(Left)
        if (!user.photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Avatar",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        //Name and Reserved Date
        Column(
            modifier = Modifier.weight(1f) // Takes up available space, pushing icons to the right
        ) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Reserved Date: 20/11/2026 to 29/11/2026",
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        //Message and Delete icons(Right) ---
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp), // Space between top and bottom icon
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp) // Constrain width so it doesn't take too much space
        ) {
            // Message Icon (Top)
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "Message",
                tint = Color.Gray,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = {})
            )

            // Delete Icon (Bottom)
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = {})
            )
        } //end column for message/delete icons
    } //end row
} //end UserBookedSummaryRow