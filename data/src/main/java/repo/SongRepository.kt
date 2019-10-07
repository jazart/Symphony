package repo

import entities.Song
import java.net.URI

interface SongRepository {
    suspend fun findSongById(id: String): Song?
    suspend fun findSongsByUserId(id: String): List<Song>
    suspend fun addSong(song: Song, uri: URI)
}