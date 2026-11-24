package com.example.utopia_campsite.owner

import com.example.utopia_campsite.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime

data class Notification (
    val title: String = "",
    val body: String = "",
    val ownerId: String = "",
    val date: String = "",
    val time: String = ""
)


class NotificationRepository() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun send(ownerId: String, campsiteId: String, title: String, body: String, recipients: List<User>) {
        val date = LocalDate.now()
        val time = LocalTime.now()
        val noti = Notification(title, body, ownerId, date.toString(), time.toString())




    }
}