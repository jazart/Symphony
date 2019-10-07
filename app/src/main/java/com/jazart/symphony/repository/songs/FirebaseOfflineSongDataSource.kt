package com.jazart.symphony.repository.songs

import com.google.firebase.firestore.Source
import repo.SongRepository
import com.jazart.symphony.common.Constants.AUTHOR
import com.jazart.symphony.common.Constants.SONGS
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import dagger.Reusable
import entities.Song
import java.net.URI
import javax.inject.Inject

@Reusable
class FirebaseOfflineSongDataSource constructor(source: Source) : AbstractFirebaseDataSource(source), SongRepository {
    @Inject
    constructor() : this(Source.CACHE)

    override suspend fun findSongById(id: String): Song? {
        return db.collection(SONGS).document(id).get(source).await().toObject(Song::class.java)
    }

    override suspend fun findSongsByUserId(id: String): List<Song> {
        return db.collection(SONGS).whereEqualTo(AUTHOR, id).get(source).await().toObjects(Song::class.java)
    }

    override suspend fun addSong(song: Song, uri: URI) {
        return
    }
}