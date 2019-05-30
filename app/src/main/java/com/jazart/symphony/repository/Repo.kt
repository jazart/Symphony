package com.jazart.symphony.repository

import com.jazart.symphony.Result

interface Repo<T> {
    suspend fun update(): Result
    suspend fun create(item: T): Result
    suspend fun delete(id: String): Result
    suspend fun load():List<T>
}