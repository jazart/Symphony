package com.jazart.symphony.repository.songs

import android.net.ConnectivityManager
import repo.SongRepository
import com.jazart.symphony.repository.InMemoryDataSource
import com.jazart.symphony.repository.posts.AbstractRepository
import com.jazart.symphony.repository.users.FetchStrategy
import dagger.Reusable
import entities.Song
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class SongRepositoryImpl @Inject constructor(connection: ConnectivityManager,
                                             @Singleton val memory: InMemoryDataSource<Song>,
                                             private val disk: FirebaseOfflineSongDataSource,
                                             private val network: FirebaseOnlineSongDataSource,
                                             fetchStrategy: FetchStrategy = FetchStrategy.NETWORK_FIRST) : AbstractRepository<Song>(connection, memory), SongRepository {

    override suspend fun findSongById(id: String): Song? {
        return super.load(id, { disk.findSongById(id) }) { network.findSongById(id) }
    }

    override suspend fun findSongsByUserId(id: String): List<Song> {
        return super.loadMany(id, { disk.findSongsByUserId(id) }) { network.findSongsByUserId(id) }
    }

    @Synchronized
    override suspend fun addSong(song: Song, uri: URI) {
        with(memory) {
            val songs = getList(song.id)
            songs.toMutableList().add(song)
            putList(song.author, songs)
        }
        network.addSong(song, uri)
    }

}
