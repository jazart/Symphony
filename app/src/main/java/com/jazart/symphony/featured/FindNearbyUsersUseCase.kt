package com.jazart.symphony.featured

import com.jazart.data.repo.UserRepository
import com.jazart.symphony.common.Error
import com.jazart.symphony.common.Result
import entities.User
import javax.inject.Inject
import javax.inject.Named

class FindNearbyUsersUseCase @Inject constructor(private val userRepo: UserRepository, @Named("uId") val uId: String) {

    suspend fun execute(): Result<List<User>> {
        val friends = userRepo.getUserFriends(uId)
        return if (friends.isEmpty()) Result.failure(Error.NOT_FOUND) else Result.success(friends)
    }
}