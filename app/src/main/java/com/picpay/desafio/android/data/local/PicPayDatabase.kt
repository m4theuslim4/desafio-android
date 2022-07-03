package com.picpay.desafio.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.picpay.desafio.android.data.local.user.UserDao
import com.picpay.desafio.android.data.local.user.UserLocalData

@Database(entities = [UserLocalData::class], version = 1)
abstract class PicPayDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}