package com.jazart.symphony.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.jazart.symphony.Constants
import com.jazart.symphony.model.User
import javax.inject.Inject

class FirebaseOfflineUserDataSource @Inject constructor(): UserRepository {

    private val db: FirebaseFirestore
        @Synchronized
        get() {
            FirebaseFirestore.getInstance().disableNetwork()
            return FirebaseFirestore.getInstance()
        }

    init {
        configureDb()
    }

    override suspend fun getUserById(id: String): User? {
        return db.collection(Constants.USERS).document(id).get(Source.CACHE).await().toObject(User::class.java)
    }

    override suspend fun getUserFriends(id: String): List<User> {
        val user = getUserById(id) ?: return emptyList()
        return user.friends.mapNotNull { friendId -> getUserById(friendId) }
    }

    private fun configureDb() {
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
    }
}