package com.jazart.symphony.repository.users

import android.net.ConnectivityManager
import com.jazart.symphony.repository.posts.AbstractRepository
import com.jazart.symphony.repository.InMemoryDataSource
import entities.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(connection: ConnectivityManager,
                                             memory: InMemoryDataSource<User>,
                                             private val disk: FirebaseOfflineUserDataSource,
                                             private val network: FirebaseOnlineUserDataSource,
                                             fetchStrategy: FetchStrategy = FetchStrategy.NETWORK_FIRST) :
        AbstractRepository<User>(connection, memory), com.jazart.data.repo.UserRepository {

    override suspend fun getUserById(id: String): User? {
        return super.load(id, { disk.getUserById(id) }, { network.getUserById(id) })
    }

    override suspend fun getUserFriends(id: String): List<User> {
        return super.loadMany(id, { disk.getUserFriends(id) }, { network.getUserFriends(id) })
    }

}

enum class FetchStrategy {
    NETWORK_FIRST, DISK_FIRST, MEMORY_FIRST, NETWORK_ONLY, DEVICE_ONLY
}