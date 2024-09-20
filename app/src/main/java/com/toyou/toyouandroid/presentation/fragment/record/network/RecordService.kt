package com.toyou.toyouandroid.presentation.fragment.record.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RecordService {
    @GET("diarycards/mine")
    fun getDiarycardsMine(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Call<DiaryCardResponse>

    @GET("diarycards/friends")
    fun getDiarycardsNumFriend(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): Call<DiaryCardNumResponse>

    @GET("diarycards/friends")
    fun getDiarycardsPerDayFriend(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int
    ): Call<DiaryCardPerDayResponse>
}