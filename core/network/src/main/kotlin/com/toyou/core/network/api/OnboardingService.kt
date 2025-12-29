package com.toyou.core.network.api

import com.toyou.core.network.model.onboarding.NicknameCheckResponse
import com.toyou.core.network.model.onboarding.PatchNicknameRequest
import com.toyou.core.network.model.onboarding.PatchNicknameResponse
import com.toyou.core.network.model.onboarding.PatchStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface OnboardingService {

    @GET("users/nickname/check")
    suspend fun getNicknameCheck(
        @Query("nickname") nickname: String,
        @Query("userId") userId: Int
    ): Response<NicknameCheckResponse>

    @PATCH("users/nickname")
    suspend fun patchNickname(
        @Body request: PatchNicknameRequest
    ): Response<PatchNicknameResponse>

    @PATCH("users/status")
    suspend fun patchStatus(
        @Body request: PatchStatusRequest
    ): Response<PatchNicknameResponse>
}
