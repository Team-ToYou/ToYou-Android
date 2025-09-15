package com.toyou.toyouandroid.data.onboarding.service

import com.toyou.toyouandroid.data.onboarding.dto.request.SignUpRequest
import com.toyou.toyouandroid.data.onboarding.dto.response.SignUpResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    fun signUp(
        @Header("oauthAccessToken") accessToken: String,
        @Body request: SignUpRequest
    ): Call<SignUpResponse>

    @POST("auth/reissue")
    fun reissue(
        @Header("refreshToken") refreshToken: String
    ): Call<SignUpResponse>

    @POST("auth/logout")
    fun logout(
        @Header("refreshToken") refreshToken: String
    ): Call<SignUpResponse>

    @POST("auth/logout")
    suspend fun logoutSuspend(
        @Header("refreshToken") refreshToken: String
    ): Response<SignUpResponse>

    @POST("auth/kakao")
    fun kakaoLogin(
        @Header("oauthAccessToken") accessToken: String
    ): Call<SignUpResponse>

    @DELETE("auth/unlink")
    fun signOut(
        @Header("refreshToken") refreshToken: String
    ): Call<SignUpResponse>

    @DELETE("auth/unlink")
    suspend fun signOutSuspend(
        @Header("refreshToken") refreshToken: String
    ): Response<SignUpResponse>
}