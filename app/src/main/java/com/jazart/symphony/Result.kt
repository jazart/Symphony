package com.jazart.symphony

import kotlin.Exception

//sealed class Result {
//    object Loading : Result()
//    object Success : Result()
//    data class SuccessWithData<T>(val data: T) : Result()
//    data class Failure(val e: Throwable = Exception(), val message: Error?) : Result()
//
//}

data class Result<out T>(val data: T?, val error: Error?, val status: Status)  {
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