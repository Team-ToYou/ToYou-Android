package com.toyou.toyouandroid.domain.record

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.data.record.dto.DeleteDiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardNumResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardPerDayResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.PatchDiaryCardResponse
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.Response

interface IRecordRepository {
    suspend fun getMyRecord(year: Int, month: Int): Response<DiaryCardResponse>
    suspend fun getFriendRecordNum(year: Int, month: Int): Response<DiaryCardNumResponse>
    suspend fun getFriendRecordPerDay(year: Int, month: Int, day: Int): Response<DiaryCardPerDayResponse>
    suspend fun deleteDiaryCard(cardId: Int): Response<DeleteDiaryCardResponse>
    suspend fun patchDiaryCard(cardId: Int): Response<PatchDiaryCardResponse>
    suspend fun getCardDetails(cardId: Long): BaseResponse<CardDetail>
}
