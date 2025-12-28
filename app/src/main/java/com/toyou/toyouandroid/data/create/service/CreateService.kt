package com.toyou.toyouandroid.data.create.service

import com.toyou.toyouandroid.data.create.dto.request.AnswerDto
import com.toyou.toyouandroid.data.create.dto.response.AnswerPost
import com.toyou.toyouandroid.data.create.dto.response.QuestionsDto
import com.toyou.toyouandroid.data.create.dto.response.HomeDto
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CreateService {

    @GET("/questions")
    suspend fun getQuestions() : BaseResponse<QuestionsDto>

    @POST("/diarycards")
    suspend fun postCard(
        @Body request : AnswerDto
    ) : BaseResponse<AnswerPost>

    @PATCH("/diarycards/{cardId}")
    suspend fun patchCard(
        @Path("cardId") card : Int,
        @Body request : AnswerDto
    ) : BaseResponse<Unit>

    @GET("/users/home")
    suspend fun getHomeEntry() : BaseResponse<HomeDto>
}