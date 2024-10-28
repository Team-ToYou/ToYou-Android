package com.toyou.toyouandroid.data.emotion.service

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.data.emotion.dto.EmotionRequest
import com.toyou.toyouandroid.data.emotion.dto.EmotionResponse
import com.toyou.toyouandroid.data.emotion.dto.YesterdayFriendsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EmotionService {

    @POST("users/emotions")
    fun patchEmotion(
        @Body emotion: EmotionRequest
    ): Call<EmotionResponse>

    @GET("friends/yesterday")
    fun getYesterdayFriendCard(): Call<YesterdayFriendsResponse>

    @GET("diarycards/{cardId}")
    fun getDiaryCardDetail(@Path("cardId") cardId : Int): Call<CardDetail>
}