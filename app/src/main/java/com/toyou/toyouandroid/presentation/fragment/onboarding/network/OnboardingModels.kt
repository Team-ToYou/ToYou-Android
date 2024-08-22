package com.toyou.toyouandroid.presentation.fragment.onboarding.network

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
