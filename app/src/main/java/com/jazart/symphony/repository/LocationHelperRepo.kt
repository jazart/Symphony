package com.jazart.symphony.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.Constants
import com.jazart.symphony.model.Song
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.Post

import java.util.ArrayList


import com.jazart.symphony.Constants.POSTS
import com.jazart.symphony.Constants.SONGS
import com.jazart.symphony.Constants.USERS

/**
 * singleton class that serves as a hub for querying and propagating localized data to our other fragment classes
 * to render them in the ui
 * Here we make several calls to find users in the same city and expose their posts and data to other users
 */

class LocationHelperRepo private constructor(uId: String) {
    private var mUser: User? = null
    private val mReference: DocumentReference
    private val mNearbyUsers: MutableLiveData<List<User>>
    val nearbyPosts: LiveData<List<Post>>
    val nearbySongs: LiveData<List<Song>>
    private val db = FirebaseFirestore.getInstance()

//    private val userLocation: String
//        get() {
//            if (mUser!!.location != null) {
//                val geoPoint = mUser!!.location
//                val latitude = geoPoint.latitude.toString()
//                val longitude = geoPoint.longitude.toString()
//                return "$latitude,$longitude"
//            }
//            return "Unknown"
//        }

    init {
        mReference = db.collection(USERS).document(uId)
        mNearbyUsers = MutableLiveData()
        getUserById()

        nearbySongs = Transformations.switchMap(mNearbyUsers) { this.findNearbySongs(it) }

        nearbyPosts = Transformations.switchMap(mNearbyUsers) { this.findNearbyPosts(it) }
    }


    companion object {
        @JvmStatic
        val instance: LocationHelperRepo by lazy {
            LocationHelperRepo(FirebaseRepo.firebaseRepoInstance.currentUser!!.uid)
        }
    }

    fun update() {
        findNearbyUsers()
    }

    private fun getUserById() {
        mReference.get().addOnCompleteListener { task ->
            mUser = task.result?.toObject(User::class.java)
            findNearbyUsers()
        }
    }

    private fun findNearbyUsers() {
        val query = db.collection(USERS).whereEqualTo("city", mUser!!.city)
                .orderBy(Constants.NAME)
                .limit(50)
        query.get().addOnCompleteListener query@{ task ->
            // No changes to the nearby user pool so no need to update the LiveData
            if (task.result?.documentChanges?.size == 0) return@query
            mNearbyUsers.setValue(task.result?.toObjects(User::class.java))

        }
    }


    private fun findNearbyPosts(nearbyUsers: List<User>): LiveData<List<Post>> {
        val reference = db.collection(POSTS)
        val posts = ArrayList<Post>()
        val postLiveData = MutableLiveData<List<Post>>()
        nearbyUsers.forEach { user ->
            reference.whereEqualTo("author", user.id)
                    .limit(10)
                    .get().addOnCompleteListener { task ->
                        if (task.result?.isEmpty!!) {
                            return@addOnCompleteListener
                        }
                        task.result?.let { result ->
                            for (j in 0 until (result.documents.size)) {
                                val post = result.toObjects(Post::class.java)[j]
                                post?.id = result.documents[j].id
                                posts.add(post)
                            }
                        }
                    }
        }
        postLiveData.value = posts
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
                        for (j in 0 until task.documents.size) {
                            val song = task.toObjects(Song::class.java)[j]
                            song.id = task.documents[j].id
                            songs.add(song)
                        }
                    }
        }
        songsLiveData.value = songs
        return songsLiveData
    }

//    private fun determineIfUpdateNecessary(): Boolean {
//        return mUser!!.city == userLocation
//    }

    private inline fun <reified T> getQueryData(vararg params: String): LiveData<T> {
        val liveData = MutableLiveData<List<T>>()
        if (params.isEmpty()) return MutableLiveData()
        val ref = db.collection(params[0])
        mNearbyUsers.value
                ?.forEach { user ->
                    ref.whereEqualTo("author", user.id)
                            .limit(10)
                            .get()
                            .addOnSuccessListener { task ->
                                liveData.value = task.toObjects(T::class.java)
                            }

                }
        return MutableLiveData()
    }

}
