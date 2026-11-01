package com.example.utopia_campsite.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
* Designated Welcome Screen
* */

@Composable
fun WelcomeScreen(
    modifier: Modifier,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit
){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Text(
            text = "Welcome to\nUtopia\nCampsite",
            fontSize = 40.sp,
            lineHeight = 60.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = modifier.padding(30.dp))

        Button(
            onClick = onSignIn,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C8B3),
                contentColor = Color.White
            )
        ) {
            Text("SIGN IN")
        }

        Spacer(modifier = modifier.padding(5.dp))

        Button(
            onClick = onCreateAccount,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C8B3),
                contentColor = Color.White
            )
        ) {
            Text("CREATE ACCOUNT")
        }

        Spacer(modifier = modifier.padding(60.dp))

        Button(
            onClick = onForgotPassword,
            modifier = modifier.fillMaxWidth(0.75f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            Text("Forgot Password")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(modifier = Modifier, {}, {}, {})
}