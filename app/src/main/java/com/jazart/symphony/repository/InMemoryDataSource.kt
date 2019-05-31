package com.jazart.symphony.repository

class InMemoryDataSource<T> {
    private val inMemoryCache = mutableMapOf<String, T>()
    private val inMemoryList = mutableMapOf<String, List<T>>()

    fun isPostMapEmpty() = inMemoryCache.isEmpty()
    fun isPostMapFull() = inMemoryCache.size == MAX_CAPACITY

    private fun flush() {
        inMemoryCache.clear()

    }

    fun put(id: String, post: T?) {
        if (inMemoryCache.size != MAX_CAPACITY && post != null) inMemoryCache[id] = post
    }

    fun get(id: String) = inMemoryCache[id]

    fun putList(id: String, post: List<T>) {
        if (inMemoryCache.size != MAX_CAPACITY && post.isEmpty()) inMemoryList[id] = post
    }

    fun getList(id: String) = inMemoryList[id]

    companion object {
        private const val MAX_CAPACITY = 100
    }
}