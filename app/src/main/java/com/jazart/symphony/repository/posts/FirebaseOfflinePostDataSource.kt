package com.jazart.symphony.repository.posts

import com.google.firebase.firestore.Source
import com.jazart.data.repo.PostRepository
import com.jazart.symphony.common.Constants
import com.jazart.symphony.common.Constants.AUTHOR
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import dagger.Reusable
import entities.Post
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class FirebaseOfflinePostDataSource constructor(source: Source) : AbstractFirebaseDataSource(source), PostRepository {
    @Inject
    constructor() : this(Source.CACHE)

    override suspend fun loadPostById(id: String): Post? {
        return db.collection(Constants.POSTS).document(id).get(source).await().toObject(Post::class.java)
    }

    override suspend fun loadPostsByUserId(id: String): List<Post> {
        return db.collection(Constants.POSTS).whereEqualTo(AUTHOR, id).get(source).await().toObjects(Post::class.java)
    }

}