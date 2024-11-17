package com.toyou.toyouandroid.data.home.service

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface HomeService {

    @GET("/diarycards/{cardId}")
    suspend fun getCardDetail(
        @Header("Authorization") id : String,
        @Path("cardId") card : Long,
    ) : BaseResponse<CardDetail>
}