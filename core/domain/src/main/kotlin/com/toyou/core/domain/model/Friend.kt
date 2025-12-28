package com.toyou.core.domain.model

data class Friend(
    val id: Long,
    val nickname: String,
    val status: String,
    val emotion: String?
)
