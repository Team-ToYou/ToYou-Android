package com.toyou.toyouandroid.data.onboarding.dto

data class NicknameCheckResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: NicknameCheckResult
)

data class NicknameCheckResult(
    val exists: Boolean
)

data class PatchNicknameResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)

data class PatchNicknameRequest(
    val nickname: String
)

data class PatchStatusRequest(
    val status: String
)
