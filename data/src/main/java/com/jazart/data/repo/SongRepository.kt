package com.jazart.data.repo

import entities.Song

interface SongRepository {

    suspend fun findSongById(id: String): Song?
    suspend fun findSongsByUserId(id: String): List<Song>
}