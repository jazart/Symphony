package com.jazart.symphony.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jazart.symphony.BaseViewModel
import com.jazart.symphony.Result
import com.jazart.symphony.di.App
import com.jazart.symphony.model.Song
import com.jazart.symphony.repository.LocationHelperRepo
import java.io.FileInputStream
import javax.inject.Inject

/**
 * Created by kendrickgholston on 4/27/18.
 * Used for helping the user
 */

class SongViewModel @Inject constructor(app: App) : BaseViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mUser: FirebaseUser?
    var songs: LiveData<List<Song>>
    private var progressLiveData: LiveData<Result> = firebaseRepo.uploadProgress
    var percentageLiveData: LiveData<Int> = Transformations.map(progressLiveData) { progress  ->
        when(progress) {
            is Result.Success<*> ->  (100.times(progress.data as Int)).div(songSize)
            is Result.Failure -> -1
            is Result.Loading -> Integer.MAX_VALUE
        }
    }
    private var songSize = 1

    init {
        mUser = mAuth.currentUser
        songs = LocationHelperRepo.getInstance().nearbySongs
        progressLiveData = firebaseRepo.uploadProgress
    }

    fun update() {
        songs = LocationHelperRepo.getInstance().nearbySongs
    }

    fun addSongToStorage(song: Song, songStream: FileInputStream, size: String) {
        firebaseRepo.addSongToStorage(song, songStream)
        songSize = size.toInt()
    }

    fun removeSongFromStorage(song: Song) {
        firebaseRepo.remove(song)
    }
}
