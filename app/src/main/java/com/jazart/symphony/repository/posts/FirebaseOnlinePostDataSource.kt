package com.jazart.symphony.repository.posts

import com.google.firebase.firestore.Source
import com.jazart.symphony.common.Constants
import com.jazart.symphony.common.Constants.AUTHOR
import com.jazart.symphony.common.Constants.POSTS
import com.jazart.symphony.repository.AbstractFirebaseDataSource
import com.jazart.symphony.repository.await
import dagger.Reusable
import entities.Post
import repo.PostRepository
import javax.inject.Inject

@Reusable
class FirebaseOnlinePostDataSource constructor(source: Source) : AbstractFirebaseDataSource(source), PostRepository {
    @Inject
    constructor() : this(Source.SERVER)

    override suspend fun loadPostById(id: String): Post? {
        return db.collection(POSTS).document(id).get(source).await().let { obj ->
            obj.toObject(Post::class.java).apply { this?.id = obj.id }
        }
    }

    override suspend fun loadPostsByUserId(id: String): List<Post> {
        return db.collection(POSTS).whereEqualTo(AUTHOR, id).get(source).await().let { ref ->
            ref.map { refs -> refs.toObject(Post::class.java).apply { this.id = refs.id } }
        }
    }

    override suspend fun deletePost(id: String) {
        db.collection(POSTS).document(id).delete()
    }
}