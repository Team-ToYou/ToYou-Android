package com.toyou.core.network.api

import com.toyou.core.network.model.onboarding.SignUpRequest
import com.toyou.core.network.model.onboarding.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signUp(
        @Header("oauthAccessToken") accessToken: String,
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @POST("auth/reissue")
    suspend fun reissue(
        @Header("refreshToken") refreshToken: String
    ): Response<SignUpResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Header("refreshToken") refreshToken: String
    ): Response<SignUpResponse>

    @POST("auth/kakao")
    suspend fun kakaoLogin(
        @Header("oauthAccessToken") accessToken: String
    ): Response<SignUpResponse>

    @DELETE("auth/unlink")
    suspend fun signOut(
        @Header("refreshToken") refreshToken: String
    ): Response<SignUpResponse>
}
