package com.jazart.symphony.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.Constants.POSTS
import com.jazart.symphony.Result
import com.jazart.symphony.posts.Post
import javax.inject.Inject

class PostRepo @Inject constructor(private val db: FirebaseFirestore) : Repo<Post> {

    override suspend fun update(): Result {
        return Result.Success
    }

    override suspend fun create(item: Post): Result {
        return Result.Success
    }

    override suspend fun delete(id: String): Result {
        return try {
            db.collection(POSTS).document(id).delete().await()
            Result.Success
        } catch (e: Exception) {
            Result.Failure(e, null)
        }
    }

    override suspend fun load(): List<Post> {
        return emptyList()
    }

    override suspend fun single(): Result {
        return Result.Success
    }
}