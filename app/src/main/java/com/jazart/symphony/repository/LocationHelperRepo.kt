package com.jazart.symphony.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.annotation.NonNull
import androidx.lifecycle.Transformations

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.jazart.symphony.Constants
import com.jazart.symphony.model.Song
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.UserPost

import java.util.ArrayList
import java.util.Objects
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

import com.jazart.symphony.Constants.POSTS
import com.jazart.symphony.Constants.SONGS
import com.jazart.symphony.Constants.USERS

/**
 * singleton class that serves as a hub for querying and propagating localized data to our other fragment classes
 * to render them in the ui
 * Here we make several calls to find users in the same city and expose their posts and data to other users
 */

class LocationHelperRepo<T> private constructor(uId: String) {
    private var mUser: User? = null
    private val mReference: DocumentReference
    private val mNearbyUsers: MutableLiveData<List<User>>
    val nearbyPosts: LiveData<List<UserPost>>
    val nearbySongs: LiveData<List<Song>>
    private val db = FirebaseFirestore.getInstance()

    private val userLocation: String
        get() {
            if (mUser!!.location != null) {
                val geoPoint = mUser!!.location
                val latitude = geoPoint.latitude.toString()
                val longitude = geoPoint.longitude.toString()
                return "$latitude,$longitude"
            }
            return "Unknown"
        }

    init {
        mReference = db.collection(USERS).document(uId)
        mNearbyUsers = MutableLiveData()
        getUserById()

        nearbySongs = Transformations.switchMap(mNearbyUsers, Function<List<User>, LiveData<List<Song>>> { this.findNearbySongs(it) }
        )

        nearbyPosts = Transformations.switchMap(mNearbyUsers, Function<List<User>, LiveData<List<UserPost>>> { this.findNearbyPosts(it) })
    }

    private fun create(): LocationHelperRepo<*> {
        if (INSTANCE == null) {
            synchronized(this) {
                INSTANCE = LocationHelperRepo(FirebaseAuth.getInstance().uid)
                INSTANCE!!.getUserById()
            }
        }

        return INSTANCE

    }

    fun update() {
        findNearbyUsers()
    }

    private fun getUserById() {
        mReference.get().addOnCompleteListener { task ->
            mUser = task.result.toObject(User::class.java)
            findNearbyUsers()
        }
    }

    private fun findNearbyUsers() {
        val q = db.collection(USERS).whereEqualTo("city", mUser!!.city)
                .addSnapshotListener { snapshots, e ->
                    val stream = Stream.of<QuerySnapshot>(snapshots)

                } as DocumentReference
        val query = db.collection(USERS).whereEqualTo("city", mUser!!.city)
                .orderBy(Constants.NAME)
                .limit(50)
        query.get().addOnCompleteListener { task ->
            // No changes to the nearby user pool so no need to update the LiveData
            val changes = task.result.getDocumentChanges()
            if (task.result.getDocumentChanges().size == 0) return@query.get().addOnCompleteListener
            mNearbyUsers.setValue(task.result.toObjects(User::class.java))

        }
    }


    private fun findNearbyPosts(nearbyUsers: List<User>): LiveData<List<UserPost>> {
        val reference = db.collection(POSTS)
        val posts = ArrayList<UserPost>()
        val postLiveData = MutableLiveData<List<UserPost>>()
        nearbyUsers.forEach { user ->
            reference.whereEqualTo("author", user.id)
                    .limit(10)
                    .get().addOnCompleteListener { task ->
                        for (j in 0 until task.result.toObjects(UserPost::class.java).size) {
                            val post = task.result.toObjects(UserPost::class.java)[j]
                            post.id = task.result.getDocuments()[j].id
                            posts.add(post)

                        }
                    }
        }
        postLiveData.setValue(posts)
        return postLiveData
    }

    private fun findNearbySongs(nearbyUsers: List<User>): LiveData<List<Song>> {
        val reference = db.collection(SONGS)
        val songsLiveData = MutableLiveData<List<Song>>()
        val songs = ArrayList<Song>()

        nearbyUsers.forEach { user ->
            reference.whereEqualTo("author", user.id).limit(10)
                    .get()
                    .addOnSuccessListener { task ->
                        IntStream.range(0, task.documents.size).forEach { j ->
                            val song = task.toObjects(Song::class.java)[j]
                            song.id = task.documents[j].id
                            songs.add(song)
                        }
                    }
        }
        songsLiveData.setValue(songs)
        return songsLiveData
    }

    private fun determineIfUpdateNecessary(): Boolean {
        return mUser!!.city == userLocation
    }

    private fun getQueryData(vararg params: String): LiveData<T> {
        if (params.size == 0) return MutableLiveData()
        val ref = db.collection(params[0])
        mNearbyUsers.value
                .forEach { user ->
                    ref.whereEqualTo("author", user.id)
                            .limit(10)
                            .get()
                            .addOnSuccessListener { task -> }

                }
        return MutableLiveData()
    }

    companion object {
        private var INSTANCE: LocationHelperRepo<*>? = null

        val instance: LocationHelperRepo<*>
            get() {
                if (INSTANCE == null)
                    INSTANCE = LocationHelperRepo(FirebaseAuth.getInstance().uid).create()
                return INSTANCE
            }
    }
}
