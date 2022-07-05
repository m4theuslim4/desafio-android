package com.picpay.desafio.android.utils

import com.picpay.desafio.android.data.local.user.User
import com.picpay.desafio.android.data.local.user.UserLocalData

object MockHelper {

    const val ERROR_CODE = 404
    const val ERROR_MESSAGE = "Error response test"
    const val ERROR_MESSAGE_ESCAPED = "\"Error response test\""

    fun generateUsersMock(size: Int): List<User> {
        val users: MutableList<User> = mutableListOf()
        for(i in 1..size+1) {
            val user = User(id = i, name = "user$i", img = "", username = "user${i}_username")
            users.add(user)
        }
        return users
    }

    fun generateUsersLocalDataMock(size: Int): List<UserLocalData> {
        val usersLocalData: MutableList<UserLocalData> = mutableListOf()
        for(i in 1..size+1) {
            val user = UserLocalData(id = i, name = "user$i", img = "", username = "user${i}_username")
            usersLocalData.add(user)
        }
        return usersLocalData
    }
}