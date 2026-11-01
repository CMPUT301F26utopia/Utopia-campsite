package com.example.utopia_campsite.owner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests turning the register campsite form's input into a campsite (US 02.01.02, 02.04.03, 02.04.04, 02.04.05)
 */
class BuildCampsiteTest {

    // Builds a campsite from valid input; each test changes one field to check one rule
    private fun mockBuild(
        name: String = "Lakeside",
        maxOccupancy: String = "6",
        tags: String = "lake, hiking",
        registrationEnd: String = "20/10/2026",
        bookingStart: String = "01/11/2026"
    ): Campsites?{
        return buildCampsite(
            name = name,
            address = "74 St NW",
            description = "Quiet spot by the lake",
            maxOccupancy = maxOccupancy,
            latitude = "53.5",
            longitude = "-113.5",
            photoUrl = "",
            tags = tags,
            lotteryWinners = "2",
            registrationStart = "10/10/2026",
            registrationEnd = registrationEnd,
            bookingStart = bookingStart,
            bookingEnd = "10/11/2026"
        )
    } //end mockBuild

    @Test
    fun buildCampsite_validInput_returnsCampsite(){
        val campsite = mockBuild()
        assertNotNull(campsite)
        assertEquals("Lakeside", campsite!!.name)
        assertEquals(listOf("lake", "hiking"), campsite.tags) // spaces around each tag are removed
        assertEquals(2, campsite.lotteryWinners)
        assertEquals("10/10/2026-20/10/2026", campsite.registrationDate)
        assertEquals("01/11/2026-10/11/2026", campsite.bookingDate)
        assertNull(campsite.photoUrl) // the image is optional
    } //end buildCampsite_validInput_returnsCampsite

    @Test
    fun buildCampsite_missingName_returnsNull(){
        assertNull(mockBuild(name = ""))
    } //end buildCampsite_missingName_returnsNull

    @Test
    fun buildCampsite_occupancyNotANumber_returnsNull(){
        assertNull(mockBuild(maxOccupancy = "six"))
    } //end buildCampsite_occupancyNotANumber_returnsNull

    @Test
    fun buildCampsite_onlyCommasInTags_returnsNull(){
        assertNull(mockBuild(tags = " , , "))
    } //end buildCampsite_onlyCommasInTags_returnsNull

    @Test
    fun buildCampsite_registrationEndsBeforeItStarts_returnsNull(){
        assertNull(mockBuild(registrationEnd = "01/10/2026"))
    } //end buildCampsite_registrationEndsBeforeItStarts_returnsNull

    @Test
    fun buildCampsite_bookingDateDoesNotExist_returnsNull(){
        assertNull(mockBuild(bookingStart = "31/02/2026")) // February has no 31st
    } //end buildCampsite_bookingDateDoesNotExist_returnsNull

}