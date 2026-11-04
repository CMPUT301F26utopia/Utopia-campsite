/**
displays owner profiles in a list

Outstanding issues:
firebase delete not implemented
 */

package com.example.utopia_campsite.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.OnMultiWindowModeChangedProvider
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.owner.Owner
import com.example.utopia_campsite.owner.UserBookedSummaryRow
import com.example.utopia_campsite.user.User
import com.example.utopia_campsite.user.UserReservationSummaryRow

@Composable
fun OwnerProfilesScreen(ownersList: MutableList<Owner>) {

    val ownersListDisplayed = remember { mutableStateListOf<Owner>() }

    //next 2 lines is copying both lists for ui display
    ownersListDisplayed.clear()
    ownersListDisplayed.addAll(ownersList)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Slightly tighter spacing for lists
    ) {
        items(ownersListDisplayed) { owner ->
            OwnerProfileSummaryRow(
                owner,
                onRemoveClick = {
                    ownersListDisplayed.remove(it) //updates ui
                    ownersList.remove(it) //remove owner locally
                }
            )
        }
    } //end LazyColumn
} //end OwnerProfilesScreen
