package com.jazart.symphony.repository


import androidx.lifecycle.MutableLiveData
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jazart.symphony.Constants
import com.jazart.symphony.Constants.*
import com.jazart.symphony.Result
import com.jazart.symphony.model.Song
import com.jazart.symphony.posts.Comment
import com.jazart.symphony.posts.PostsLiveData
import com.jazart.symphony.posts.UserPost
import java.io.FileInputStream


class FirebaseRepo private constructor(
        private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
        private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
        private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
        private val storage: FirebaseStorage = FirebaseStorage.getInstance()) {

    var uploadProgress = MutableLiveData<Result>()
    companion object {
        @JvmStatic
        val firebaseRepoInstance by lazy {
            FirebaseRepo()
        }
    }


    fun addPostToDb(post: UserPost) {
        post.author = currentUser?.uid
        post.profilePic = currentUser?.photoUrl.toString()
        db.collection(POSTS)
                .add(post).addOnCompleteListener {}
                .addOnFailureListener {}
        db.collection(POSTS)
    }

    fun getUserPosts(): PostsLiveData<List<UserPost>> {
        val query = db.collection(POSTS)
                .whereEqualTo("author", currentUser?.uid)
                .orderBy("postDate")
        return PostsLiveData(query)
    }

    fun deletePost(postId: String) {
        db.collection(POSTS)
                .document(postId)
                .delete()

    }

    fun addPostComment(comment: Comment?, id: String?) {
        var reference: CollectionReference
        id?.let { postId ->
            reference = db.collection(POSTS).document(postId).collection("comments")
            comment?.let { postComment ->
                reference.add(postComment).addOnCompleteListener { }
            }
        }
    }

    fun addSongToStorage(song: Song, inputStream: FileInputStream) {
        val storageRef = storage.reference.child("${Constants.USERS}/${currentUser?.uid}/${Constants.SONGS}/${song.name}")
        val uploadTask = storageRef.putStream(inputStream)
        uploadTask.addOnProgressListener { progress ->
            uploadProgress.value = Result.Success(progress.bytesTransferred.toInt())
        }

        uploadTask.addOnCompleteListener {
            if (it.isComplete && it.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { url ->
                    Log.d("44343434", url.result.toString())
                    addSongToFireStore(url.result, song)
                }
            }
        }
    }

    private fun addSongToFireStore(songUri: Uri?, song: Song) {
        song.apply {
            uri = "$songUri"
            author = currentUser?.uid
            name = song.name
            artists = song.artists
        }
        db.collection(SONGS).add(song)
    }

    fun remove(song: Song) {
        db.collection(SONGS).whereEqualTo("uri", song.uri).get()
                .continueWith { querySnapshot ->
                    querySnapshot.result?.forEach { result -> result.reference.delete() }
                }
        val songStorageRef = storage.reference.child("$USERS/${currentUser?.uid}/$SONGS/${song.name}")
        songStorageRef.delete()
    }
}
