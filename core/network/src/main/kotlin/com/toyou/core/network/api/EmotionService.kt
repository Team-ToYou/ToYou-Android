package com.toyou.core.network.api

import com.toyou.core.network.model.emotion.EmotionRequest
import com.toyou.core.network.model.emotion.EmotionResponse
import com.toyou.core.network.model.emotion.YesterdayFriendsResponse
import com.toyou.core.network.model.home.CardDetail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EmotionService {
    @POST("users/emotions")
    suspend fun patchEmotion(
        @Body emotion: EmotionRequest
    ): Response<EmotionResponse>

    @GET("diarycards/yesterday")
    suspend fun getYesterdayFriendCard(): Response<YesterdayFriendsResponse>

    @GET("diarycards/{cardId}")
    suspend fun getDiaryCardDetail(@Path("cardId") cardId: Int): Response<CardDetail>
}
