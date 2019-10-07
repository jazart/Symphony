package com.jazart.symphony.repository.songs

import android.net.Uri
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import repo.SongRepository
import com.jazart.symphony.common.Constants.*
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import dagger.Reusable
import entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.util.*
import javax.inject.Inject

@Reusable
class FirebaseOnlineSongDataSource constructor(source: Source, private val storage: FirebaseStorage) : AbstractFirebaseDataSource(source), SongRepository {
    @Inject
    constructor(storage: FirebaseStorage) : this(Source.SERVER, storage)

    override suspend fun findSongById(id: String): Song? {
        return db.collection(SONGS).document(id).get(source).await().toObject(Song::class.java)
    }

    override suspend fun findSongsByUserId(id: String): List<Song> {
        return db.collection(SONGS).whereEqualTo(AUTHOR, id).get(source).await().toObjects(Song::class.java)
    }

    override suspend fun addSong(song: Song, uri: URI) {
        withContext(Dispatchers.IO) {
            val result = storage.getReference(StringJoiner("/").add(USERS).add(song.author).add(SONGS).add(song.name).toString())
                        .putFile(uri.toUri(), StorageMetadata.Builder().apply {
                            setContentType("audio/mp3")
                        }.build())
                        .await()
            if (result.error == null) {
                db.collection(SONGS).add(song)
            }
            return@withContext
        }
    }

}

fun URI.toUri(): Uri = Uri.parse(this.toString())