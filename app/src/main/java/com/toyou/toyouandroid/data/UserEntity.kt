package com.toyou.toyouandroid.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val nickname: String,
    @PrimaryKey(autoGenerate = true) val cardId: Int?
)
