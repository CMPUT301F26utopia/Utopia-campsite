package com.example.utopia_campsite.user

import java.util.UUID

/**
*Stores details about a reservation
* @property reservationId: unique id generated when a reservation object is created
* @property campsiteId: the id of the campsite the user is reserved to
* @property startReservedDate: the start date of the reservation in form dd/mm/yyyy
* @property endReservedDate: the end date of the reservation in form dd/mm/yyyy
* @property numOfGuests: number of people reserved
*/
data class Reservation(
    val reservationId : String = UUID.randomUUID().toString(),
    val campsiteId : String = "",
    val startReservedDate: String = "",
    val endReservedDate: String = "",
    val numOfGuests: Int = 0,
    val appPolicyRead: Boolean = false
)

/**
 * Builds a CSV file's text from a list of reservations, one reservation per line
 * Fields with commas, quotes or new lines are wrapped in quotes so the CSV stays valid
 * @param reservations: the reservations to write
 * @param campsiteNames: campsite id to campsite name, the id is written if the name is missing
 * @return the CSV text, starting with a header line
 */
fun reservationsToCsv(reservations: List<Reservation>, campsiteNames: Map<String, String>): String {
    fun escape(field: String) =
        if (field.any { it == ',' || it == '"' || it == '\n' }) "\"${field.replace("\"", "\"\"")}\"" else field

    val lines = listOf("Campsite,Start Date,End Date,Guests") + reservations.map { r ->
        listOf(campsiteNames[r.campsiteId] ?: r.campsiteId, r.startReservedDate, r.endReservedDate, r.numOfGuests.toString())
            .joinToString(",") { escape(it) }
    }
    return lines.joinToString("\n") + "\n"
} //end reservationsToCsv