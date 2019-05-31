package com.jazart.symphony.repository

import com.jazart.symphony.model.Song

interface SongRepository {

    fun findSongById(id: String): Song?
    fun findSongsByUserId(id: String): List<Song>
}