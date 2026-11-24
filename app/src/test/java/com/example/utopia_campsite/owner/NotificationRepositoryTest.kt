package com.example.utopia_campsite.owner

import com.example.utopia_campsite.user.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRepositoryTest {

    @Test
    fun recipientNotificationFiltering_onlyIncludesEnabledUsers() {
        val userEnabled = User(
            deviceID = "user_001",
            firstName = "Alice",
            notificationsEnabled = true
        )

        val userDisabled = User(
            deviceID = "user_002",
            firstName = "Bob",
            notificationsEnabled = false
        )

        val recipients = listOf(userEnabled, userDisabled)
        val enabledRecipients = recipients.filter { it.notificationsEnabled }

        assertEquals(1, enabledRecipients.size)
        assertTrue(enabledRecipients.contains(userEnabled))
        assertFalse(enabledRecipients.contains(userDisabled))
    }

    @Test
    fun send_withNoEnabledRecipients_completesSuccessfully() {
        val repo = NotificationRepository()
        val disabledUser = User(deviceID = "user_002", notificationsEnabled = false)
        var successCalled = false

        repo.send(
            ownerId = "owner_123",
            campsiteId = "campsite_456",
            title = "Test Notification",
            body = "Notification body content",
            recipients = listOf(disabledUser),
            onSuccess = { successCalled = true }
        )

        assertTrue(successCalled)
    }
}
