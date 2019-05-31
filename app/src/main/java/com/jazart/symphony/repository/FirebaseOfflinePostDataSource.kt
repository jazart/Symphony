package com.jazart.symphony.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.jazart.symphony.Constants
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.Post

class FirebaseOfflinePostDataSource : PostRepository{
    private val db: FirebaseFirestore
        @Synchronized
        get() {
            FirebaseFirestore.getInstance().disableNetwork()
            return FirebaseFirestore.getInstance()
        }

    override suspend fun loadPostById(id: String): Post? {
        return db.collection(Constants.POSTS).document(id).get(Source.CACHE).await().toObject(Post::class.java)
    }
    init {
        configureDb()
    }
    private fun configureDb() {
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
    }
}