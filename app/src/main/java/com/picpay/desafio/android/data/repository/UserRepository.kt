package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.data.local.PicPayDatabase
import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.local.user.asDatabaseModel
import com.picpay.desafio.android.data.network.PicPayService
import com.picpay.desafio.android.utils.ResourceState
import com.picpay.desafio.android.utils.handleBoundaryFlow
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val picPayDatabase: PicPayDatabase,
    private val picPayService: PicPayService,
    private val dispatcher: CoroutineDispatcher
) {
    private val userDao = picPayDatabase.userDao()

    suspend fun getUsers(): ResourceState<List<UserLocalData>> {
        return handleBoundaryFlow(
            dispatcher = dispatcher,
            executeApi = { picPayService.fetchUsers() },
            executeLocalSource = { userDao.getUsers().takeIf { it.isNotEmpty() } },
            processBeforeSave = { usersFromApi -> usersFromApi.asDatabaseModel()},
            saveLocalSource = { users -> userDao.insertAll(users)}
        )
    }
}