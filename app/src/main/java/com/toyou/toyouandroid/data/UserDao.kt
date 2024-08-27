package com.toyou.toyouandroid.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(userEntity: UserEntity)

    @Query("SELECT * FROM UserEntity")
    suspend fun getAll(): List<UserEntity>

    @Query("DELETE FROM UserEntity")
    suspend fun deleteAll()
}