package com.jazart.symphony.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.jazart.symphony.Constants
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.Post

class FirebaseOnlinePostDataSource: PostRepository{

    private val db: FirebaseFirestore
        @Synchronized
        get() {
            FirebaseFirestore.getInstance().enableNetwork()
            return FirebaseFirestore.getInstance()
        }

    override suspend fun loadPostById(id: String): Post? {
        return db.collection(Constants.POSTS).document(id).get(Source.SERVER).await().toObject(Post::class.java)
    }
}