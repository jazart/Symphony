package com.jazart.symphony.location

import android.net.Uri
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

class FirebaseRepo(private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
                   private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
                   private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    val firebaseRepoInstance by lazy {
        FirebaseRepo()
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

    fun addSongToStorage(song: Song, songStream: FileInputStream) {
        val store = FirebaseStorage.getInstance().reference

        val songRef = store.child(Constants.USERS +
                "/" +
                currentUser?.uid +
                "/" +
                Constants.SONGS +
                "/" +
                song.name)
        val songTask = songRef.putStream(songStream)
        songTask.addOnCompleteListener {
            songRef.downloadUrl.addOnSuccessListener { downloadUrl -> addSongToFireStore(downloadUrl, song) }
        }
        songTask.addOnFailureListener {
        }
    }

    private fun addSongToFireStore(songUri: Uri, song: Song) {
        song.uri = "$songUri"
        song.author = currentUser?.uid
        db.collection(SONGS).add(song)
    }
}