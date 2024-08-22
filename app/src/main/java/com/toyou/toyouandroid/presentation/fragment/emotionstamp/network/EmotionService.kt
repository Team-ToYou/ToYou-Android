package com.toyou.toyouandroid.presentation.fragment.emotionstamp.network

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Query

interface EmotionService {

    @PATCH("users/emotions")
    fun patchEmotion(
        @Header("userId") userId: Int,
        @Query("emotion") emotion: EmotionRequest
    ): Call<EmotionResponse>
}