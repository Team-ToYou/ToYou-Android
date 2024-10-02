package com.toyou.toyouandroid.presentation.fragment.record.network

import com.toyou.toyouandroid.data.create.dto.request.AnswerDto
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
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

    @DELETE("diarycards/{cardId}")
    fun deleteDiarycard(
        @Path("cardId") cardId: Int
    ): Call<DeleteDiaryCardResponse>

    @PATCH("diarycards/{cardId}/exposure")
    fun patchDiarycardExposure(
        @Path("cardId") cardId: Int
    ): Call<PatchDiaryCardResponse>
}