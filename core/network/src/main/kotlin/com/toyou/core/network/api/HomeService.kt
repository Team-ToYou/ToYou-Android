package com.toyou.core.network.api

import com.toyou.core.network.model.BaseResponse
import com.toyou.core.network.model.home.CardDetail
import com.toyou.core.network.model.home.YesterdayCardResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeService {

    @GET("/diarycards/{cardId}")
    suspend fun getCardDetail(
        @Path("cardId") cardId: Long
    ): BaseResponse<CardDetail>

    @GET("/diarycards/yesterday")
    suspend fun getCardYesterday(): BaseResponse<YesterdayCardResponse>
}
