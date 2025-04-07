package com.toyou.toyouandroid.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun dao() : UserDao

    companion object {
        @Volatile
        private var Instance : UserDatabase? = null

        fun getDatabase(context: Context) : UserDatabase{
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user-database"
                ).build()
                Instance = instance
                instance
            }
        }
    }
}