package com.example.utopia_campsite.owner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for the registration date validation and inclusive overlap rules. */
class MultipleLotteryDatesTest {
    @Test
    fun validPeriodsIncludeSingleDayAndLeapDay() {
        assertTrue(MultipleLotteryDates.isValidPeriod("29/02/2028", "29/02/2028"))
        assertTrue(MultipleLotteryDates.isValidPeriod("30/12/2026", "02/01/2027"))
    }

    @Test
    fun invalidFormatsCalendarDatesAndReversedPeriodsAreRejected() {
        assertFalse(MultipleLotteryDates.isValidPeriod("1/12/2026", "02/12/2026"))
        assertFalse(MultipleLotteryDates.isValidPeriod("31/02/2026", "02/03/2026"))
        assertFalse(MultipleLotteryDates.isValidPeriod("02/12/2026", "01/12/2026"))
    }

    @Test
    fun overlappingPeriodsIncludeSharedBoundaryAndContainment() {
        assertEquals(true, MultipleLotteryDates.overlaps("10/12/2026", "12/12/2026", "12/12/2026", "15/12/2026"))
        assertEquals(true, MultipleLotteryDates.overlaps("10/12/2026", "20/12/2026", "12/12/2026", "15/12/2026"))
    }

    @Test
    fun separatePeriodsDoNotOverlap() {
        assertEquals(false, MultipleLotteryDates.overlaps("10/12/2026", "12/12/2026", "13/12/2026", "15/12/2026"))
    }

    @Test
    fun invalidPeriodDoesNotProduceAnAvailabilityDecision() {
        assertNull(MultipleLotteryDates.overlaps("invalid", "12/12/2026", "13/12/2026", "15/12/2026"))
        assertNull(MultipleLotteryDates.overlaps("15/12/2026", "12/12/2026", "13/12/2026", "15/12/2026"))
    }
}
