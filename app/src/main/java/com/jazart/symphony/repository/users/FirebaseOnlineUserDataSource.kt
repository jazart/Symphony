package com.jazart.symphony.repository.users

import com.google.firebase.firestore.Source
import com.jazart.symphony.common.Constants
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import entities.User
import javax.inject.Inject

class FirebaseOnlineUserDataSource @Inject constructor() : AbstractFirebaseDataSource(), com.jazart.data.repo.UserRepository {


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
