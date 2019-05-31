package com.jazart.symphony.repository
import com.jazart.symphony.posts.Post

interface PostRepository {
    suspend fun loadPostById(id: String): Post?

}