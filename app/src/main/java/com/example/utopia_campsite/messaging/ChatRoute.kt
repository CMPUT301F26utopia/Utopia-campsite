
/**
 * Connects ChatScreen to the firebase firestore through MessageRepository
 *
 * It keeps listening for new messages while it is on screen, sends the messages the person types
 * and shows an error line when loading or sending fails
 *

 */
package com.example.utopia_campsite.messaging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.catch

/**
 * Shows one conversation, live
 *
 * @param conversation: the conversation to show, see Conversation.forCampsite
 * @param myId: the uid of the signed in person, must be the owner or the entrant of the conversation
 * @param onBack: called when the back arrow is pressed
 * @param canSend: false makes the conversation read only, e.g. after the reservation ended
 * @param repository: the repository to read and write messages with
 */
@Composable
fun ChatRoute(
    conversation: Conversation,
    myId: String,
    onBack: () -> Unit,
    repository: MessageRepository = remember { MessageRepository() }
) {
    var sendError by remember { mutableStateOf<String?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    // starts listening when the screen opens and stops when it closes
    val messages by remember(conversation.id) {
        repository.observeMessages(conversation.id).catch {
            loadError = "Couldn't load messages: ${it.message}"
            emit(emptyList())
        }
    }.collectAsState(initial = emptyList())

    ChatScreen(
        title = conversation.campsiteName,
        myId = myId,
        messages = messages,
        onSend = { text ->
            sendError = null
            repository.sendMessage(
                conversation = conversation,
                senderId = myId,
                text = text,
                onSuccess = { sendError = null },
                onFailure = { sendError = "Couldn't send your message: ${it.message}" }
            )
        },
        onBack = onBack
    )
} //end ChatRoute