package com.toyou.toyouandroid.domain.record

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.data.record.dto.DeleteDiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardNumResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardPerDayResponse
import com.toyou.toyouandroid.data.record.dto.DiaryCardResponse
import com.toyou.toyouandroid.data.record.dto.PatchDiaryCardResponse
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepositoryImpl @Inject constructor(
    private val recordService: RecordService
) : IRecordRepository {

    override suspend fun getMyRecord(year: Int, month: Int): Response<DiaryCardResponse> {
        return recordService.getDiarycardsMine(year, month)
    }

    override suspend fun getFriendRecordNum(year: Int, month: Int): Response<DiaryCardNumResponse> {
        return recordService.getDiarycardsNumFriend(year, month)
    }

    override suspend fun getFriendRecordPerDay(year: Int, month: Int, day: Int): Response<DiaryCardPerDayResponse> {
        return recordService.getDiarycardsPerDayFriend(year, month, day)
    }

    override suspend fun deleteDiaryCard(cardId: Int): Response<DeleteDiaryCardResponse> {
        return recordService.deleteDiarycard(cardId)
    }

    override suspend fun patchDiaryCard(cardId: Int): Response<PatchDiaryCardResponse> {
        return recordService.patchDiarycardExposure(cardId)
    }

    override suspend fun getCardDetails(cardId: Long): BaseResponse<CardDetail> {
        return recordService.getCardDetail(cardId)
    }
}