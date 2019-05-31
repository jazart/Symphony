package com.jazart.symphony.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.jazart.symphony.Constants
import com.jazart.symphony.posts.Post

class FirebaseOfflinePostDataSource : AbstractFirebaseDataSource(), PostRepository {

    override suspend fun loadPostById(id: String): Post? {
        return db.collection(Constants.POSTS).document(id).get(Source.CACHE).await().toObject(Post::class.java)
    }

    override suspend fun configureDb() {
        db.enableNetwork().await()
        FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
    }

}