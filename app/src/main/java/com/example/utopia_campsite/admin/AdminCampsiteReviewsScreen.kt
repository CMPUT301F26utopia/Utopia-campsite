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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
/**
 * Lists every review of one campsite
 *
 * @param campsite: the campsite whose reviews are shown
 * @param navController: the navigation controller, used to go back to the campsite details
 */
@Composable
fun AdminCampsiteReviewsScreen(
    campsite: Campsites,
    navController: NavController
){
    val campsiteRepository = remember { CampsiteRepository() }

    val reviews = remember(campsite.id) { mutableStateListOf<String>().apply { addAll(campsite.reviews) } }

    var errorMessage by remember { mutableStateOf<String?>(null) }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Reviews for ${campsite.name}",
            style = MaterialTheme.typography.headlineSmall
        )

        if(errorMessage!=null){
            Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp))

        }
        if (reviews.isEmpty()){
            Text(text = "This campsite has no reviews.", modifier = Modifier.padding(vertical = 16.dp))
        }
        LazyColumn(
            modifier = Modifier.weight(1f).padding(vertical=8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews){ review ->
                val author = review.substringBefore(": ", missingDelimiterValue = "")
                val body = review.substringAfter(": ", missingDelimiterValue = review)

                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                            if (author.isNotEmpty()) {
                                Text(text = author, fontWeight = FontWeight.Bold)
                            }//end if
                            Text(text = body)
                        }//end column
                        Button(
                            onClick = {
                                errorMessage=null
                                campsiteRepository.removeReview(
                                    campsiteId = campsite.id,
                                    review = review,
                                    onSuccess = {reviews.remove(review)},
                                    onFailure = {errorMessage = "Could not remove review"}
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Remove")
                        }
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
}//end AdminCampsiteReviewScreen