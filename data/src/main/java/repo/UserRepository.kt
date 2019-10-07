package repo

import entities.User

interface UserRepository {
    suspend fun findUserById(id: String): User?
    suspend fun getUserFriends(id: String): List<User>
    suspend fun getNearbyUsers(location: String): List<User>
}