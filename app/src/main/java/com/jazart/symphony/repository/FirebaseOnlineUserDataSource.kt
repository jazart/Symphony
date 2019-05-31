package com.jazart.symphony.repository

import com.google.firebase.firestore.Source
import com.jazart.symphony.Constants
import com.jazart.symphony.model.User
import javax.inject.Inject

class FirebaseOnlineUserDataSource @Inject constructor() : AbstractFirebaseDataSource(), UserRepository {


    override suspend fun getUserById(id: String): User? {
        return db.collection(Constants.USERS).document(id).get(Source.SERVER).await().toObject(User::class.java)
    }

    override suspend fun getUserFriends(id: String): List<User> {
        return getUserById(id)?.friends?.mapNotNull { friendId -> getUserById(friendId) }
                ?: emptyList()
    }

    override suspend fun configureDb() {
        db.enableNetwork().await()
    }
}
