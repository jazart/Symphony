package com.jazart.data.repo

import entities.Song

interface SongRepository {

    fun findSongById(id: String): Song?
    fun findSongsByUserId(id: String): List<Song>
}