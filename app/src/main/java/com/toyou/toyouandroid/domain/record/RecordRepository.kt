package com.toyou.toyouandroid.domain.record

import com.toyou.toyouandroid.data.record.dto.DeleteDiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardNumResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardPerDayResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.PatchDiaryCardResponse
import com.toyou.toyouandroid.data.record.service.RecordService
import retrofit2.Call

class RecordRepository(private val recordService: RecordService) {

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
}