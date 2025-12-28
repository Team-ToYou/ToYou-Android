package com.toyou.core.domain.model

data class Notice(
    val id: Long,
    val type: String,
    val message: String,
    val senderId: Long?,
    val senderNickname: String?,
    val isRead: Boolean
)
