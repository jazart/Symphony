package com.jazart.symphony

sealed class Result {
    object Loading : Result()
    data class Success<T>(val data: T) : Result()
    data class Failure(val e: Throwable, val message: Error) : Result()
}

enum class Error(var message: String = "") {
    NOT_FOUND("Entry not found"),
    UNKNOWN_ERROR()
    // more enum error cases here
}