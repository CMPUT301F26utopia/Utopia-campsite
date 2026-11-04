/**
 * Shows the summary of one owner profile
 * full name and role and a message if they violated the app policy
 * shows remove profile button to remove profile
 * Outstanding Issues:
 * firebase delete not implemented
 */

package com.example.utopia_campsite.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.Owner
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserRepository

@Composable
fun OwnerProfileSummaryRow(owner: Owner, onRemoveClick : (Owner) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp), // Slightly more padding for the two-line text
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Photo(Left)
        if (!owner.photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = owner.photoUrl,
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

        //Name and Role
        Column(
            modifier = Modifier.weight(1f) // Takes up available space, pushing icons to the right
        ) {
            Text(
                text = "${owner.firstName} ${owner.lastName}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            if (owner.policyViolated) {
                Text(
                    text = "Owner(violated app policy)",
                    fontSize = 12.sp,
                    color = Color.Red,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            else {
                Text(
                    text = "Owner",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } //end column

        // Remove Profile Button
        Button(
            onClick = { onRemoveClick(owner) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Material Red
            shape = RoundedCornerShape(50), // Fully rounded pill shape
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
            modifier = Modifier.height(32.dp) // Keep it compact
        ) {
            Text(
                text = "Remove Profile",
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } //end row
} //end OwnerProfileSummaryRow