package com.toyou.toyouandroid.network

data class BaseResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T
)