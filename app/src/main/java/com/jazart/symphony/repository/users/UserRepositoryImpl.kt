package com.jazart.symphony.repository.users

import android.net.ConnectivityManager
import com.jazart.data.repo.UserRepository
import com.jazart.symphony.repository.InMemoryDataSource
import com.jazart.symphony.repository.posts.AbstractRepository
import dagger.Reusable
import entities.User
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class UserRepositoryImpl @Inject constructor(connection: ConnectivityManager,
                                             @Singleton memory: InMemoryDataSource<User>,
                                             private val disk: FirebaseOfflineUserDataSource,
                                             private val network: FirebaseOnlineUserDataSource,
                                             fetchStrategy: FetchStrategy = FetchStrategy.NETWORK_FIRST) :
        AbstractRepository<User>(connection, memory), UserRepository {

    override suspend fun findUserById(id: String): User? {
        return super.load(id, { disk.findUserById(id) }, { network.findUserById(id) })
    }

    override suspend fun getUserFriends(id: String): List<User> {
        return super.loadMany(id, { disk.getUserFriends(id) }, { network.getUserFriends(id) })
    }

}

enum class FetchStrategy {
    NETWORK_FIRST, DISK_FIRST, MEMORY_FIRST, NETWORK_ONLY, DEVICE_ONLY
}