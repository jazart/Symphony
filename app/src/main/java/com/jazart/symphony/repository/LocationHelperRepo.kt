package com.jazart.symphony.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.Constants.*
import com.jazart.symphony.model.Song
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.Post
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * singleton class that serves as a hub for querying and propagating localized data to our other fragment classes
 * to render them in the ui
 * Here we make several calls to find users in the same city and expose their posts and data to other users
 */

class LocationHelperRepo private constructor(private val uId: String) {
    private var mUser: User? = null
    private val mNearbyUsers = MutableLiveData<List<User>>()
    var nearbyPosts: LiveData<List<Post>>? = null
    var nearbySongs: LiveData<List<Song>>? = null
    private val db = FirebaseFirestore.getInstance()
    private var mReference: DocumentReference = db.collection(USERS).document(uId)

    companion object {
        @JvmStatic
        val instance: LocationHelperRepo by lazy {
            LocationHelperRepo(FirebaseRepo.firebaseRepoInstance.currentUser!!.uid)
        }
    }

    suspend fun initBackground() {
        getUserById()
        nearbySongs = mNearbyUsers.switchMap { users ->
            liveData(Dispatchers.Default) {
                emit(findNearbySongs(users))
            }
        }
        nearbyPosts = mNearbyUsers.switchMap { users ->
            liveData(Dispatchers.Default) {
                emit(getQueryData<Post>(POSTS))
            }
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
                .orderBy(NAME)
                .limit(50)
        query.get().addOnCompleteListener query@{ task ->
            if (task.result?.documentChanges?.size == 0) return@query
            val nearbyUsers = task.result?.toObjects(User::class.java)
            mNearbyUsers.value = nearbyUsers
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

    private suspend fun findNearbySongs(nearbyUsers: List<User>): List<Song> {
        val reference = db.collection(SONGS)
        val result = mutableListOf<Song>()
        nearbyUsers.forEach { user ->
            result.addAll(
                    reference.whereEqualTo("author", user.id).limit(10)
                            .get()
                            .await()
                            .toObjects(Song::class.java)
                            .toList()
            )
        }
        return result
    }

    private suspend inline fun <reified T> getQueryData(vararg params: String): List<T> {
        val ref = db.collection(params[0])
        return mNearbyUsers.value
                ?.flatMap { user ->
                    ref.whereEqualTo("author", user.id)
                            .limit(10)
                            .get()
                            .await()
                            .toObjects(T::class.java)
                            .toList()
                } ?: return listOf()
    }
}

suspend fun <T> Task<T>.await(): T {
    return suspendCoroutine { cont ->
        addOnSuccessListener { data -> cont.resumeWith(Result.success(data)) }
        addOnFailureListener { result -> cont.resumeWithException(result) }
    }
}

//class LocationMediatorLiveData : MediatorLiveData<List<User>>() {
//
//    override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<in S>) {
//        super.addSource(source, onChanged)
//        withContext(Dispatchers.Default) {
//            l
//        }
//    }
//}

