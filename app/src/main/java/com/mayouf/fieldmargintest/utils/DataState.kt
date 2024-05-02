package com.mayouf.fieldmargintest.utils

sealed class DataState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Idle<T> : DataState<T>()
    class Success<T>(data: T) : DataState<T>(data)
    class Loading<T>(data: T? = null) : DataState<T>(data)
    class Error<T>(message: String, data: T? = null) : DataState<T>(data, message)
}