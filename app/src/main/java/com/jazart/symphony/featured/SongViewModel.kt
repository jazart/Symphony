package com.jazart.symphony.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.jazart.symphony.BaseViewModel
import com.jazart.symphony.Error
import com.jazart.symphony.Result
import com.jazart.symphony.di.App
import com.jazart.symphony.model.Song
import com.jazart.symphony.repository.LocationHelperRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.FileInputStream
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Created by kendrickgholston on 4/27/18.
 * Used for helping the user
 */

class SongViewModel @Inject constructor(val app: App) : BaseViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val job = Job()
    private val _playing = MutableLiveData<Boolean>()
    private val _percentLiveData = MutableLiveData<Long>()
    private val _snackbar = MutableLiveData<Result>()
    private var songSize = 1

    var songs: LiveData<List<Song>> = LocationHelperRepo.getInstance().nearbySongs
    val playing get() = _playing
    val snackbar: LiveData<Result> = Transformations.map(_snackbar) {it}
    val percentageLiveData: LiveData<Int> = Transformations.map(_percentLiveData) { progress  ->
            100.times(progress.toInt()).div(songSize)

    }

    fun addSongToStorage(song: Song, songStream: FileInputStream, size: String) {
        firebaseRepo
                .addSongToStorage(song, songStream)
                .observeOn(Schedulers.io())
                .doOnNext{ bytesUploaded ->
                    _percentLiveData.postValue(bytesUploaded)
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe()
        songSize = size.toInt()
    }

    fun removeSongFromStorage(song: Song) {
        if(firebaseRepo.remove(song)) {
            locationRepo.update()
            return
        }
        launch {
            _snackbar.postValue(Result.Failure(message = Error.ILLEGAL_ACCESS))
        }
    }

    override fun onCleared() {
        app.player.release()
        coroutineContext.cancel()
        super.onCleared()
    }
}