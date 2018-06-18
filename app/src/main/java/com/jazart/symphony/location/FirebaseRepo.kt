package com.jazart.symphony.location

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.Constants.POSTS
import com.jazart.symphony.posts.PostsLiveData
import com.jazart.symphony.posts.UserPost

class FirebaseRepo(val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
                   val auth: FirebaseAuth = FirebaseAuth.getInstance(),
                   val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {


    val firebaseRepoInstance by lazy {
        FirebaseRepo()
    }

    fun addPostToDb(post: UserPost) {
        post.author = currentUser?.uid
        post.profilePic = currentUser?.photoUrl.toString()
        db.collection(POSTS)
                .add(post).addOnCompleteListener {
                    //success msg
                }
                .addOnFailureListener {}
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

}