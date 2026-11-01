package com.example.utopia_campsite.owner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests the registration period checks used when listing a campsite in a lottery (US 02.04.03)
 */
class RegistrationPeriodTest {

    @Test
    fun buildRegistrationPeriod_validDates_returnsPeriod(){
        assertEquals("10/10/2026-20/10/2026", buildRegistrationPeriod("10/10/2026", "20/10/2026"))
    } //end buildRegistrationPeriod_validDates_returnsPeriod

    @Test
    fun buildRegistrationPeriod_sameDay_returnsPeriod(){
        assertEquals("10/10/2026-10/10/2026", buildRegistrationPeriod("10/10/2026", "10/10/2026")) // a one-day registration is allowed
    } //end buildRegistrationPeriod_sameDay_returnsPeriod

    @Test
    fun buildRegistrationPeriod_startAfterEnd_returnsNull(){
        assertNull(buildRegistrationPeriod("20/10/2026", "10/10/2026"))
    } //end buildRegistrationPeriod_startAfterEnd_returnsNull

    @Test
    fun buildRegistrationPeriod_missingDate_returnsNull(){
        assertNull(buildRegistrationPeriod("", "20/10/2026"))
        assertNull(buildRegistrationPeriod("10/10/2026", ""))
    } //end buildRegistrationPeriod_missingDate_returnsNull

    @Test
    fun buildRegistrationPeriod_dateThatDoesNotExist_returnsNull(){
        assertNull(buildRegistrationPeriod("31/02/2026", "10/03/2026")) // February has no 31st
    } //end buildRegistrationPeriod_dateThatDoesNotExist_returnsNull

}