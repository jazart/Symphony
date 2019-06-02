package com.jazart.symphony.repository.users

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.jazart.data.repo.UserRepository
import com.jazart.symphony.common.Constants
import com.jazart.symphony.repository.await
import entities.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseOfflineUserDataSource @Inject constructor(): UserRepository {

    private val db: FirebaseFirestore
        @Synchronized
        get() {
            return FirebaseFirestore.getInstance()
        }

    override suspend fun getUserById(id: String): User? {
        return db.collection(Constants.USERS).document(id).get(Source.CACHE).await().toObject(User::class.java)
    }

    override suspend fun getUserFriends(id: String): List<User> {
        val user = getUserById(id) ?: return emptyList()
        return user.friends.mapNotNull { friendId -> getUserById(friendId) }
    }

}

