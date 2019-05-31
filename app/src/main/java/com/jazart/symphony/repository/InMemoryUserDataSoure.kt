package com.jazart.symphony.repository

import com.jazart.symphony.model.User

class InMemoryUserDataSource : UserRepository {

    private val inMemoryCache = mutableMapOf<String, User>()
    private val inMemoryFriendCache = mutableMapOf<String, List<User>>()

    override suspend fun getUserById(id: String): User? {
        return inMemoryCache[id]
    }

    override suspend fun getUserFriends(id: String): List<User> {
        return inMemoryFriendCache[id] ?: emptyList()
    }

    fun isUserMapEmpty() = inMemoryCache.isEmpty()
    fun isFriendMapEmpty() = inMemoryFriendCache.isEmpty()
    fun isUserMapFull() = inMemoryCache.size == MAX_CAPACITY
    fun isFrienMapFull() = inMemoryFriendCache.size == MAX_CAPACITY

    private fun flush() {
        inMemoryCache.clear()
        inMemoryFriendCache.clear()
    }

    fun putInFriendCache(id: String, friends: List<User>) {
        if(inMemoryFriendCache.size != MAX_CAPACITY) inMemoryFriendCache[id] = friends
    }

    fun putInUserCache(user: User?) {
        if(user == null) return
        inMemoryCache[user.id] = user
    }

    companion object {
        private const val MAX_CAPACITY = 100
    }
}