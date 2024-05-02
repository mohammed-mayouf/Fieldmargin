package com.mayouf.fieldmargintest.utils

import okio.IOException
import retrofit2.HttpException
import java.util.concurrent.TimeoutException

inline fun <T : Any> safeApiCall(call: () -> T?): DataState<T> {
    return try {
        val response = call.invoke()
        if (response != null) {
            DataState.Success(response)
        } else {
            DataState.Error("Unexpected error: Response is null")
        }
    } catch (e: Exception) {
        DataState.Error(e.toErrorMessage())
    }
}

fun Exception.toErrorMessage(): String {
    return when (this) {
        is HttpException -> {
            when (this.code()) {
                404 -> "Not Found"
                500 -> "Internal Server Error"
                else -> "Unexpected HTTP error, code: ${this.code()}"
            }
        }

        is IOException -> "Network Error. Please check your internet connection."
        is TimeoutException -> "Request timed out. Please try again."
        else -> this.message ?: "An unexpected error occurred."
    }
}