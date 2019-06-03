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
class FirebaseOnlineUserDataSource constructor(source: Source) : AbstractFirebaseDataSource(source), UserRepository {
    @Inject
    constructor() : this(Source.SERVER)

    override suspend fun getUserById(id: String): User? {
        return db.collection(Constants.USERS).document(id).get(source).await().toObject(User::class.java)
    }

    override suspend fun getUserFriends(id: String): List<User> {
        return getUserById(id)?.friends?.mapNotNull { friendId -> getUserById(friendId) }
                ?: emptyList()
    }

}
