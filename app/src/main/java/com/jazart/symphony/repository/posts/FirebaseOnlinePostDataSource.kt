package com.jazart.symphony.repository.posts

import com.google.firebase.firestore.Source
import com.jazart.data.repo.PostRepository
import com.jazart.symphony.common.Constants.AUTHOR
import com.jazart.symphony.common.Constants.POSTS
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import dagger.Reusable
import entities.Post
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class FirebaseOnlinePostDataSource constructor(source: Source) : AbstractFirebaseDataSource(source), PostRepository {
    @Inject
    constructor() : this(Source.SERVER)

    override suspend fun loadPostById(id: String): Post? {
        return db.collection(POSTS).document(id).get(Source.SERVER).await().toObject(Post::class.java)
    }

    override suspend fun loadPostsByUserId(id: String): List<Post> {
        return db.collection(POSTS).whereEqualTo(AUTHOR, id).get(Source.SERVER).await().toObjects(Post::class.java)
    }
}