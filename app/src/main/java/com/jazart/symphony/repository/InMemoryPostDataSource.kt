package com.jazart.symphony.repository

import com.jazart.symphony.posts.Post

class InMemoryPostDataSource: PostRepository{
    private val inMemoryCache = mutableMapOf<String, Post>()

    override suspend fun loadPostById(id: String): Post? {
        return inMemoryCache[id]
    }
    fun isPostMapEmpty() = inMemoryCache.isEmpty()
    fun isPostMapFull() = inMemoryCache.size == InMemoryPostDataSource.MAX_CAPACITY

    private fun flush() {
        inMemoryCache.clear()

    }

    fun putInPostCache(id: String, post: Post?) {
        if(inMemoryCache.size != MAX_CAPACITY && post != null) inMemoryCache[id] = post
    }
    companion object {
        private const val MAX_CAPACITY = 100
    }
}