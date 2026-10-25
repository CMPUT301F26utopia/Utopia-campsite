package com.example.utopia_campsite.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.utopia_campsite.owner.Campsites
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class UserCampsiteFilterViewModel : ViewModel() {
    // Dedicated class to maintain the results of the filtered process for users

    private val availableCampsitesList = ArrayList<Campsites>() // This will be retrieved from firebase
    var filteredCampsitesList by mutableStateOf<List<Campsites>>(emptyList())
        private set

    fun convertStringDate(date: String): Date? {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.isLenient = false
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            formatter.parse(date)

        } catch (e: ParseException) {
            null
        }
    }

    // Method to retrieve the filtered list of campsites, given by the user's inputs
    fun filterCampsites(userLocation: String, userGuests: String, userStartDate: String, userEndDate: String): List<Campsites> {
        val guests = userGuests.toIntOrNull()

        val startDate = if (userStartDate.isNotBlank()) {
            convertStringDate(userStartDate)
        } else null

        val endDate = if (userEndDate.isNotBlank()) {
            convertStringDate(userEndDate)
        } else null

        // Reject invalid inputs, but allow empty fields
        if (userGuests.isNotBlank() && (guests == null || guests <= 0)) return emptyList()
        if (userStartDate.isNotBlank() && startDate == null) return emptyList()
        if (userEndDate.isNotBlank() && endDate == null) return emptyList()
        if (startDate != null && endDate != null && startDate.after(endDate)) return emptyList()

        return availableCampsitesList.filter { campsite ->

            val matchesLocation = userLocation.isBlank() ||
                    campsite.location.equals(userLocation, ignoreCase = true)

            val matchesNumGuests = guests == null || campsite.maximumOccupants >= guests

            // Guaranteed to be true
            val availableStart = convertStringDate(campsite.availableStartDate)
            val availableEnd = convertStringDate(campsite.availableEndDate)

            val matchesStartDate = startDate == null || (availableStart != null && !startDate.before(availableStart))

            val matchesEndDate = endDate == null || (availableEnd != null && !endDate.after(availableEnd))

            matchesLocation && matchesNumGuests && matchesStartDate && matchesEndDate
        }
    }
    fun updateFilteredCampsites(campsites: List<Campsites>) {
        filteredCampsitesList = campsites
    }

    fun updateAvailableCampsites(campsites: List<Campsites>) {
        availableCampsitesList.clear()
        availableCampsitesList.addAll(campsites)
    }

}