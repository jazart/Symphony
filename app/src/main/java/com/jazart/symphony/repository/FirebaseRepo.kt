package com.jazart.symphony.repository


import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.jazart.symphony.Constants
import com.jazart.symphony.Constants.*
import com.jazart.symphony.model.Song
import com.jazart.symphony.posts.Comment
import com.jazart.symphony.posts.Post
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.io.FileInputStream


class FirebaseRepo private constructor(
        internal val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
        private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
        private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
        private val storage: FirebaseStorage = FirebaseStorage.getInstance()) {

    companion object {
        @JvmStatic
        val firebaseRepoInstance by lazy {
            FirebaseRepo()
        }
    }


    suspend fun addPostToDb(post: Post): Boolean {
        post.author = currentUser?.uid
        post.profilePic = currentUser?.photoUrl.toString()
        try {
            db.collection(POSTS)
                    .add(post)
                    .await()
            return true
        } catch (e: Exception) {
            Log.e("FireBaseRepo", "Add Post Failure", e)
            return false
        }
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

    fun addSongToStorage(song: Song, inputStream: FileInputStream): Observable<Long> {
        val storageRef = storage.reference.child("${Constants.USERS}/${currentUser?.uid}/${Constants.SONGS}/${song.name}")
        val uploadTask = storageRef.putStream(inputStream)
        return Observable.create{ emitter ->
            uploadTask.addOnProgressListener { progress ->
                emitter.onNext(progress.bytesTransferred)
            }
            uploadToFirestore(uploadTask, storageRef, song, emitter)
        }


    }

    private fun uploadToFirestore(uploadTask: UploadTask, storageRef: StorageReference, song: Song, emitter: ObservableEmitter<Long>) {
        uploadTask.addOnCompleteListener {
            if (it.isComplete && it.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { url ->
                    Log.d("44343434", url.result.toString())
                    addSongToFireStore(url.result, song)
                }
            }
            emitter.onComplete()
        }
    }

    private fun addSongToFireStore(songUri: Uri?, song: Song) {
        song.apply {
            uri = "$songUri"
            currentUser?.let { author = it.uid }
            name = song.name
            artists = song.artists
        }
        db.collection(SONGS).add(song)
    }

    fun remove(song: Song): Boolean {
        if(song.author != auth.currentUser?.uid) {
            return false
        }

        db.collection(SONGS).whereEqualTo("uri", song.uri).get()
                .continueWith { querySnapshot ->
                    querySnapshot.result?.forEach { result -> result.reference.delete() }
                }
        val songStorageRef = storage.reference.child("$USERS/${currentUser?.uid}/$SONGS/${song.name}")
        songStorageRef.delete()
        return true
    }
}