package com.jazart.data.repo

import entities.Post

interface PostRepository {
    suspend fun loadPostById(id: String): Post?
    suspend fun loadPostsByUserId(id: String): List<Post>
}