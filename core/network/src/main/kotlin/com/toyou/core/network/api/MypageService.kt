package com.toyou.core.network.api

import com.toyou.core.network.model.mypage.MypageResponse
import retrofit2.Response
import retrofit2.http.GET

interface MypageService {
    @GET("/users/mypage")
    suspend fun getMypage(): Response<MypageResponse>
}
