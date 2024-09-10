package com.toyou.toyouandroid.data.home.service

import com.toyou.toyouandroid.data.create.dto.request.AnswerDto
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface HomeService {

    @GET("/diarycards/{card-id}")
    suspend fun getCardDetail(
        @Header("userId") id : Int,
        @Path("cardId") card : Long,
    ) : BaseResponse<Unit>
}