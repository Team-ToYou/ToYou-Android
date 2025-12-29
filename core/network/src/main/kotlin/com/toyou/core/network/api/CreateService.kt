package com.toyou.core.network.api

import com.toyou.core.network.model.BaseResponse
import com.toyou.core.network.model.create.AnswerPost
import com.toyou.core.network.model.create.AnswerRequest
import com.toyou.core.network.model.create.AnswerPatchRequest
import com.toyou.core.network.model.create.HomeDto
import com.toyou.core.network.model.create.QuestionsDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CreateService {

    @GET("/questions")
    suspend fun getQuestions(): BaseResponse<QuestionsDto>

    @POST("/diarycards")
    suspend fun postCard(
        @Body request: AnswerRequest
    ): BaseResponse<AnswerPost>

    @PATCH("/diarycards/{cardId}")
    suspend fun patchCard(
        @Path("cardId") cardId: Int,
        @Body request: AnswerRequest
    ): BaseResponse<Unit>

    @GET("/users/home")
    suspend fun getHomeEntry(): BaseResponse<HomeDto>
}
