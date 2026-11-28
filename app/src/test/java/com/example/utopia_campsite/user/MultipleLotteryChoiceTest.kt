package com.example.utopia_campsite.user

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MultipleLotteryChoiceTest {
    private val period = "01/12/2026-10/12/2026"

    @Test fun allowsEntireBookingPeriodAndCapacity() {
        assertNull(validateMultipleLotteryChoice("01/12/2026", "10/12/2026", period, 4, 4))
    }

    @Test fun rejectsDatesOutsideBookingPeriodOrReversed() {
        assertNotNull(validateMultipleLotteryChoice("30/11/2026", "02/12/2026", period, 1, 4))
        assertNotNull(validateMultipleLotteryChoice("09/12/2026", "11/12/2026", period, 1, 4))
        assertNotNull(validateMultipleLotteryChoice("03/12/2026", "02/12/2026", period, 1, 4))
        assertNotNull(validateMultipleLotteryChoice("01/12/2026", "02/12/2026", "invalid", 1, 4))
    }

    @Test fun rejectsInvalidGuestCount() {
        assertNotNull(validateMultipleLotteryChoice("01/12/2026", "02/12/2026", period, 0, 4))
        assertNotNull(validateMultipleLotteryChoice("01/12/2026", "02/12/2026", period, 5, 4))
    }
}
