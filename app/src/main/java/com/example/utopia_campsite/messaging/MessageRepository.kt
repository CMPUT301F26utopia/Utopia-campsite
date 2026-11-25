package com.example.utopia_campsite.messaging
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class MessageRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    /**
     * Converts a message document into a Message
     * A message that was just sent has no server time yet, so the time is estimated from the device clock
     *
     * @param doc: the message document
     */
    private fun toMessage(doc: DocumentSnapshot): Message = Message(
        id = doc.id,
        senderId = doc.getString("senderId") ?: "",
        text = doc.getString("text")?: "",
        sentAt = doc.getTimestamp("sentAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate()
    )
    /**
     * Converts a conversation document into a Conversation
     *
     * @param doc: the conversation document
     */
    private fun toConversation(doc: DocumentSnapshot): Conversation = Conversation(
        id = doc.id,
        campsiteId = doc.getString("campsiteId") ?: "",
        campsiteName = doc.getString("campsiteName") ?: "",
        ownerId = doc.getString("ownerId") ?: "",
        userId = doc.getString("userId") ?: "",
        lastMessage = doc.getString("lastMessage") ?: "",
        lastMessageAt = doc.getTimestamp("lastMessageAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate()

    )
    fun observeMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val registration = db.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("sentAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.map { toMessage(it) } ?: emptyList())
            }
        awaitClose { registration.remove() }
    }
}