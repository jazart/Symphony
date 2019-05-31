package com.jazart.symphony.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.jazart.symphony.*
import com.jazart.symphony.di.App
import com.jazart.symphony.model.Song
import com.jazart.symphony.repository.LocationHelperRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.io.FileInputStream
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Created by kendrickgholston on 4/27/18.
 * Used for helping the user
 */

class SongViewModel constructor(val app: App) : BaseViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val job = Job()
    private val _playing = MutableLiveData<Boolean>()
    private val _percentLiveData = MutableLiveData<Long>()
    private val _snackbar = MutableLiveData<Event<Status>>()
    private var songSize = 1

    var songs: LiveData<List<Song>> = LocationHelperRepo.instance.nearbySongs
    var userSongs: LiveData<List<Song>> = LocationHelperRepo.instance.userSongs
    val playing get() = _playing
    val snackbar: LiveData<Event<Status>> = _snackbar.toSingleEvent()
    val percentageLiveData: LiveData<Int> = Transformations.map(_percentLiveData) { progress ->
        100.times(progress.toInt()).div(songSize)

    }

    fun addSongToStorage(song: Song, songStream: FileInputStream, size: String) {
        firebaseRepo
                .addSongToStorage(song, songStream)
                .observeOn(Schedulers.io())
                .doOnNext { bytesUploaded ->
                    _percentLiveData.postValue(bytesUploaded)
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe()
        songSize = size.toInt()
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

    override fun onCleared() {
        app.player.release()
        coroutineContext.cancel()
        super.onCleared()
    }

    fun loadUserSongs() {
        viewModelScope.launch {
            locationRepo.loadUserData()
        }
    }

    fun refreshUserSongs() {
        viewModelScope.launch {
            locationRepo.loadUserData()
        }
    }
}