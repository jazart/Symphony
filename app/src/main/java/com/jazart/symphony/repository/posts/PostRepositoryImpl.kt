package com.jazart.symphony.repository.posts

import android.net.ConnectivityManager
import com.jazart.symphony.repository.InMemoryDataSource
import com.jazart.symphony.repository.users.FetchStrategy
import dagger.Reusable
import entities.Post
import repo.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class PostRepositoryImpl @Inject constructor(connection: ConnectivityManager,
                                             @Singleton memory: InMemoryDataSource<Post>,
                                             private val disk: FirebaseOfflinePostDataSource,
                                             private val network: FirebaseOnlinePostDataSource,
                                             fetchStrategy: FetchStrategy = FetchStrategy.NETWORK_FIRST) : AbstractRepository<Post>(
        connection, memory), PostRepository {

    override suspend fun loadPostById(id: String): Post? {
        return super.load(id, { disk.loadPostById(id) }, { network.loadPostById(id) })
    }

    override suspend fun loadPostsByUserId(id: String): List<Post> {
        return super.loadMany(id, { disk.loadPostsByUserId(id) }, { network.loadPostsByUserId(id) })
    }

    override suspend fun deletePost(id: String) {
        super.delete(id)
    }

}

abstract class AbstractRepository<T>(private val connection: ConnectivityManager,
                                     private val memory: InMemoryDataSource<T>) {

    protected suspend fun load(id: String, loadFromDisk: suspend () -> T?, loadFromNetwork: suspend () -> T?): T? {
        if (!isNetworkSuitable()) {
            val resource = loadFromDisk()
            if (resource != null) {
                memory.put(id, resource)
                return resource
            } else {
                return memory.get(id)
            }
        } else {
            val resource = loadFromNetwork()
            memory.put(id, resource)
            return resource
        }
    }

    protected suspend fun loadMany(id: String, loadFromDisk: suspend () -> List<T>, loadFromNetwork: suspend () -> List<T>): List<T> {
        // Flag for dev purposes
        var bool = true
        if (bool) {
            val resource = memory.getList(id)
            if (resource.isNotEmpty()) {
                return resource
            } else {
                return memory.putList(id, loadFromDisk())
            }
        } else {
            val resource = loadFromNetwork()
            memory.putList(id, resource)
            return resource
        }
    }

    protected fun delete(id: String) {
        memory.remove(id)
    }

    private fun isNetworkSuitable(): Boolean {
        return connection.isDefaultNetworkActive or !connection.isActiveNetworkMetered
    }
}
