package com.picpay.desafio.android.data.local.user

fun List<User>.asDatabaseModel(): List<UserLocalData> {
    return map {
        UserLocalData(
            id = it.id,
            name = it.name,
            img = it.img,
            username = it.username
        )
    }
}

fun List<UserLocalData>.asDomainModel(): List<User> {
    return map {
        User(
            id = it.id,
            name = it.name,
            img = it.img,
            username = it.username
        )
    }
}