package com.jazart.symphony.common


data class Result<out T>(val data: T?, val error: Error?, val status: Status) {
    companion object {
        fun <T> success(data: T): Result<T> = Result(data, null, Status.Success)
        fun <T> failure(error: Error): Result<T> = Result(null, error, Status.Failure)
        fun <T> completed(): Result<T> = Result(null, null, Status.Completed)
    }
}


sealed class Status {
    object Success : Status()
    object Failure : Status()
    object Completed : Status()
}

enum class Error(var message: String = "") {
    NOT_FOUND("Entry not found"),
    ILLEGAL_ACCESS("Invalid access.")
}