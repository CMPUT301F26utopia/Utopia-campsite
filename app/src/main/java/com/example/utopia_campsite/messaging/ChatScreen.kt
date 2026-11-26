package com.example.utopia_campsite.messaging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Shows one conversation
 *
 * @param title: shown in the top bar, e.g. the campsite name
 * @param myId: the uid of the person looking at the screen, their messages are shown on the right
 * @param messages: the messages to show, oldest first
 * @param onSend: called with the cleaned text when the send button is pressed
 * @param onBack: called when the back arrow is pressed
 * @param modifier: modifier for the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    title: String,
    myId: String,
    messages: List<Message>,
    onSend:(String)->Unit,
    onBack:()-> Unit,
    modifier:Modifier= Modifier
){

    //the text being typed
    var draft by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {Text(text = title)},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text("Message") },
                    modifier = Modifier.weight(1f),
                    maxLines = 4
                )
                IconButton(
                    onClick = {
                        val cleaned = MessageValidation.clean(draft)
                        if (cleaned != null) {
                            onSend(cleaned)
                            draft = ""
                        }
                    },
                    enabled = MessageValidation.clean(draft) != null
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    ){
        innerPadding ->
        if(messages.isEmpty()){
            Text(
                text = "No messages yet. Say hello!",
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = {it.id}){ message->
                    MessageBubble(message=message, isMine = message.senderId ==myId)
                }
            }
        }
    }
}

/**
 * Shows one message as a bubble, the person's own messages on the right in the main colour,
 * the other person's on the left
 *
 * @param message: the message to show
 * @param isMine: true if the person looking at the screen sent it
 */
@Composable
private fun MessageBubble(message: Message, isMine: Boolean, ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ){
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ){
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)){
                Text(text = message.text)
                message.sentAt?.let {
                    Text(
                        text = formatSentTime(it),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
private fun formatSentTime(date: Date): String =
    SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(date)

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        title = "Lakeview Campsite",
        myId = "user1",
        messages = listOf(
            Message(id = "1", senderId = "owner1", text = "Welcome! Check-in is after 3 PM.", sentAt = Date()),
            Message(id = "2", senderId = "user1", text = "Thanks! Is firewood included?", sentAt = Date()),
            Message(id = "3", senderId = "owner1", text = "Yes, there is a bundle at the site.", sentAt = Date())
        ),
        onSend = {},
        onBack = {}
    )
}