package com.jazart.symphony

import androidx.lifecycle.ViewModel
import com.jazart.symphony.repository.FirebaseRepo
import com.jazart.symphony.repository.LocationHelperRepo

open class BaseViewModel
    : ViewModel() {

    val locationRepo: LocationHelperRepo? by lazy {
        LocationHelperRepo.getInstance()
    }
    val firebaseRepo: FirebaseRepo by lazy {
        FirebaseRepo.firebaseRepoInstance
    }

    fun addUserToDb() {}

    fun removeUserFromDb() {}

    fun updateUserProfile() {}

    fun getNearbyUsers() {}

    fun refreshContent() {
        locationRepo?.update()
    }
}
