package com.example.utopia_campsite.messaging
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Reads and writes conversations and messages in the firebase firestore
 */
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
    /**
     * Streams the messages of a conversation, oldest first, and emits again whenever one is added
     *
     * @param conversationId: the ID of the conversation (see conversationId)
     * @return a flow of every message in the conversation
     */
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
    /**
     * Streams the conversations a person is part of, newest first, whether they are the owner or the entrant
     * Sorted here instead of in the query so no Firestore index is needed yet
     *
     * @param userId: the uid of the signed in person
     * @return a flow of the person's conversations
     */
    fun observeConversations(userId: String): Flow<List<Conversation>> = callbackFlow {
        val registration = db.collection("conversations")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val conversations = snapshot?.documents?.map { toConversation(it) } ?: emptyList()
                trySend(conversations.sortedByDescending { it.lastMessageAt })
            }
        awaitClose { registration.remove() }
    } //end observeConversations
    /**
     * Sends a message, creating the conversation if this is the first one
     *
     * @param conversation: the conversation to send in
     * @param senderId: the uid of the person sending, must be the owner or the entrant
     * @param text: the text the person typed
     * @param onSuccess: called once the message is saved
     * @param onFailure: called if the sender is not in the conversation or saving fails
     */
    fun sendMessage(
        conversation: Conversation,
        senderId: String,
        text: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ){
        if(conversation.ownerId.isBlank() || conversation.userId.isBlank()){
            onFailure(IllegalStateException("This campsite has no owner to message"))
            return
        }
        if(senderId !in conversation.participants){
            onFailure(IllegalArgumentException("Sender is not part of this conversation"))
            return
        }

        val conversationRef = db.collection("conversations").document(conversation.id)
        val batch = db.batch()
        batch.set(
            conversationRef,
            mapOf(
                "campsiteId" to conversation.campsiteId,
                "campsiteName" to conversation.campsiteName,
                "ownerId" to conversation.ownerId,
                "userId" to conversation.userId,
                "participants" to conversation.participants,
                "lastMessage" to text.take(1000),
                "lastMessageAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        )
        batch.set(
            conversationRef.collection("messages").document(),
            mapOf(
                "senderId" to senderId,
                "text" to text,
                "sentAt" to FieldValue.serverTimestamp()
            )
        )
        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}