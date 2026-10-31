package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.google.apps.card.v1.Columns
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun UserProfileMeScreen(user: User){
    Column(
        modifier = Modifier.fillMaxSize()

    ){
        Text(
            text = user.firstName + " " + user.lastName,
            fontSize = 30.sp,
            modifier = Modifier.alignment.AlignHorizontally

        )
        Text(
            text = "Current info:",
            Modifier.padding(20.dp)
        )
        Text(
            text = "Email: " + user.email
        )
        Text(
            text = "Phone: " + user.phone
        )
        Text(
            text = "Notifications Enabled: " + user.notificationsEnabled
        )

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(16.dp)
        )
        {
            Text(text = "Edit Profile")
        }






    }//end column


}//end UserProfileMeScreen