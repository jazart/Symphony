package com.jazart.symphony.location

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.Constants.POSTS
import com.jazart.symphony.model.Song
import com.jazart.symphony.posts.Comment
import com.jazart.symphony.posts.PostsLiveData
import com.jazart.symphony.posts.UserPost

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

    fun addSongToStorage(song: Song) {

    }

}