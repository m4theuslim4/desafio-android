package com.picpay.desafio.android.data.respository

import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.repository.IUserRepository
import com.picpay.desafio.android.utils.CountingIdlingResourceSingleton
import com.picpay.desafio.android.utils.ResourceState
import com.picpay.desafio.android.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch

class FakeUserRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IUserRepository {

    private var shouldReturnError = false
    private var shouldThrowException = false
    private var code: Int = -1
    private var message: String = ""
    private val usersData = mutableListOf<UserLocalData>()

    override suspend fun getUsers(): ResourceState<List<UserLocalData>> {
        wrapEspressoIdlingResource {
            return withContext(dispatcher) {
                delay(1_000)
                if (shouldReturnError) return@withContext ResourceState.Error.ApiError(code, message)

                if (shouldThrowException) return@withContext ResourceState.Error.ExceptionError(message)

                return@withContext ResourceState.Success(usersData)
            }
        }
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    fun setCode(code: Int) {
        this.code = code
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setThrowException(value: Boolean) {
        shouldThrowException = value
    }

    fun insertUsers(users: List<UserLocalData>) {
        usersData.addAll(users)
    }

}