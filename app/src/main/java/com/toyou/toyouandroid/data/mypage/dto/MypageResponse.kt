package com.toyou.toyouandroid.data.mypage.dto

data class MypageResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: MypageResult
)

data class MypageResult(
    val nickname: String,
    val friendNum: Int,
    val status: String
)