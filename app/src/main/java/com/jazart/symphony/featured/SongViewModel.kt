package com.jazart.symphony.featured

import android.arch.lifecycle.LiveData
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jazart.symphony.Constants.SONGS
import com.jazart.symphony.MainActivity.sDb
import com.jazart.symphony.di.App
import com.jazart.symphony.location.LocationHelperRepo
import com.jazart.symphony.model.Song
import com.jazart.symphony.posts.BaseViewModel
import java.io.FileInputStream
import javax.inject.Inject

/**
 * Created by kendrickgholston on 4/27/18.
 * Used for helping the user
 */

class SongViewModel @Inject constructor(app: App) : BaseViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mUser: FirebaseUser?
    var songs: LiveData<List<Song>>? = null
        private set

    val userSongs: Task<List<Song>>
        get() {
            val query = sDb.collection(SONGS).whereEqualTo("author", mUser!!.uid)
            Log.d("DEBUG", query.toString())
            return query.get()
                    .continueWith { task ->
                        val snapshot = task.result
                        snapshot.toObjects(Song::class.java)
                    }
        }

    init {
        mUser = mAuth.currentUser
        songs = LocationHelperRepo.getInstance().nearbySongs
    }

    fun update() {
        songs = LocationHelperRepo.getInstance().nearbySongs
    }

    fun addSongToStorage(song: Song, songStream: FileInputStream) {
        firebaseRepo.addSongToStorage(song, songStream)
    }
}
