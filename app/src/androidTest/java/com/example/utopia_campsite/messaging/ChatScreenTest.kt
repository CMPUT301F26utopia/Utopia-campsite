package com.example.utopia_campsite.messaging

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for ChatScreen, the screen both the campsite owner and the entrant use to message
 * each other (Utopia 02.12.01)
 */
class ChatScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val fromOwner = Message(id = "1", senderId = "owner_001", text = "Check-in is after 3 PM.")
    private val fromUser = Message(id = "2", senderId = "user_001", text = "Thanks, is firewood included?")

    private val sent = mutableListOf<String>()
    private var backClicks = 0

    private fun setContent(
        messages: List<Message> = emptyList(),
        canSend: Boolean = true,
        errorMessage: String? = null
    ) {
        composeTestRule.setContent {
            MaterialTheme {
                ChatScreen(
                    title = "Banff Mountain Campground",
                    myId = "user_001",
                    messages = messages,
                    onSend = { sent.add(it) },
                    onBack = { backClicks++ }
                )
            }
        }
    } //end setContent

    // ---------- Messages ----------

    @Test
    fun testTitleIsShown() {
        setContent()
        composeTestRule.onNodeWithText("Banff Mountain Campground").assertIsDisplayed()
    }

    @Test
    fun testNoMessagesShowsEmptyState() {
        setContent()
        composeTestRule.onNodeWithText("No messages yet. Say hello!").assertIsDisplayed()
    }

    @Test
    fun testMessagesAreShown() {
        setContent(messages = listOf(fromOwner, fromUser))
        composeTestRule.onNodeWithText("Check-in is after 3 PM.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Thanks, is firewood included?").assertIsDisplayed()
        composeTestRule.onNodeWithText("No messages yet. Say hello!").assertDoesNotExist()
    }

    @Test
    fun testOwnMessageIsOnTheRightOfTheOtherPersonsMessage() {
        setContent(messages = listOf(fromOwner, fromUser))
        val other = composeTestRule.onNodeWithText("Check-in is after 3 PM.").getUnclippedBoundsInRoot()
        val mine = composeTestRule.onNodeWithText("Thanks, is firewood included?").getUnclippedBoundsInRoot()
        assertTrue(mine.right > other.right)
    }

    // ---------- Back ----------

    @Test
    fun testBackArrowCallsOnBack() {
        setContent()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, backClicks)
    }

    // ---------- Sending ----------

    @Test
    fun testSendIsDisabledWhenTheBoxIsEmpty() {
        setContent()
        composeTestRule.onNodeWithContentDescription("Send").assertIsNotEnabled()
    }

    @Test
    fun testSendIsDisabledForWhitespaceOnly() {
        setContent()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("   ")
        composeTestRule.onNodeWithContentDescription("Send").assertIsNotEnabled()
    }

    @Test
    fun testSendIsEnabledOnceTextIsTyped() {
        setContent()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Hello")
        composeTestRule.onNodeWithContentDescription("Send").assertIsEnabled()
    }

    @Test
    fun testSendIsDisabledForATooLongMessage() {
        setContent()
        composeTestRule.onNode(hasSetTextAction())
            .performTextInput("a".repeat(MessageValidation.MAX_LENGTH + 1))
        composeTestRule.onNodeWithContentDescription("Send").assertIsNotEnabled()
    }

    @Test
    fun testSendPassesTheTrimmedTextToOnSend() {
        setContent()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("  Hello there  ")
        composeTestRule.onNodeWithContentDescription("Send").performClick()
        assertEquals(listOf("Hello there"), sent)
    }

    @Test
    fun testBoxIsClearedAfterSending() {
        setContent()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Hello")
        composeTestRule.onNodeWithContentDescription("Send").performClick()
        composeTestRule.onNodeWithContentDescription("Send").assertIsNotEnabled()
    }

    @Test
    fun testNothingIsSentWhileTheSendButtonIsDisabled() {
        setContent()
        composeTestRule.onNodeWithContentDescription("Send").performClick()
        assertTrue(sent.isEmpty())
    }
    }