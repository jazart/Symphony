package com.jazart.data.repo

import entities.User

interface UserRepository {
    suspend fun getUserById(id: String): User?
    suspend fun getUserFriends(id: String): List<User>
}