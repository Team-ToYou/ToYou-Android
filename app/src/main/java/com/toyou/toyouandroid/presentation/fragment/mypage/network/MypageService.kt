package com.toyou.toyouandroid.presentation.fragment.mypage.network

import retrofit2.Call
import retrofit2.http.GET

interface MypageService {

    @GET("/users/mypage")
    fun getMypage(): Call<MypageResponse>
}