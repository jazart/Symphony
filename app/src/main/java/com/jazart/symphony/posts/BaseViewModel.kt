package com.jazart.symphony.posts

import android.arch.lifecycle.ViewModel
import com.jazart.symphony.location.FirebaseRepo
import com.jazart.symphony.location.LocationHelperRepo

open class BaseViewModel
    : ViewModel() {

    val locationRepo: LocationHelperRepo? by lazy {
        LocationHelperRepo.getInstance()
    }
    val firebaseRepo: FirebaseRepo by lazy {
        FirebaseRepo().firebaseRepoInstance
    }

    fun addUserToDb() {}

    fun removeUserFromDb() {}

    fun upateUserProfile() {}

    fun getNearbyUsers() {}

    fun refreshContent() {
        locationRepo?.update()
    }
}
