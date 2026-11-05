package com.example.utopia_campsite.owner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests picking lottery winners and checking when the registration period is over (US 02.05.01)
 */
class LotteryDrawTest {

    @Test
    fun pickWinners_moreEntrantsThanWinners_picksThatMany(){
        val waitlist = listOf("a", "b", "c", "d", "e")
        val winners = pickWinners(waitlist, 2)
        assertEquals(2, winners.size)
        assertTrue(waitlist.containsAll(winners)) // every winner was on the waitlist
        assertEquals(2, winners.toSet().size) // nobody wins twice
    } //end pickWinners_moreEntrantsThanWinners_picksThatMany

    @Test
    fun pickWinners_fewerEntrantsThanWinners_everyoneWins(){
        assertEquals(setOf("a", "b"), pickWinners(listOf("a", "b"), 5).toSet())
    } //end pickWinners_fewerEntrantsThanWinners_everyoneWins

    @Test
    fun pickWinners_emptyWaitlist_noWinners(){
        assertTrue(pickWinners(emptyList(), 3).isEmpty())
    } //end pickWinners_emptyWaitlist_noWinners

    @Test
    fun isRegistrationOver_dayAfterLastDay_returnsTrue(){
        assertTrue(isRegistrationOver("10/10/2026-20/10/2026", "21/10/2026"))
    } //end isRegistrationOver_dayAfterLastDay_returnsTrue

    @Test
    fun isRegistrationOver_onLastDay_returnsFalse(){
        assertFalse(isRegistrationOver("10/10/2026-20/10/2026", "20/10/2026")) // entrants can still join on the last day
    } //end isRegistrationOver_onLastDay_returnsFalse

    @Test
    fun isRegistrationOver_noRegistrationPeriod_returnsFalse(){
        assertFalse(isRegistrationOver("", "21/10/2026"))
    } //end isRegistrationOver_noRegistrationPeriod_returnsFalse

}