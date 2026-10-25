package com.example.utopia_campsite.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.utopia_campsite.owner.Campsites

class UserCampsiteFilterViewModel : ViewModel() {
    // Dedicated class to maintain the results of the filtered campsites from the filter screen to results screen of showing the filtered campsites
    var filteredCampsites by mutableStateOf<List<Campsites>>(emptyList())
        private set

    fun updateFilteredCampsites(campsites: List<Campsites>) {
        filteredCampsites = campsites
    }

}