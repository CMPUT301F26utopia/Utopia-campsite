/**
 * Utopia 01.01.05
 * Shows the details of one campsite: photo, name, location, description, tags,
 * maximum occupancy, rating and available dates
 *
 * Outstanding Issues:
 * The join/leave waitlist buttons (US 01.01.06) are commented out, they need the current user which this screen does not get yet
 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.util.CampsiteDetailsContent

/**
 * Shows every detail of one campsite, opened by tapping it in the campsite list
 *
 * @param campsite: the campsite to show
 */
@Composable
fun CampsiteDetailsScreen(campsite: Campsites){
    CampsiteDetailsContent(campsite = campsite)
        /*
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (user.deviceID in campsite.lotteryWaitlist){
                Button(
                    onClick = { user.leaveLottery(campsite)}
                ) {
                    Text("Leave waitlist")
                } // end button
            } else {
                Button(
                    onClick = { user.joinLottery(campsite)}
                ) {
                    Text("Join waitlist")
                } // end button
            } // end if-else block

        } // end row*/

} //end CampsiteDetailsScreen