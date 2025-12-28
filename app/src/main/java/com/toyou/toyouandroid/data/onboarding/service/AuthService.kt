package com.toyou.toyouandroid.data.onboarding.service

import com.toyou.toyouandroid.data.onboarding.dto.request.SignUpRequest
import com.toyou.toyouandroid.data.onboarding.dto.response.SignUpResponse
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
