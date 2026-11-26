package com.example.utopia_campsite.user

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests reservationsToCsv, used to download reservations and cancellations (US 01.05.02, 01.07.02)
 */
class ReservationCsvTest {

    private val header = "Campsite,Start Date,End Date,Guests\n"

    @Test
    fun testEmptyListIsHeaderOnly() {
        assertEquals(header, reservationsToCsv(emptyList(), emptyMap()))
    }//end testEmptyListIsHeaderOnly

    @Test
    fun testRowUsesCampsiteName() {
        val reservation = Reservation(campsiteId = "camp_001", startReservedDate = "01/12/2026", endReservedDate = "05/12/2026", numOfGuests = 3)
        assertEquals(
            header + "Banff,01/12/2026,05/12/2026,3\n",
            reservationsToCsv(listOf(reservation), mapOf("camp_001" to "Banff"))
        )
    }//end testRowUsesCampsiteName

    @Test
    fun testMissingNameFallsBackToId() {
        val reservation = Reservation(campsiteId = "camp_001", numOfGuests = 1)
        assertEquals(header + "camp_001,,,1\n", reservationsToCsv(listOf(reservation), emptyMap()))
    }//end testMissingNameFallsBackToId

    @Test
    fun testCommasAndQuotesAreEscaped() {
        val reservation = Reservation(campsiteId = "camp_001", numOfGuests = 2)
        assertEquals(
            header + "\"Banff, \"\"Tunnel\"\" Mountain\",,,2\n",
            reservationsToCsv(listOf(reservation), mapOf("camp_001" to "Banff, \"Tunnel\" Mountain"))
        )
    }//end testCommasAndQuotesAreEscaped
}
