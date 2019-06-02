package com.jazart.symphony.repository.songs

import com.google.firebase.firestore.Source
import com.jazart.data.repo.SongRepository
import com.jazart.symphony.common.Constants.AUTHOR
import com.jazart.symphony.common.Constants.SONGS
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import entities.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseOfflineSongDataSource @Inject constructor(source: Source) : AbstractFirebaseDataSource(source), SongRepository {
    constructor() : this(Source.CACHE)

    override suspend fun findSongById(id: String): Song? {
        return db.collection(SONGS).document(id).get(source).await().toObject(Song::class.java)
    }

    override suspend fun findSongsByUserId(id: String): List<Song> {
        return db.collection(SONGS).whereEqualTo(AUTHOR, id).get(source).await().toObjects(Song::class.java)
    }
}