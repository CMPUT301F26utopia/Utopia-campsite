package com.example.utopia_campsite.owner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.utopia_campsite.user.LotteryWinnerScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for listing campsites with other owners in a shared lottery (Utopia 02.04.02)
 * The screens are shown with fake data, and every firebase write is replaced by a stub
 * that changes the fake data instead, so the tests don't touch firebase
 */
class SharedLotteryUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val today = "01/12/2026"

    private val lottery = Campsites(
        id = "lottery",
        name = "Pine Lake",
        ownerId = "host",
        registrationDate = "01/12/2026-10/12/2026",
        bookingDate = "20/12/2026-27/12/2026",
        maximumOccupants = 4,
        campsiteIds = listOf("hostSite"),
        memberOwnerIds = listOf("host")
    )

    private val hostSite = Campsites(id = "hostSite", name = "Host Site", address = "1 Lake Rd", ownerId = "host",
        bookingDate = "20/12/2026-27/12/2026", maximumOccupants = 4)
    private val guestSite = Campsites(id = "guestSite", name = "Guest Site", address = "2 River Rd", ownerId = "guest",
        bookingDate = "20/12/2026-27/12/2026", maximumOccupants = 4)
    private val wrongDatesSite = Campsites(id = "wrongDates", name = "Wrong Dates", ownerId = "guest",
        bookingDate = "01/01/2027-05/01/2027", maximumOccupants = 4)

    private val owners = listOf(
        Owner(deviceID = "host", firstName = "Hannah", lastName = "Host", email = "host@camp.ca"),
        Owner(deviceID = "guest", firstName = "Jane", lastName = "Doe", email = "jane@camp.ca"),
        Owner(deviceID = "sam", firstName = "Sam", lastName = "Lee", email = "sam@woods.ca")
    )

    /**
     * Stub of SharedLotteryRepository.inviteOwner: adds the owner to the lottery's invites
     * @param invited: stands in for the lottery's invitedOwnerIds in firebase
     */
    private fun stubInvite(invited: MutableList<String>, ownerId: String, finished: (String?) -> Unit) {
        invited.add(ownerId)
        finished(null)
    } //end stubInvite

    /**
     * Stub of SharedLotteryRepository.acceptInvite: adds the campsite to the lottery
     * @param campsiteIds: stands in for the lottery's campsiteIds in firebase
     */
    private fun stubAccept(campsiteIds: MutableList<String>, campsiteId: String, finished: (String?) -> Unit) {
        campsiteIds.add(campsiteId)
        finished(null)
    } //end stubAccept

    /**
     * Stub of SharedLotteryRepository.chooseCampsite: records the winner's choice
     * @param choices: stands in for the lottery's winnerChoices in firebase
     * @return null on success, or an error message if the campsite was already taken
     */
    private fun stubChoose(choices: MutableMap<String, String>, userId: String, campsiteId: String): String? {
        if (campsiteId in choices.values) return "Another winner already chose this campsite."
        choices[userId] = campsiteId
        return null
    } //end stubChoose

    // ----- Hosting a shared lottery -----

    @Test
    fun testHostCannotCreateWithoutACampsite() {
        var created = false
        composeTestRule.setContent {
            HostSharedLotteryContent(listOf(hostSite), null, onBack = {}, onCreate = { _, _, _, _, _ -> created = true })
        }
        composeTestRule.onNodeWithText("Registration start DD/MM/YYYY").performTextInput("01/12/2026")
        composeTestRule.onNodeWithText("Registration end DD/MM/YYYY").performTextInput("10/12/2026")
        composeTestRule.onNodeWithText("Number of winners").performTextInput("2")
        composeTestRule.onNodeWithText("Create lottery").performClick()

        composeTestRule.onNodeWithText("Select at least one campsite", substring = true).assertIsDisplayed()
        assertTrue(!created)
    } //end testHostCannotCreateWithoutACampsite

    @Test
    fun testHostCanCreateWithOnlyOneOfTheirCampsites() {
        var savedIds: List<String> = emptyList()
        var savedCount = 0
        composeTestRule.setContent {
            HostSharedLotteryContent(listOf(hostSite), null, onBack = {}, onCreate = { ids, _, _, count, finished ->
                savedIds = ids
                savedCount = count
                finished(null)
            })
        }
        composeTestRule.onNodeWithTag("select_hostSite").performClick()
        composeTestRule.onNodeWithText("Registration start DD/MM/YYYY").performTextInput("01/12/2026")
        composeTestRule.onNodeWithText("Registration end DD/MM/YYYY").performTextInput("10/12/2026")
        composeTestRule.onNodeWithText("Number of winners").performTextInput("2")
        composeTestRule.onNodeWithText("Create lottery").performClick()

        assertEquals(listOf("hostSite"), savedIds)
        assertEquals(2, savedCount)
    } //end testHostCanCreateWithOnlyOneOfTheirCampsites

    // ----- Inviting owners -----

    @Test
    fun testInviteListHidesTheHostAndShowsOtherOwners() {
        composeTestRule.setContent { InviteOwnersContent(lottery, owners, null, onBack = {}, onInvite = { _, _ -> }) }

        composeTestRule.onNodeWithTag("invite_host").assertDoesNotExist()
        composeTestRule.onNodeWithText("Jane Doe", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Sam Lee", substring = true).assertIsDisplayed()
    } //end testInviteListHidesTheHostAndShowsOtherOwners

    @Test
    fun testInviteListShowsAtMostTenOwners() {
        val many = (1..15).map { Owner(deviceID = "o$it", firstName = "Owner", lastName = "Number$it") }
        composeTestRule.setContent { InviteOwnersContent(lottery, many, null, onBack = {}, onInvite = { _, _ -> }) }

        composeTestRule.onNodeWithTag("invite_o1").assertExists()
        composeTestRule.onNodeWithTag("invite_o11").assertDoesNotExist()
    } //end testInviteListShowsAtMostTenOwners

    @Test
    fun testSearchFindsOwnersByName() {
        composeTestRule.setContent { InviteOwnersContent(lottery, owners, null, onBack = {}, onInvite = { _, _ -> }) }
        composeTestRule.onNodeWithTag("ownerSearch").performTextInput("sam")

        composeTestRule.onNodeWithTag("invite_sam").assertIsDisplayed()
        composeTestRule.onNodeWithTag("invite_guest").assertDoesNotExist()
    } //end testSearchFindsOwnersByName

    @Test
    fun testInvitingAnOwnerSavesTheInviteAndDisablesTheButton() {
        val invited = mutableListOf<String>()
        composeTestRule.setContent {
            InviteOwnersContent(lottery, owners, null, onBack = {}, onInvite = { id, finished -> stubInvite(invited, id, finished) })
        }
        composeTestRule.onNodeWithTag("invite_guest").performClick()

        assertEquals(listOf("guest"), invited)
        composeTestRule.onNodeWithTag("invite_guest").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Invited").assertIsDisplayed()
    } //end testInvitingAnOwnerSavesTheInviteAndDisablesTheButton

    // ----- Answering an invite -----

    @Test
    fun testInvitedOwnerCanOnlyAddAMatchingCampsite() {
        composeTestRule.setContent {
            AcceptInviteContent(lottery, listOf(guestSite, wrongDatesSite), "guest", today, null,
                onBack = {}, onAccept = { _, _ -> }, onDecline = {})
        }

        composeTestRule.onNodeWithTag("add_guestSite").assertIsDisplayed()
        composeTestRule.onNodeWithTag("add_wrongDates").assertDoesNotExist()
        composeTestRule.onNodeWithText("The booking period must be", substring = true).assertIsDisplayed()
    } //end testInvitedOwnerCanOnlyAddAMatchingCampsite

    @Test
    fun testAcceptingAddsTheCampsiteToTheLottery() {
        val campsiteIds = lottery.campsiteIds.toMutableList()
        composeTestRule.setContent {
            AcceptInviteContent(lottery, listOf(guestSite), "guest", today, null,
                onBack = {}, onAccept = { id, finished -> stubAccept(campsiteIds, id, finished) }, onDecline = {})
        }
        composeTestRule.onNodeWithTag("add_guestSite").performClick()

        assertEquals(listOf("hostSite", "guestSite"), campsiteIds)
    } //end testAcceptingAddsTheCampsiteToTheLottery

    @Test
    fun testDecliningTheInvite() {
        var declined = false
        composeTestRule.setContent {
            AcceptInviteContent(lottery, listOf(guestSite), "guest", today, null,
                onBack = {}, onAccept = { _, _ -> }, onDecline = { finished -> declined = true; finished(null) })
        }
        composeTestRule.onNodeWithText("Decline invite").performClick()

        assertTrue(declined)
    } //end testDecliningTheInvite

    @Test
    fun testCannotJoinAfterRegistrationEnds() {
        composeTestRule.setContent {
            AcceptInviteContent(lottery, listOf(guestSite), "guest", "11/12/2026", null,
                onBack = {}, onAccept = { _, _ -> }, onDecline = {})
        }

        composeTestRule.onNodeWithTag("add_guestSite").assertDoesNotExist()
        composeTestRule.onNodeWithText("registration period is over", substring = true).assertIsDisplayed()
    } //end testCannotJoinAfterRegistrationEnds

    // ----- Lottery details: only the host can change the lottery -----

    @Test
    fun testInvitedOwnerCannotEditTheLottery() {
        composeTestRule.setContent {
            CampsiteScreens(lottery, onBack = {}, onSave = { _, _ -> }, onSaveLottery = { _, _ -> }, canEdit = false)
        }

        composeTestRule.onNodeWithText("Edit Lottery").assertDoesNotExist()
        composeTestRule.onNodeWithText("Edit campsite").assertDoesNotExist()
        composeTestRule.onNodeWithText("Invite owners").assertDoesNotExist()
        composeTestRule.onNodeWithText("Only the lottery's host", substring = true).performScrollTo().assertIsDisplayed()
    } //end testInvitedOwnerCannotEditTheLottery

    @Test
    fun testHostSeesInviteOwnersButton() {
        var opened = false
        composeTestRule.setContent {
            CampsiteScreens(lottery, onBack = {}, onSave = { _, _ -> }, onSaveLottery = { _, _ -> },
                canEdit = true, onInviteOwners = { opened = true })
        }
        composeTestRule.onNodeWithText("Edit Lottery").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Invite owners").performScrollTo().performClick()

        assertTrue(opened)
    } //end testHostSeesInviteOwnersButton

    // ----- Winner chooses a campsite -----

    @Test
    fun testWinnerChoosesWhichCampsiteToStayAt() {
        val shared = lottery.copy(campsiteIds = listOf("hostSite", "guestSite"))
        val choices = mutableMapOf<String, String>()
        composeTestRule.setContent {
            LotteryWinnerScreen(
                campsite = shared,
                opportunityId = "opportunity",
                isWinner = true,
                availableDates = listOf(shared.bookingDate),
                onBack = {},
                onDecline = { null },
                campsiteOptions = SharedLotteryRules.availableChoices(shared, listOf(hostSite, guestSite)),
                onChooseCampsite = { id -> stubChoose(choices, "user1", id) }
            )
        }
        composeTestRule.onNodeWithText("Choose your campsite").assertIsDisplayed()
        composeTestRule.onNodeWithTag("choose_guestSite").performScrollTo().performClick()

        composeTestRule.waitForIdle()
        assertEquals("guestSite", choices["user1"])
        composeTestRule.onNodeWithText("You booked Guest Site", substring = true).assertIsDisplayed()
    } //end testWinnerChoosesWhichCampsiteToStayAt

    @Test
    fun testTakenCampsiteIsNotOffered() {
        val shared = lottery.copy(campsiteIds = listOf("hostSite", "guestSite"), winnerChoices = mapOf("user0" to "hostSite"))
        composeTestRule.setContent {
            LotteryWinnerScreen(
                campsite = shared,
                opportunityId = "opportunity",
                isWinner = true,
                availableDates = emptyList(),
                onBack = {},
                onDecline = { null },
                campsiteOptions = SharedLotteryRules.availableChoices(shared, listOf(hostSite, guestSite)),
                onChooseCampsite = { null }
            )
        }

        composeTestRule.onNodeWithTag("choose_hostSite").assertDoesNotExist()
        composeTestRule.onNodeWithTag("choose_guestSite").assertExists()
    } //end testTakenCampsiteIsNotOffered

    @Test
    fun testAllCampsitesTakenMessage() {
        composeTestRule.setContent {
            LotteryWinnerScreen(
                campsite = lottery,
                opportunityId = "opportunity",
                isWinner = true,
                availableDates = emptyList(),
                onBack = {},
                onDecline = { null },
                campsiteOptions = emptyList(),
                onChooseCampsite = { null }
            )
        }

        composeTestRule.onNodeWithText("All campsites in this lottery have been taken.").assertIsDisplayed()
    } //end testAllCampsitesTakenMessage

    @Test
    fun testChoosingAnAlreadyTakenCampsiteShowsTheError() {
        // another winner took guestSite after this screen loaded
        val choices = mutableMapOf("user0" to "guestSite")
        composeTestRule.setContent {
            LotteryWinnerScreen(
                campsite = lottery.copy(campsiteIds = listOf("hostSite", "guestSite")),
                opportunityId = "opportunity",
                isWinner = true,
                availableDates = emptyList(),
                onBack = {},
                onDecline = { null },
                campsiteOptions = listOf(hostSite, guestSite),
                onChooseCampsite = { id -> stubChoose(choices, "user1", id) }
            )
        }
        composeTestRule.onNodeWithTag("choose_guestSite").performScrollTo().performClick()

        composeTestRule.waitForIdle()
        assertNull(choices["user1"])
        composeTestRule.onNodeWithText("Another winner already chose this campsite.").assertExists()
    } //end testChoosingAnAlreadyTakenCampsiteShowsTheError
}
