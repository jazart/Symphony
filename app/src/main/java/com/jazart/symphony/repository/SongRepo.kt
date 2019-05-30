package com.jazart.symphony.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jazart.symphony.Result
import com.jazart.symphony.model.Song
import com.jazart.symphony.usecase.UseCase
import javax.inject.Inject

class SongRepo @Inject constructor(private val db: FirebaseFirestore,
                                   private val storage: FirebaseStorage) : Repo<Song> {

    override suspend fun update(): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun create(item: Song, useCase: UseCase?): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun delete(id: String, useCase: UseCase?): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun load(useCase: UseCase?): List<Song> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun single(useCase: UseCase?): Result {
        return Result.Success
    }
}