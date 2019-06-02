package com.jazart.symphony.repository.posts

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.jazart.data.repo.PostRepository
import com.jazart.symphony.common.Constants
import com.jazart.symphony.common.Constants.AUTHOR
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import entities.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class FirebaseOfflinePostDataSource @Inject constructor(): AbstractFirebaseDataSource(), PostRepository {

    override suspend fun loadPostById(id: String): Post? {
        return db.collection(Constants.POSTS).document(id).get(Source.CACHE).await().toObject(Post::class.java)
    }

    override suspend fun loadPostsByUserId(id: String): List<Post> {
        return db.collection(Constants.POSTS).whereEqualTo(AUTHOR, id).get(Source.CACHE).await().toObjects(Post::class.java)
    }

    override suspend fun configureDb() {
        db.enableNetwork().await()
        FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
    }

}