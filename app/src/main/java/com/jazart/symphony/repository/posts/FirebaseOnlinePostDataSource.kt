package com.jazart.symphony.repository.posts

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.jazart.symphony.common.Constants.AUTHOR
import com.jazart.symphony.common.Constants.POSTS
import com.jazart.symphony.repository.await
import entities.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class FirebaseOnlinePostDataSource @Inject constructor() : com.jazart.data.repo.PostRepository {

    private val db: FirebaseFirestore
        @Synchronized
        get() {
            FirebaseFirestore.getInstance().enableNetwork()
            return FirebaseFirestore.getInstance()
        }

    override suspend fun loadPostById(id: String): Post? {
        return db.collection(POSTS).document(id).get(Source.SERVER).await().toObject(Post::class.java)
    }

    override suspend fun loadPostsByUserId(id: String): List<Post> {
        return db.collection(POSTS).whereEqualTo(AUTHOR, id).get(Source.SERVER).await().toObjects(Post::class.java)
    }
}