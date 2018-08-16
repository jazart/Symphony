package com.jazart.symphony.repository


import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jazart.symphony.Constants
import com.jazart.symphony.Constants.POSTS
import com.jazart.symphony.Constants.SONGS
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

    var uploadProgress = MutableLiveData<Int>()
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

    fun getUserPosts(): PostsLiveData {
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
            also { Log.d("PROGRESS_UPLOAD", "${progress.bytesTransferred} ${progress.totalByteCount}") }
            val progressPercentage = (100.times(progress.bytesTransferred)).div(progress.totalByteCount)
            uploadProgress.value = progressPercentage.toInt()
        }.addOnSuccessListener {
            //Success addSongToFireStore(storageRef.downloadUrl.result, song)
        }.addOnFailureListener {
            //failed
        }
    }

    private fun addSongToFireStore(songUri: Uri, song: Song) {
        song.apply {
            uri = "$songUri"
            author = currentUser?.displayName
        }
        db.collection(SONGS).add(song)
        var nums = intArrayOf(3)
    }
}
