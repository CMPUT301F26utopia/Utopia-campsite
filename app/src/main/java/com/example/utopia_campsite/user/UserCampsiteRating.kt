package com.example.utopia_campsite.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Allows a user to give a rating for a Campsite Listing
 *
 * @param modifier: modifiying for UI compose elements
 * @Param onDismiss: callback for when user cancels their rating
 * @Param onSubmit: Callback for when user confirms their rating
 */
@OptIn(ExperimentalMaterial3Api::class) // Needed as BasicAlertDialog is experimental and may change
@Composable
fun UserCampsiteRating(
    modifier: Modifier,
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    var selectedRating by remember { mutableIntStateOf(0) }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Rate Campsite",
                    style = MaterialTheme.typography.titleLarge
                )

                // Displaying 5 stars
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (rating in 1..5) {
                        Icon(
                            imageVector = if (rating <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "$rating stars",
                            tint = if (rating <= selectedRating) Color.Blue else Color.Gray, // Subject to change
                            modifier = modifier
                                .weight(1f)
                                .clickable{
                                    selectedRating = rating
                                }
                                .testTag("ratingStar")
                        )
                    }
                }

                // Row for Submit and cancel button
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = modifier.testTag("cancelRatingButton"),
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        modifier = modifier.testTag("submitRatingButton"),
                        onClick = { onSubmit(selectedRating) }, enabled = selectedRating > 0
                    ) {
                        Text("Submit Rating")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampsiteRatingPreview() {
    UserCampsiteRating(modifier = Modifier, {}, {0})
}
