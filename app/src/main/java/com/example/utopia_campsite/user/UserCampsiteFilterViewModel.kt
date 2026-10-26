package com.example.utopia_campsite.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.Campsites
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Manages campsite filtering and stores the filtered results
 * Validates user inputs and compares them against available campsite data
 * Maintains the filtered campsite list and validation error state for the UI
 */
class UserCampsiteFilterViewModel : ViewModel() {
    private val availableCampsitesList = ArrayList<Campsites>() // This will be retrieved from firebase
    private val campsiteRepository = CampsiteRepository()
    var filteredCampsitesList by mutableStateOf<List<Campsites>>(emptyList())
        private set

    var filterError by mutableStateOf(false)
        private set
    var campsiteLoadStatus by mutableStateOf("loading")
        private set
    var allCampsitesList by mutableStateOf<List<Campsites>>(emptyList())
        private set

    init {
        campsiteRepository.getCampsites(
            onSuccess = { campsites ->
                updateAvailableCampsites(campsites)
                campsiteLoadStatus = "success"
            },
            onFailure = {
                campsiteLoadStatus = "error"

            }

        )
    }

    /**
     * Converts a date string into a Date object using the DD/MM/YYYY format
     * Returns null if the provided string cannot be converted into a valid date
     *
     * @param date the date string to be converted
     * @return the converted Date object, or null if the date is invalid
     */
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

    /**
     * Filters the available campsites based on the user's input criteria
     * Checks location, number of guests, and requested start and end dates
     * Empty input fields are ignored, allowing all options for those criteria
     * Sets filterError to true if any provided input is invalid
     *
     * @param userLocation: the location entered by the user
     * @param userGuests: the number of guests entered by the user
     * @param userStartDate: the requested start date in DD/MM/YYYY format
     * @param userEndDate: the requested end date in DD/MM/YYYY format
     * @return a list of campsites matching the provided filters, or an empty list if no matches are found or the input is invalid
     */
    fun filterCampsites(userLocation: String, userGuests: String, userStartDate: String, userEndDate: String): List<Campsites> {
        filterError = false
        if (campsiteLoadStatus != "success") { //this prevents the program for trying to filter campsites before the list has been loaded
            return emptyList()
        }

        val guests = userGuests.toIntOrNull()

        val startDate = if (userStartDate.isNotBlank()) {
            convertStringDate(userStartDate)
        } else null

        val endDate = if (userEndDate.isNotBlank()) {
            convertStringDate(userEndDate)
        } else null

        // Reject invalid inputs, but allow empty fields
        if (
            (userGuests.isNotBlank() && (guests == null || guests <= 0)) ||
            (userStartDate.isNotBlank() && startDate == null) ||
            (userEndDate.isNotBlank() && endDate == null) ||
            (startDate != null && endDate != null && startDate.after(endDate))
        ) {
            filterError = true
            return emptyList()
        }
        return availableCampsitesList.filter { campsite ->

            val matchesLocation = userLocation.isBlank() ||
                    campsite.address.contains(userLocation, ignoreCase = true)

            val matchesNumGuests = guests == null || campsite.maximumOccupants >= guests

            // Guaranteed to be true
            val availableStart = convertStringDate(campsite.availableStartDate)
            val availableEnd = convertStringDate(campsite.availableEndDate)

            val matchesStartDate = startDate == null || (availableStart != null && !startDate.before(availableStart))

            val matchesEndDate = endDate == null || (availableEnd != null && !endDate.after(availableEnd))

            matchesLocation && matchesNumGuests && matchesStartDate && matchesEndDate
        }
    }
    /**
     * Updates the stored list of filtered campsites
     * The updated list is observed by the results filter screen to display matching campsites
     *
     * @param campsites: the list of filtered campsites to store
     */
    fun updateFilteredCampsites(campsites: List<Campsites>) {
        filteredCampsitesList = campsites
    }

    /**
     * Updates the list of available campsites used for filtering
     * Clears the existing list and replaces it with the provided campsite data
     *
     * @param campsites: the list of available campsites to store
     */
    fun updateAvailableCampsites(campsites: List<Campsites>) {
        availableCampsitesList.clear()
        availableCampsitesList.addAll(campsites)
        allCampsitesList = campsites
    }

}