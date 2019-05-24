package com.jazart.symphony

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jazart.symphony.repository.FirebaseRepo
import com.jazart.symphony.repository.LocationHelperRepo
import kotlinx.coroutines.launch

open class BaseViewModel
    : ViewModel() {

    val locationRepo: LocationHelperRepo by lazy {
        LocationHelperRepo.instance
    }
    val firebaseRepo: FirebaseRepo by lazy {
        FirebaseRepo.firebaseRepoInstance
    }

    fun addUserToDb() {}

    fun removeUserFromDb() {}

    fun updateUserProfile() {}

    fun getNearbyUsers() {}

    fun refreshContent() {
        viewModelScope.launch {
            locationRepo.update()
        }
    }

    fun load() {
        viewModelScope.launch {
            locationRepo.initBackground()
        }
    }
}
