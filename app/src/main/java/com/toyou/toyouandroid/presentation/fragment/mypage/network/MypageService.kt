package com.toyou.toyouandroid.presentation.fragment.mypage.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MypageService {

    @GET("/users/mypage")
    fun getMypage(
        @Header("userId") userId: Int
    ): Call<MypageResponse>
}