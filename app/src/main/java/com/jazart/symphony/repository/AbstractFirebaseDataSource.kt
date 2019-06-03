package com.jazart.symphony.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.google.protobuf.BoolValueOrBuilder

abstract class AbstractFirebaseDataSource(val source: Source) {

    protected val db: FirebaseFirestore
        @Synchronized
        get() {
            return FirebaseFirestore.getInstance()
        }

    @Synchronized
    protected fun configureCache() {
        val dbSettings = FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
        if(hasSettingsBeenApplied(dbSettings)) return
        db.firestoreSettings = dbSettings
    }

    protected suspend fun enableNetwork() {
        db.enableNetwork().await()
    }

    protected suspend fun disableNetwork() {
        db.disableNetwork().await()
    }

    private fun hasSettingsBeenApplied(firestoreSettings: FirebaseFirestoreSettings): Boolean =
            firestoreSettings == db.firestoreSettings

}