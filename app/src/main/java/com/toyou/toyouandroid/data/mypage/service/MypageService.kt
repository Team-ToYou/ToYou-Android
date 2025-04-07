package com.toyou.toyouandroid.data.mypage.service

import com.toyou.toyouandroid.data.mypage.dto.MypageResponse
import retrofit2.Call
import retrofit2.http.GET

interface MypageService {

    @GET("/users/mypage")
    fun getMypage(): Call<MypageResponse>
}