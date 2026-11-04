/**
 *
 * This screen will show the user's current details.
 * Outstanding Issues: Currently no way to navigate to this screen
 */

package com.example.utopia_campsite.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.utopia_campsite.R
import com.example.utopia_campsite.ui.theme.UtopiacampsiteTheme
import java.io.File


/**
 * This screen will show the user's current details.
 * @param user: the user to display
 * @param onEditProfileClick: a callback function to navigate to the edit profile screen
 */
@Composable
fun UserProfileMeScreen(
    user: User,
    onEditProfileClick: () -> Unit = {}
){
    val context = LocalContext.current
    val appPolText = remember(context) {
        try {
            context.assets.open("appPolicy.txt").bufferedReader().use { it.readText() }
        } catch (_: Exception) {
            try {
                val file = File("app/src/main/assets/appPolicy.txt")
                if (file.exists()) {
                    file.readText()
                } else {
                    val devFile = File("app/src/main/java/com/example/utopia_campsite/user/appPolicy.txt")
                    if (devFile.exists()) devFile.readText() else "failed to load app policy."
                }
            } catch (_: Exception) {
                "failed to load app policy."
            }
        }
    }

    var showAppPolicy by remember { mutableStateOf(false) }

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

        Button(
            onClick = { showAppPolicy = true },
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally)
                .testTag("appPolicyButton")
        )
        {
            Text(text = "App Policy")
        }

        if (showAppPolicy) {

            Dialog(onDismissRequest = {showAppPolicy = false}){
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 6.dp,
                    modifier = Modifier.height(600.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(end=8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(appPolText)
                    }
                    Spacer(modifier = Modifier.heightIn(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            showAppPolicy = false
                        }
                        ) { Text("close") }
                    }
                }
            }
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
