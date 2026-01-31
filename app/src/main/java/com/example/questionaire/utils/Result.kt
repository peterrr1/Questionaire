package com.example.questionaire.utils

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception): Result<Nothing>()
}

fun <T> Result<T>.successOr(fallback: T): T {
    /*
    when(this) {
        is Result.Success -> data
        is Result.Error -> fallback
    }
    */

    return (this as? Result.Success<T>)?.data ?: fallback
}