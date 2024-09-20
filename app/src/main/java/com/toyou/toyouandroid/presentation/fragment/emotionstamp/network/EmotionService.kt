package com.toyou.toyouandroid.presentation.fragment.emotionstamp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface EmotionService {

    @POST("users/emotions")
    fun patchEmotion(
        @Body emotion: EmotionRequest
    ): Call<EmotionResponse>
}