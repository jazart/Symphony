package com.jazart.symphony.repository

import android.net.ConnectivityManager
import com.jazart.symphony.posts.Post
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(private val connection: ConnectivityManager,
                                             private val memory: InMemoryPostDataSource,
                                             private val disk: FirebaseOfflinePostDataSource,
                                             private val network: FirebaseOnlinePostDataSource,
                                             private val fetchStragety: FetchStrategy = FetchStrategy.NETWORK) : PostRepository
{
    override suspend fun loadPostById(id: String): Post? {
        if (!isNetworkSuitable()) {
            val post = disk.loadPostById(id)
            if(post != null) {
                memory.loadPostById(id)
                return post
            } else {
                return memory.loadPostById(id)
            }
        } else {
            val post = network.loadPostById(id)
            memory.putInPostCache(id,post)
            return post
        }
    }
    private fun isNetworkSuitable(): Boolean {
        return !connection.isActiveNetworkMetered || connection.isDefaultNetworkActive
    }
}
