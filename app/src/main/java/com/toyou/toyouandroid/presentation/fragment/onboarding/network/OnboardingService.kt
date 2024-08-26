package com.toyou.toyouandroid.presentation.fragment.onboarding.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Query

interface OnboardingService {

    @GET("users/nickname/check")
    fun getNicknameCheck(
        @Query("nickname") nickname: String
    ): Call<NicknameCheckResponse>

    @PATCH("users/nickname")
    fun patchNickname(
        @Header("userId") userId: Int,
        @Body nickname: String
    ): Call<PatchNicknameResponse>
}