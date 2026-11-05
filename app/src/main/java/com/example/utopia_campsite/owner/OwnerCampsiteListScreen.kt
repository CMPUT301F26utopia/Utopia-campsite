package com.example.utopia_campsite.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OwnerCampsiteListScreen(
    modifier: Modifier,
    onCampsiteClick: (Campsites) -> Unit,
    onFilterClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var submittedQuery by remember{mutableStateOf("")}

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search campsites by name") },
            leadingIcon = {
                IconButton(
                    onClick = {
                        submittedQuery = searchQuery
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                }//end iconButton
            },//end leading icon
            trailingIcon = {
                IconButton(onClick = onFilterClick) {

                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Filter Icon"
                    )
                }//end iconButton
            },//end trailing icon
            singleLine = true

        )
    }
}


@Preview(showBackground = true)
@Composable
fun OwnerCampsiteListScreenPreview() {
    OwnerCampsiteListScreen(modifier = Modifier, {}, {})
}