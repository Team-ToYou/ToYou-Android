package com.toyou.core.domain.model

data class NicknameCheckResult(
    val exists: Boolean
)

data class FcmToken(
    val token: String
)

data class FcmTokenList(
    val tokens: List<String>
)

data class PushNotification(
    val token: String,
    val title: String,
    val body: String
)
