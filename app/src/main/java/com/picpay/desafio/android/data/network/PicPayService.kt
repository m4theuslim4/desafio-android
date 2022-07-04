package com.picpay.desafio.android.data.network

import com.picpay.desafio.android.data.local.user.User
import retrofit2.Response
import retrofit2.http.GET


interface PicPayService {

    @GET("users")
    suspend fun fetchUsers(): Response<List<User>>
}