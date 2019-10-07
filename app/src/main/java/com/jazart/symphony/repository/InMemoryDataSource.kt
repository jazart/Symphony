package com.jazart.symphony.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton class InMemoryDataSource<T> @Inject constructor()  {
    private val inMemoryCache = mutableMapOf<String, T>()
    private val inMemoryList = mutableMapOf<String, List<T>>()

    fun isPostMapEmpty() = inMemoryCache.isEmpty()
    fun isPostMapFull() = inMemoryCache.size == MAX_CAPACITY

    private fun flush() {
        inMemoryCache.clear()
        inMemoryList.clear()
    }

    fun put(id: String, post: T?) {
        if (inMemoryCache.size != MAX_CAPACITY && post != null) inMemoryCache[id] = post
    }

    fun get(id: String) = inMemoryCache[id]

    fun putList(id: String, post: List<T>): List<T> {
        if (inMemoryCache.size != MAX_CAPACITY && post.isNotEmpty()) inMemoryList[id] = post
        return post
    }

    fun getList(id: String) = inMemoryList[id] ?: emptyList()

    fun remove(id: String) {
        inMemoryCache.remove(id)
    }

    companion object {
        private const val MAX_CAPACITY = 100
    }
}