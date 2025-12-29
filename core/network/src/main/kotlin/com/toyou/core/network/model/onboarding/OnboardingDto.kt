package com.toyou.core.network.model.onboarding

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

data class SignUpRequest(
    var adConsent: Boolean,
    var nickname: String,
    var status: String
)

data class SignUpResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)
