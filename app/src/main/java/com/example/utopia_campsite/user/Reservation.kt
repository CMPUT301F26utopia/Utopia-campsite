package com.example.utopia_campsite.user

data class Reservation(
    val campsiteName : String,
    val photoUrl : String?,
    val startReservedDate: String,
    val endReservedDate: String,
    val numOfGuests: Int
)