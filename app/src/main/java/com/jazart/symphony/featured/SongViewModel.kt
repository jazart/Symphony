package com.jazart.symphony.featured

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.jazart.symphony.Constants.SONGS
import com.jazart.symphony.Constants.USERS
import com.jazart.symphony.MainActivity.sDb
import com.jazart.symphony.di.App
import com.jazart.symphony.location.LocationHelperRepo
import com.jazart.symphony.model.Song
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * Created by kendrickgholston on 4/27/18.
 * Used for helping the user
 */

class SongViewModel @Inject constructor(app: App) : ViewModel() {
    private val mAuth: FirebaseAuth
    private val mUser: FirebaseUser?
    var songs: LiveData<List<Song>>? = null
        private set
    @SuppressLint("StaticFieldLeak")
    private val appContext: Context = app.applicationContext

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
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
        songs = LocationHelperRepo.getInstance().nearbySongs
    }

    fun update() {
        songs = LocationHelperRepo.getInstance().nearbySongs
    }

    fun addSongToStorage(song: Song) {

        val songPath = Uri.parse(song.uri)

        try {
            val songResolver = appContext.contentResolver

            val songStream = songResolver.openInputStream(songPath)
            songResolver.getType(songPath)

            val store = FirebaseStorage.getInstance().reference

            val songRef = store.child(USERS +
                    "/" +
                    mUser!!.uid +
                    "/" +
                    SONGS +
                    "/" +
                    song.name)
            val songTask = songRef.putStream(songStream!!)


            songTask.addOnCompleteListener {
                Toast.makeText(appContext, "Successful Upload", Toast.LENGTH_SHORT).show()
                songRef.downloadUrl.addOnSuccessListener { downloadUrl -> addSongToDb(downloadUrl, song) }
            }
            songTask.addOnFailureListener {
                //      Toast.makeText(getApplicationContext(), "Unsuccessful Upload", Toast.LENGTH_SHORT).show();
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun addSongToDb(downloadUrl: Uri, song: Song) {
        val link = downloadUrl.toString()
        song.uri = link
        song.author = mUser!!.uid
        sDb.collection(SONGS).add(song)
    }

}
