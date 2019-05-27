package com.jazart.symphony

import kotlin.Exception

sealed class Result {
    object Loading : Result()
    object Success : Result()
    data class SuccessWithData<T>(val data: T) : Result()
    data class Failure(val e: Throwable = Exception(), val message: Error?) : Result()
}

enum class Error(var message: String = "") {
    NOT_FOUND("Entry not found"),
    ILLEGAL_ACCESS("Invalid access.")
}