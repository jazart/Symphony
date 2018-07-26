package com.jazart.symphony

sealed class Result<T> {
    data class Loading<T>(var loading: Boolean) : Result<T>()
    data class Success<T>(var data: T) : Result<T>() {
        data class Failure<T>(val e: Throwable, val message: String = "Failure") : Result<T>()
    }

    companion object {
        fun <T> loading(isLoading: Boolean): Result<T> = Loading(isLoading)
        fun <T> success(data: T) = Success(data)
        fun <T> failure(e: Throwable, message: String = "Failure"): Result<T> = Success.Failure(e, message)
    }
}

enum class DatabaseError(message: String) {
    NOT_FOUND("Entry not found")
    // more enum error cases here

}