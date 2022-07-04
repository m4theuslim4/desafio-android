package com.picpay.desafio.android.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

suspend fun <T> getDataFromDatabase(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    execute: suspend () -> T
): T {
    return withContext(dispatcher) {
        execute()
    }
}

suspend fun <T> setDataInDatabase(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    data: T,
    execute: suspend (T) -> Unit
) {
    withContext(dispatcher) {
        execute(data)
    }
}

suspend fun <T, V> handleApi(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    execute: suspend () -> Response<T>,
    processResponse: suspend (T) -> V
): ResourceState<V> {
    return try {
        withContext(dispatcher) {
            val responseApi = execute()
            val body = responseApi.body()
            if (responseApi.isSuccessful && body != null) {
                val bodyProcessed = processResponse(body)
                ResourceState.Success(bodyProcessed)
            } else {
                ResourceState.Error.ApiError(
                    code = responseApi.code(),
                    message = responseApi.errorBody()?.toObject()
                )
            }
        }
    } catch (throwable: Throwable) {
        ResourceState.Error.ExceptionError(
            message = throwable.message ?: "General Exception"
        )
    }
}