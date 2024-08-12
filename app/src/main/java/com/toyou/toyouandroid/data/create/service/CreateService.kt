package com.toyou.toyouandroid.data.create.service

import com.toyou.toyouandroid.data.create.dto.response.QuestionsDto
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CreateService {

    @GET("/questions")
    suspend fun getQuestions(
        @Header("userId") id : Long
    ) : BaseResponse<QuestionsDto>
}