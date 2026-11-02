package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.utopia_campsite.R
import com.example.utopia_campsite.ui.theme.UtopiacampsiteTheme

@Composable
fun UserProfileMeScreen(
    user: User,
    onEditProfileClick: () -> Unit = {}
){

    Column(
        modifier = Modifier.fillMaxSize()

    ){
        if(!user.photoUrl.isNullOrBlank()){
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "Profile Picture",
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = "Profile Details:",
            Modifier.padding(start = 16.dp, bottom = 20.dp),
            fontSize = 30.sp
        )
        Text(
            text = "Name: " + user.firstName + " " + user.lastName,
            Modifier.padding(start = 16.dp, bottom = 5.dp)
        )

        Text(
            text = "Email: " + user.email,
            Modifier.padding(start = 16.dp, bottom = 5.dp)

        )
        Text(
            text = "Phone number: " + user.phone,
            Modifier.padding(start = 16.dp, bottom = 5.dp)
        )
        if(user.notificationsEnabled){
            Text(
                text = "Notifications: Enabled",
                Modifier.padding(start = 16.dp, bottom = 5.dp)
            )
        } else{
            Text(
                text = "Notifications: Disabled",
                Modifier.padding(start = 16.dp, bottom = 5.dp)
            )
        }

        Button(
            onClick = onEditProfileClick,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally)
                .testTag("editProfileButton")
        )
        {
            Text(text = "Edit Profile")
        }

    }//end column


}//end UserProfileMeScreen

@Preview(showBackground = true)
@Composable
fun UserProfileMeScreenPreview() {
    val sampleUser = User(
        deviceID = "sample_device_id",
        photoUrl = "https://tse1.explicit.bing.net/th/id/OIP.c2q_x-4ksFaYVEwC_khvEAHaLH?r=0&rs=1&pid=ImgDetMain&o=7&rm=3",
        email = "john.doe@example.com",
        firstName = "John",
        lastName = "Doe",
        notificationsEnabled = true,
        phone = "123-456-7890",
        currentReservations = mutableListOf(),
        pastReservations = emptyList()
    )
    UtopiacampsiteTheme {
        UserProfileMeScreen(user = sampleUser)
    }
}
