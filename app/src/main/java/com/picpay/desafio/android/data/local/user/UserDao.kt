package com.picpay.desafio.android.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: UserEntity)

    @Query("SELECT * FROM user")
    fun getUsers(): List<UserEntity>
}