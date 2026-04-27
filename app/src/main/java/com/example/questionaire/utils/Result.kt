package com.example.questionaire.utils

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.retain.RetainedEffect
import java.io.IOException

sealed interface Result<out R> {
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val exception: Exception): Result<Nothing>
}

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> = try {
    Result.Success(call())
} catch (e: Exception) {
    Result.Error(e)
}
