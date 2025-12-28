package com.toyou.toyouandroid.data.emotion.service

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.data.emotion.dto.EmotionRequest
import com.toyou.toyouandroid.data.emotion.dto.EmotionResponse
import com.toyou.toyouandroid.data.emotion.dto.YesterdayFriendsResponse
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
