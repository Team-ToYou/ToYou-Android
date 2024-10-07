package com.toyou.toyouandroid.presentation.fragment.emotionstamp.network

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
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