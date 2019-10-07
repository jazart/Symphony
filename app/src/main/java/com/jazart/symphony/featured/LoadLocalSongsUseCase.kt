package com.jazart.symphony.featured

import com.jazart.symphony.common.Error
import com.jazart.symphony.common.Result
import com.jazart.symphony.common.Status
import entities.Song
import repo.SongRepository
import javax.inject.Inject

class LoadLocalSongsUseCase @Inject constructor(private val findNearbyUsersUseCase: FindNearbyUsersUseCase, private val songRepository: SongRepository) {

    suspend fun execute(): Result<List<Song>> {
        val nearbyUsers = findNearbyUsersUseCase.execute()
        return when (nearbyUsers.status) {
            is Status.Success -> {
                val songs = nearbyUsers.data?.flatMap { user -> songRepository.findSongsByUserId(user.id) }
                        ?: return Result.failure(Error.NOT_FOUND)
                Result.success(songs)
            }
            else -> return Result.failure(Error.NOT_FOUND)
        }
    }
}
