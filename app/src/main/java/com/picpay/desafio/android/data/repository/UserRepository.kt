package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.data.local.PicPayDatabase
import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.local.user.asDatabaseModel
import com.picpay.desafio.android.data.network.PicPayService
import com.picpay.desafio.android.utils.*
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val picPayDatabase: PicPayDatabase,
    private val picPayService: PicPayService,
    private val dispatcher: CoroutineDispatcher
) {
    private val userDao = picPayDatabase.userDao()

    suspend fun getUsers(): ResourceState<List<UserLocalData>> {
        val usersFromDatabase =
            getDataFromDatabase(dispatcher) { userDao.getUsers().takeIf { it.isNotEmpty() } }
        if (usersFromDatabase != null) return ResourceState.Success(usersFromDatabase)

        val usersFromApi = handleApi(
            dispatcher = dispatcher,
            execute = { picPayService.fetchUsers() },
            processResponse = { usersFromApi -> usersFromApi.asDatabaseModel() }
        )
        if(usersFromApi is ResourceState.Success) {
            setDataInDatabase(
                dispatcher = dispatcher,
                data = usersFromApi.data,
                execute = { users -> userDao.insertAll(users) }
            )
        }
        return usersFromApi
    }
}