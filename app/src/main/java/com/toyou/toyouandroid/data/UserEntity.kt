package com.toyou.toyouandroid.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val emotion: String?,
    val cardId: Int?
)
