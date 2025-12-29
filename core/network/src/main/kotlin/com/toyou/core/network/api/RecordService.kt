package com.toyou.core.network.api

import com.toyou.core.network.model.BaseResponse
import com.toyou.core.network.model.home.CardDetail
import com.toyou.core.network.model.record.DeleteDiaryCardResponse
import com.toyou.core.network.model.record.DiaryCardNumResponse
import com.toyou.core.network.model.record.DiaryCardPerDayResponse
import com.toyou.core.network.model.record.DiaryCardResponse
import com.toyou.core.network.model.record.PatchDiaryCardResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface RecordService {
    @GET("diarycards/mine")
    suspend fun getDiarycardsMine(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<DiaryCardResponse>

    @GET("diarycards/friends")
    suspend fun getDiarycardsNumFriend(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): Response<DiaryCardNumResponse>

    @GET("diarycards/friends")
    suspend fun getDiarycardsPerDayFriend(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int
    ): Response<DiaryCardPerDayResponse>

    @DELETE("diarycards/{cardId}")
    suspend fun deleteDiarycard(
        @Path("cardId") cardId: Int
    ): Response<DeleteDiaryCardResponse>

    @PATCH("diarycards/{cardId}/exposure")
    suspend fun patchDiarycardExposure(
        @Path("cardId") cardId: Int
    ): Response<PatchDiaryCardResponse>

    @GET("/diarycards/{cardId}")
    suspend fun getCardDetail(
        @Path("cardId") cardId: Long
    ): BaseResponse<CardDetail>
}
