package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.utils.ResourceState

interface IUserRepository {
    suspend fun getUsers(): ResourceState<List<UserLocalData>>
}