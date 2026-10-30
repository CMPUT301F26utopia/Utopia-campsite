package com.example.utopia_campsite.owner
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.utopia_campsite.ui.components.BlackButton
import com.example.utopia_campsite.ui.components.InputField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Preview
@Composable
fun RegisterCampsiteScreen(){
    var name by remember { mutableStateOf("") }
    var photoUrl by remember {mutableStateOf("")}
    var address by remember { mutableStateOf("") }
    var tags = remember {mutableStateListOf<String>()}
    var showTags by remember {mutableStateOf(false)}
    var lotteryWinners by remember { mutableStateOf("") }
    var lotteryWaitlist by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var maxOccupants by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //Title text, bolded
        Text(
            "Register Campsite Listing",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        //name input field
        Label("Campsite Name")
        InputField(name, {name=it})
        Row(horizontalArrangement=Arrangement.spacedBy(16.dp)){
            Column(
                modifier=Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //image PLACEHOLDER BOX. because we are storing imageURL as a string right now instead of an actual URI, im keeping this as a placeholder until I get time to fix it in sprint 2.
                Box(
                    modifier =Modifier
                        .size(120.dp)
                        .border(3.dp,Color.Black, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center

                ){
                    Text("+", fontSize = 40.sp)
                }
                InputField(
                    value = photoUrl,
                    onChange = {photoUrl = it},
                    placeholder = "Image URL"


                )
            }
            Column(
                modifier=Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy((8.dp))
            ){
                //Location input field.
                Label("Location")
                InputField(
                    value = address,
                    onChange = {address=it},
                    placeholder = "e.g 74 St. NW"
                )
                //Tags button.
                BlackButton(
                    text = if (tags.isEmpty()) "Tag(s)" else tags.joinToString { ", " },
                    onClick = {showTags=true}
                )
            }


            if(showTags){
                //add tags to the list of tags, one at a time.
                // creates a working copy of tags to edit ("draftTags") and opens a dialog box to add tags.
                var draftTags = remember {tags.toMutableStateList()}
                var newTag by remember {mutableStateOf("")}

                fun addTag(){
                    val t = newTag.trim()
                    if(t.isNotEmpty() && t !in draftTags) draftTags.add(t)
                    newTag = ""
                }
                Dialog(onDismissRequest = {showTags = false}){
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 6.dp
                    ){
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Tags", style = MaterialTheme.typography.titleLarge)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ){
                                InputField(
                                    value = newTag,
                                    onChange = {newTag = it},
                                    placeholder = "e.g lakeside",
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = {addTag()}) {Text("Add") }
                            }
                        }
                        //flowrow and input chip create that "pill box" list of tags. flowrow ensures that all tags are displayed.
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            draftTags.forEach { tag ->
                                InputChip(
                                    selected = false,
                                    onClick = {draftTags.remove(tag)},
                                    label = {Text(tag)},
                                    trailingIcon = {Text("✕")}
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ){
                            TextButton(onClick = {showTags=false}) { Text("Done") }
                            TextButton(onClick = {
                                tags.clear()
                                tags.addAll(draftTags)
                                showTags=false}
                            ) {Text("Done") }
                        }
                    }
                }
            }
        }
        //END PHOTO, LOCATION, AND TAGS ROW.

        //other fields. way easier to implement.
        Label("Number of Winners")
        InputField(
            value = lotteryWinners,
            onChange = {lotteryWinners=it},
            placeholder="e.g 10",
            keyboard = KeyboardType.Number
            )
        Label("Lottery Waitlist")
        InputField(
            value = lotteryWaitlist,
            onChange = { lotteryWaitlist = it },
            placeholder = "Optional: eg. 5...",
            keyboard = KeyboardType.Number
        )

        Label("Description")
        InputField(
            value = description,
            onChange = { description = it },
            placeholder = "Description...",
            singleLine = false,
            modifier = Modifier.heightIn(min = 80.dp)
        )

        Label("Maximum Occupants")
        InputField(
            value = maxOccupants,
            onChange = { maxOccupants = it},
            placeholder = "Capacity...",
            keyboard = KeyboardType.Number
        )

    }
}
@Composable
private fun Label(text:String){
    Text(text, fontSize = 14.sp)
}
