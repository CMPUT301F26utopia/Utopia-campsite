package com.example.utopia_campsite.user

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun UserCampsiteRating(modifier: Modifier) {
    Text("Hello")
}


@Preview(showBackground = true)
@Composable
fun CampsiteRatingPreview() {
    UserCampsiteRating(modifier = Modifier)
}
