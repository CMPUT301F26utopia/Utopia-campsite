package com.example.utopia_campsite.owner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests picking lottery winners and losers and checking when the registration period is over
 * (US 02.05.01, 01.03.01, 01.03.02)
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
    fun pickLosers_someWinners_returnsEveryoneElse(){
        assertEquals(listOf("a", "c"), pickLosers(listOf("a", "b", "c", "d"), listOf("b", "d")))
    } //end pickLosers_someWinners_returnsEveryoneElse

    @Test
    fun pickLosers_everyoneWins_returnsEmpty(){
        assertTrue(pickLosers(listOf("a", "b"), listOf("a", "b")).isEmpty())
    } //end pickLosers_everyoneWins_returnsEmpty

    @Test
    fun pickLosers_noWinners_everyoneLoses(){
        assertEquals(listOf("a", "b"), pickLosers(listOf("a", "b"), emptyList()))
    } //end pickLosers_noWinners_everyoneLoses

    @Test
    fun pickLosers_afterPickWinners_everyEntrantGetsOneResult(){
        val waitlist = listOf("a", "b", "c", "d", "e")
        val winners = pickWinners(waitlist, 2)
        val losers = pickLosers(waitlist, winners)
        assertEquals(3, losers.size) // 5 entrants - 2 winners = 3 losers
        assertTrue(losers.none { it in winners }) // nobody is told they won and lost
        assertEquals(waitlist.toSet(), (winners + losers).toSet()) // everyone on the waitlist is told something
    } //end pickLosers_afterPickWinners_everyEntrantGetsOneResult

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
