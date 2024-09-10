package com.toyou.toyouandroid.presentation.fragment.onboarding.network

import com.toyou.toyouandroid.presentation.fragment.onboarding.data.dto.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    fun signUp(
        @Header("oauthAccessToken") accessToken: String
    ): Call<SignUpResponse>

    @POST("auth/reissue")
    fun reissue(
        @Header("refreshToken") refreshToken: String
    ): Call<SignUpResponse>

    @POST("auth/logout")
    fun logout(
        @Header("refreshToken") refreshToken: String
    ): Call<SignUpResponse>

    @POST("auth/kakao")
    fun kakaoLogin(
        @Header("oauthAccessToken") accessToken: String
    ): Call<SignUpResponse>

    @DELETE("auth/unlink")
    fun signOut(
        @Header("refreshToken") refreshToken: String
    ): Call<SignUpResponse>
}