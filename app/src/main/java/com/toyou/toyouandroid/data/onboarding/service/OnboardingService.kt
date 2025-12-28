package com.toyou.toyouandroid.data.onboarding.service

import com.toyou.toyouandroid.data.onboarding.dto.NicknameCheckResponse
import com.toyou.toyouandroid.data.onboarding.dto.PatchNicknameRequest
import com.toyou.toyouandroid.data.onboarding.dto.PatchNicknameResponse
import com.toyou.toyouandroid.data.onboarding.dto.PatchStatusRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface OnboardingService {

    @GET("users/nickname/check")
    fun getNicknameCheck(
        @Query("nickname") nickname: String,
        @Query("userId") userId: Int
    ): Call<NicknameCheckResponse>

    @GET("users/nickname/check")
    suspend fun getNicknameCheckSuspend(
        @Query("nickname") nickname: String,
        @Query("userId") userId: Int
    ): Response<NicknameCheckResponse>

    @PATCH("users/nickname")
    fun patchNickname(
        @Body request: PatchNicknameRequest
    ): Call<PatchNicknameResponse>

    @PATCH("users/nickname")
    suspend fun patchNicknameSuspend(
        @Body request: PatchNicknameRequest
    ): Response<PatchNicknameResponse>

    @PATCH("users/status")
    fun patchStatus(
        @Body request: PatchStatusRequest
    ): Call<PatchNicknameResponse>

    @PATCH("users/status")
    suspend fun patchStatusSuspend(
        @Body request: PatchStatusRequest
    ): Response<PatchNicknameResponse>
}