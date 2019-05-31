package com.jazart.symphony.repository

import com.jazart.symphony.model.User

interface UserRepository {
    suspend fun getUserById(id: String): User?
    suspend fun getUserFriends(id: String): List<User>
}