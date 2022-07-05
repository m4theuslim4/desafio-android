package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.data.local.user.User
import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.local.user.asDatabaseModel
import com.picpay.desafio.android.utils.ResourceState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FakeTestUserRepository(
    private val dispatcher: CoroutineDispatcher
) : IUserRepository {

    private var shouldReturnError = false
    private var shouldThrowException = false
    private var code: Int = -1
    private var message: String = ""
    private val usersData = mutableListOf<UserLocalData>()

    override suspend fun getUsers(): ResourceState<List<UserLocalData>> {
        return withContext(dispatcher) {
            if(shouldReturnError) return@withContext ResourceState.Error.ApiError(code, message)

            if(shouldThrowException) return@withContext ResourceState.Error.ExceptionError(message)

            return@withContext ResourceState.Success(usersData)
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