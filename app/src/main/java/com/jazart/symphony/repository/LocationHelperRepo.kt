package com.jazart.symphony.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.Constants.*
import com.jazart.symphony.model.Song
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.Post
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * singleton class that serves as a hub for querying and propagating localized data to our other fragment classes
 * to render them in the ui
 * Here we make several calls to find users in the same city and expose their posts and data to other users
 */

class LocationHelperRepo private constructor(uId: String) {
    private var mUser: User? = null
    private val mNearbyUsers = MutableLiveData<List<User>>()
    private val _nearbyPosts = MutableLiveData<List<Post>>()
    private val _userPosts = MutableLiveData<List<Post>>()
    private val _nearbySongs = MutableLiveData<List<Song>>()
    private val _userSongs = MutableLiveData<List<Song>>()
    val nearbyPosts: LiveData<List<Post>> = _nearbyPosts
    val userPosts: LiveData<List<Post>> = _userPosts
    val nearbySongs: LiveData<List<Song>> = _nearbySongs
    val userSongs: LiveData<List<Song>> = _userSongs

    private val db = FirebaseFirestore.getInstance()
    private val mReference: DocumentReference = db.collection(USERS).document(uId)


    companion object {
        @JvmStatic
        val instance: LocationHelperRepo by lazy {
            LocationHelperRepo(FirebaseRepo.firebaseRepoInstance.currentUser!!.uid)
        }
    }

    suspend fun initBackground() {
        getUserById()
        update()
    }

    suspend fun update() {
        _nearbySongs.value = getQueryData(SONGS)
        _nearbyPosts.value = getQueryData(POSTS)
    }

    private suspend fun getUserById() {
        mUser = mReference.get().await().toObject(User::class.java)
        findNearbyUsers()
    }
    suspend fun loadUserData() {
        _userPosts.postValue(getQueryUserData(POSTS))
        _userSongs.postValue(getQueryUserData(SONGS))
    }

    private suspend fun findNearbyUsers() {
        val query = db.collection(USERS).whereEqualTo(CITY, mUser?.city)
                .orderBy(NAME)
                .limit(50)
        mNearbyUsers.value = query.get().await().toObjects(User::class.java).filter { user ->
            user.id != mUser?.id
        }
    }

    private suspend inline fun <reified T> getQueryData(vararg params: String): List<T> {
        val ref = db.collection(params[0])
        return mNearbyUsers.value?.flatMap { user ->
            ref.whereEqualTo(AUTHOR, user.id)
                    .limit(10)
                    .get()
                    .await()
                    .documents
                    .map { doc ->
                        val postId = doc.id
                        val docPojo = doc.toObject(T::class.java) ?: return listOf()
                        if (docPojo is Post) {
                            docPojo.id = postId
                        }
                        docPojo
                    }
        } ?: return listOf()
    }
    private suspend inline fun <reified T> getQueryUserData(collection :String): List<T>{
        val ref = db.collection(collection)
        return ref.whereEqualTo(AUTHOR, mUser?.id)
                .limit(10)
                .get()
                .await().toObjects(T::class.java)
    }

}

suspend fun <T> Task<T>.await(): T {
    return suspendCoroutine { cont ->
        addOnSuccessListener { data -> cont.resumeWith(Result.success(data)) }
        addOnFailureListener { result -> cont.resumeWithException(result) }
    }
}
