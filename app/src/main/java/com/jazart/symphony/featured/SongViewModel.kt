package com.jazart.symphony.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import repo.SongRepository
import com.jazart.symphony.common.BaseViewModel
import com.jazart.symphony.common.Event
import com.jazart.symphony.common.Status
import com.jazart.symphony.repository.LocationHelperRepo
import entities.Song
import kotlinx.coroutines.*
import java.net.URI
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by kendrickgholston on 4/27/18.
 * Used for helping the user
 */

class SongViewModel @Inject constructor(private val songRepo: SongRepository, private val loadLocalSongsUseCase: LoadLocalSongsUseCase, @Named("uId") val uId: String)
    : BaseViewModel() {

    private val _playing = MutableLiveData<Boolean>()
    private val _percentLiveData = MutableLiveData<Long>()
    private val _snackbar = MutableLiveData<Event<Status>>()
    private var songSize = 1
    private val _songs = MutableLiveData<List<Song>>()

    var songs: LiveData<List<Song>> = _songs
    var userSongs: LiveData<List<Song>> = LocationHelperRepo.instance.userSongs
    val playing get() = _playing
    val snackbar: LiveData<Event<Status>> = _snackbar.toSingleEvent()
    val percentageLiveData: LiveData<Int> = Transformations.map(_percentLiveData) { progress ->
        100.times(progress.toInt()).div(songSize)
    }

    fun addSong(song: Song) {
        viewModelScope.launch {
            song.author = uId
            songRepo.addSong(song, URI(song.uri))
        }
    }

    fun removeSongFromStorage(song: Song): Boolean {
        if (firebaseRepo.remove(song)) {
            viewModelScope.launch {
                locationRepo.update()
            }
            return true
        }
        viewModelScope.launch {
            _snackbar.postValue(Event(Status.Failure))
        }
        return false
    }

    fun loadUserSongs() {
        viewModelScope.launch {
            val result = loadLocalSongsUseCase.execute()
            when (result.status) {
                is Status.Success -> _songs.value = result.data
            }
        }
    }

    fun refreshUserSongs() {
        viewModelScope.launch {
            locationRepo.loadUserData()
        }
    }
}