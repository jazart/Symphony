package com.jazart.symphony.repository

import android.net.ConnectivityManager
import com.jazart.symphony.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val connection: ConnectivityManager,
                                             private val memory: InMemoryUserDataSource,
                                             private val disk: FirebaseOfflineDataSource,
                                             private val network: FirebaseOnlineDataSource,
                                             private val fetchStragety: FetchStrategy = FetchStrategy.NETWORK) : UserRepository {

    override suspend fun getUserById(id: String): User? {
        if (!isNetworkSuitable()) {
            val user = disk.getUserById(id)
            if(user != null) {
                memory.putInUserCache(user)
                return user
            } else {
                return memory.getUserById(id)
            }
        } else {
            val user = network.getUserById(id)
            memory.putInUserCache(user)
            return user
        }
    }

    override suspend fun getUserFriends(id: String): List<User> {
        if (!isNetworkSuitable()) {
            val friends = disk.getUserFriends(id)
            if(friends.isEmpty()) {
                memory.putInFriendCache(id, friends)
                return friends
            } else {
                return memory.getUserFriends(id)
            }
        } else {
            val friends = network.getUserFriends(id)
            memory.putInFriendCache(id, friends)
            return friends
        }
    }

    private fun isNetworkSuitable(): Boolean {
        return !connection.isActiveNetworkMetered || connection.isDefaultNetworkActive
    }
}

enum class FetchStrategy {
    NETWORK, DISK, RAM
}
