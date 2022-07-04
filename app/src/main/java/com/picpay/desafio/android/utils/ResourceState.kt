package com.picpay.desafio.android.utils

sealed class ResourceState<out T> {
    class Success<T>(val data: T) : ResourceState<T>()

    open class Error(val code: Int? = null, val message: String? = null) : ResourceState<Nothing>() {
        class ApiError(code: Int, message: String?) : Error(code = code, message = message)
        class ExceptionError(message: String) : Error(message = message)
    }
}
