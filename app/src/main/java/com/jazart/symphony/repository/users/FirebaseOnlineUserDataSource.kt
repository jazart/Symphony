package com.jazart.symphony.repository.users

import com.google.firebase.firestore.Source
import com.jazart.symphony.common.Constants.LOCATION
import com.jazart.symphony.common.Constants.USERS
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import dagger.Reusable
import entities.User
import repo.UserRepository
import javax.inject.Inject

@Reusable
class FirebaseOnlineUserDataSource constructor(source: Source) : AbstractFirebaseDataSource(source), UserRepository {
    @Inject
    constructor() : this(Source.SERVER)

    override suspend fun findUserById(id: String): User? {
        return db.collection(USERS).document(id).get(source).await().toObject(User::class.java)
    }

    override suspend fun getUserFriends(id: String): List<User> {
        return findUserById(id)?.friends?.mapNotNull { friendId -> findUserById(friendId) }
                ?: emptyList()
    }

    override suspend fun getNearbyUsers(location: String): List<User> {
        return db.collection(USERS).whereEqualTo(LOCATION, location).get().await().toObjects(User::class.java)
    }

}
