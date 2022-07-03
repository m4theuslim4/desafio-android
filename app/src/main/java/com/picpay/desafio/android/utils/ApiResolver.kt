package com.picpay.desafio.android.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend fun <T, G> handleBoundaryFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    executeApi: suspend () -> Response<T>,
    executeLocalSource: suspend () -> G?,
    saveLocalSource: suspend (G) -> Unit,
    processBeforeSave: (T) -> G
): ResourceState<G> {
    return try {
        withContext(dispatcher) {
            val responseLocalSource = executeLocalSource()
            if (responseLocalSource != null)
                ResourceState.Success(responseLocalSource)
            else {
                val responseApi = executeApi()
                val body = responseApi.body()
                if (responseApi.isSuccessful && body != null) {
                    val bodyToLocalSource = processBeforeSave(body)
                    if (bodyToLocalSource != null) saveLocalSource(bodyToLocalSource)
                    ResourceState.Success(bodyToLocalSource)
                } else {
                    ResourceState.Error.ApiError(
                        code = responseApi.code(),
                        message = responseApi.message()
                    )
                }
            }
        }
    } catch (throwable: Throwable) {
        ResourceState.Error.ExceptionError(message = throwable.message ?: "Exception on fetch users")
    }
}