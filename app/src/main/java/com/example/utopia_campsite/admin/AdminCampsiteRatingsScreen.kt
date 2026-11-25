package com.example.utopia_campsite.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites
import com.example.utopia_campsite.user.UserRepository
/**
 * Lists every rating of one campsite, showing the name of the user who gave each one
 *
 * @param campsite: the campsite whose ratings are shown
 * @param navController: the navigation controller, used to go back to the campsite details
 * @param removeRating: removes one user's rating from the database; passed in so tests can use a fake
 * @param getUserName: looks up a user's full name from their uid; passed in so tests can use a fake
 */
@Composable
fun AdminCampsiteRatingsScreen(
    campsite: Campsites,
    navController: NavController,
    removeRating: (String, String, () -> Unit, (Exception) -> Unit) -> Unit =
        { id, userId, onSuccess, onFailure -> CampsiteRepository().removeRating(id, userId, onSuccess, onFailure) },
    getUserName: (String, (String) -> Unit, () -> Unit) -> Unit =
        { userId, onSuccess, onFailure ->
            UserRepository().getUser(
                userId = userId,
                onSuccess = { user -> onSuccess("${user.firstName} ${user.lastName}".trim()) },
                onFailure = { onFailure() }
            )
        }
){
    // Local copy of the ratings  so a removed rating disappears right away
    val ratings = remember(campsite.id) { mutableStateListOf<Pair<String, Int>>().apply { addAll(campsite.ratings.toList()) } }

    val userNames = remember(campsite.id) { mutableStateMapOf<String, String>() }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Look up the name of every user who left a rating when the screen opens
    LaunchedEffect(campsite.id) {
        for (userId in campsite.ratings.keys) {
            getUserName(
                userId,
                { name -> userNames[userId] = name.ifBlank { "Unnamed user" } },
                { userNames[userId] = "Unknown user" } // e.g. the user's account was deleted
            )
        } //end for
    } //end LaunchedEffect

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Ratings for ${campsite.name}",
            style = MaterialTheme.typography.headlineSmall
        )

        if(errorMessage!=null){
            Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp))

        }
        if (ratings.isEmpty()){
            Text(text = "This campsite has no ratings.", modifier = Modifier.padding(vertical = 16.dp))
        }
        LazyColumn(
            modifier = Modifier.weight(1f).padding(vertical=8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ratings){ rating ->
                val (userId, score) = rating

                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                            Text(text = userNames[userId] ?: "Loading...", fontWeight = FontWeight.Bold)
                            Text(text = "Rating: $score")
                        }//end column
                        Button(
                            onClick = {
                                errorMessage=null
                                removeRating(campsite.id, userId, { ratings.remove(rating) }, { errorMessage = "Could not remove rating" })
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Remove")
                        }//end button
                    }//end row
                }//end card
            }//end items
        }//end LazyColumn
        Button(
            onClick = {navController.popBackStack()},
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Back")
        }//end button
    }//end Column
}//end AdminCampsiteRatingsScreen