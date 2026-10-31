/**
 * Utopia 02.01.02, 02.04.03, 02.04.04, 02.04.05
 * Checks what a campsite owner typed into the register campsite form (RegisterCampsiteScreen)
 * and turns it into a new campsite with its registration and booking periods
 *
 * Outstanding Issues:
 * The saved campsite is not linked to its owner yet (Campsites has no owner ID)
 * The lottery waitlist capacity (US 02.04.07) is not saved, Campsites has no field for it
 */

package com.example.utopia_campsite.owner

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Turns what the owner typed into a new campsite
 * Only the image is optional, so every other field must be filled in,
 * the number fields must hold valid numbers and both periods must be valid
 *
 * @param name: the campsite's name
 * @param address: the campsite's location
 * @param description: the campsite's description and amenities
 * @param maxOccupancy: the maximum number of occupants, as typed
 * @param latitude: the map location's latitude, as typed
 * @param longitude: the map location's longitude, as typed
 * @param photoUrl: the image URL, or empty for no image
 * @param tags: the tags, separated by commas
 * @param lotteryWinners: the number of lottery winners, as typed
 * @param registrationStart: the first day entrants can join the waiting list, as DD/MM/YYYY
 * @param registrationEnd: the last day entrants can join the waiting list, as DD/MM/YYYY
 * @param bookingStart: the first day winners can book, as DD/MM/YYYY
 * @param bookingEnd: the last day winners can book, as DD/MM/YYYY
 * @return the new campsite, or null if a required field is missing, not a valid number, or a period is not valid
 */
fun buildCampsite(
    name: String,
    address: String,
    description: String,
    maxOccupancy: String,
    latitude: String,
    longitude: String,
    photoUrl: String,
    tags: String,
    lotteryWinners: String,
    registrationStart: String,
    registrationEnd: String,
    bookingStart: String,
    bookingEnd: String
): Campsites?{
    // Turn the typed text into numbers
    // toIntOrNull / toDoubleOrNull give null instead of crashing when the text is not a number
    val occupancyNumber = maxOccupancy.toIntOrNull()
    val latitudeNumber = latitude.toDoubleOrNull()
    val longitudeNumber = longitude.toDoubleOrNull()
    val winnersNumber = lotteryWinners.toIntOrNull()

    // Split the tags at each comma, remove the spaces around each tag, and drop empty pieces
    // e.g. " lake, , hiking " becomes ["lake", "hiking"]
    val tagList = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    // US 02.04.03 and 02.04.04: each period becomes "start-end", or null if it is not valid
    val registrationPeriod = buildRegistrationPeriod(registrationStart, registrationEnd)
    val bookingPeriod = buildRegistrationPeriod(bookingStart, bookingEnd)

    // true if any required text field is empty (tags made of only commas count as empty)
    val textMissing = name.isBlank() || address.isBlank() || description.isBlank() || tagList.isEmpty()
    // true if any number field could not be read as a number
    val numberInvalid = occupancyNumber == null || latitudeNumber == null || longitudeNumber == null || winnersNumber == null
    // true if either period is missing a date, has a date that does not exist, or starts after it ends
    val periodInvalid = registrationPeriod == null || bookingPeriod == null
    if (textMissing || numberInvalid || periodInvalid) {
        return null
    }
    // After this check Kotlin knows the numbers and periods are not null, so they can be used directly below

    return Campsites(
        id = "", // Firestore makes the ID when the campsite is saved
        name = name,
        photoUrl = photoUrl.ifBlank { null }, // no image if left empty
        address = address,
        latitude = latitudeNumber,
        longitude = longitudeNumber,
        ratings = mutableListOf(), // a new campsite has no ratings
        tags = tagList.toMutableList(), // Campsites stores tags as a MutableList
        lotteryWinners = winnersNumber,
        lotteryWaitlist = mutableListOf(), // nobody has joined yet
        description = description,
        registrationDate = registrationPeriod,
        bookingDate = bookingPeriod,
        maximumOccupants = occupancyNumber,
        availableStartDate = "", // not part of this story
        availableEndDate = "", // not part of this story
        usersBooked = mutableListOf() // not part of this story
    )
} //end buildCampsite

/**
 * Turns two picked dates into the "start-end" period text stored in Campsites
 * Used for both the registration period and the booking period
 *
 * @param startDate: the first day of the period, as DD/MM/YYYY
 * @param endDate: the last day of the period, as DD/MM/YYYY
 * @return "start-end" (e.g. "10/10/2026-20/10/2026"), or null if a date is invalid or the start is after the end
 */
fun buildRegistrationPeriod(startDate: String, endDate: String): String?{
    val start = parseDate(startDate)
    val end = parseDate(endDate)
    if (start == null || end == null || start.after(end)) {
        return null
    }
    return "$startDate-$endDate" // same "start-end" format the Campsites class uses
} //end buildRegistrationPeriod

/**
 * Reads a DD/MM/YYYY date, the same way UserCampsiteFilterViewModel does
 *
 * @param date: the date text
 * @return the date, or null if the text is not a real date (e.g. 31/02/2026)
 */
private fun parseDate(date: String): Date?{
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formatter.isLenient = false // reject dates that don't exist instead of rolling them over
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        formatter.parse(date)
    } catch (e: ParseException) {
        null // not a valid date
    }
} //end parseDate