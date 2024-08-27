package com.toyou.toyouandroid.data.home.service

import com.toyou.toyouandroid.data.home.dto.response.HomeDto
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeService {
    @GET("/users/home")
    suspend fun getHomeEntry(
        @Header("userId") id : Int,
    ) : BaseResponse<HomeDto>
}