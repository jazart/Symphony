package com.jazart.symphony.repository

import com.google.firebase.firestore.FirebaseFirestore

abstract class AbstractFirebaseDataSource {

    protected val db: FirebaseFirestore
        @Synchronized
        get() {
            return FirebaseFirestore.getInstance()
        }

    abstract suspend fun configureDb()
}