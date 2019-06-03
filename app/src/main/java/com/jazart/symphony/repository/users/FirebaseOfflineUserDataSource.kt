package com.jazart.symphony.repository.users

import com.google.firebase.firestore.Source
import com.jazart.data.repo.UserRepository
import com.jazart.symphony.common.Constants
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import dagger.Reusable
import entities.User
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class FirebaseOfflineUserDataSource constructor(source: Source) : AbstractFirebaseDataSource(source), UserRepository {
    @Inject
    constructor() : this(Source.CACHE)

    override suspend fun getUserById(id: String): User? {
        return db.collection(Constants.USERS).document(id).get(Source.CACHE).await().toObject(User::class.java)
    }

    override suspend fun getUserFriends(id: String): List<User> {
        val user = getUserById(id) ?: return emptyList()
        return user.friends.mapNotNull { friendId -> getUserById(friendId) }
    }

}

