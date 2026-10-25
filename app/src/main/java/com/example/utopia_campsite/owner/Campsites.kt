package com.example.utopia_campsite.owner

data class Campsites(
    val location: String,
    val tags: List<String>,
    val lotteryWinners: Int,
    val lotteryWaitlist: Int?,
    val description: String,
    val registrationDate: String,   // (start date-end date) -> eg. 10/10/2026-20/10/2026
    val bookingDate: String,        // (start date-end date) -> eg. 10/10/2026-20/10/2026
    val maximumOccupants: Int,
    val availableStartDate: String, // Added on, this is the starting date of which the campsite is available
    val availableEndDate: String    // Added on, this is the ending date of which the campsite is available
)
