package com.jazart.symphony.repository

import android.net.ConnectivityManager
import com.jazart.symphony.posts.Post
import javax.inject.Inject

class RepositoryImpl @Inject constructor(connection: ConnectivityManager,
                                         memory: InMemoryDataSource<Post>,
                                         private val disk: FirebaseOfflinePostDataSource,
                                         private val network: FirebaseOnlinePostDataSource,
                                         fetchStrategy: FetchStrategy = FetchStrategy.NETWORK_FIRST) : AbstractRepository<Post>(
        connection, memory), PostRepository {

    override suspend fun loadPostById(id: String): Post? {
        return super.load(id, { disk.loadPostById(id) }, { network.loadPostById(id) })
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
        if (!isNetworkSuitable()) {
            val resource = loadFromDisk()
            if (resource.isEmpty()) {
                memory.putList(id, resource)
                return resource
            } else {
                return memory.getList(id) ?: return emptyList()
            }
        } else {
            val resource = loadFromNetwork()
            memory.putList(id, resource)
            return resource
        }
    }

    private fun isNetworkSuitable(): Boolean {
        return !connection.isActiveNetworkMetered || connection.isDefaultNetworkActive
    }
}
