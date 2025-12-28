package com.toyou.toyouandroid.domain.record

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.data.record.dto.DeleteDiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardNumResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardPerDayResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.PatchDiaryCardResponse
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepository @Inject constructor(private val recordService: RecordService) {

    fun getMyRecord(year: Int, month: Int): Call<DiaryCardResponse> {
        return recordService.getDiarycardsMine(year, month)
    }

    fun getFriendRecordNum(year: Int, month: Int): Call<DiaryCardNumResponse> {
        return recordService.getDiarycardsNumFriend(year, month)
    }

    fun getFriendRecordPerDay(year: Int, month: Int, day: Int): Call<DiaryCardPerDayResponse> {
        return recordService.getDiarycardsPerDayFriend(year, month, day)
    }

    fun deleteDiaryCard(cardId: Int): Call<DeleteDiaryCardResponse> {
        return recordService.deleteDiarycard(cardId)
    }

    fun patchDiaryCard(cardId: Int): Call<PatchDiaryCardResponse> {
        return recordService.patchDiarycardExposure(cardId)
    }

    suspend fun getCardDetails(cardId: Long): BaseResponse<CardDetail> {
        return recordService.getCardDetail(cardId)
    }
}