package com.toyou.core.data.repository

import com.toyou.core.data.mapper.toDomain
import com.toyou.core.data.mapper.toDomainResult
import com.toyou.core.data.mapper.toDomainResultUnit
import com.toyou.core.domain.model.CardDetailInfo
import com.toyou.core.domain.model.CardExposure
import com.toyou.core.domain.model.DiaryCardList
import com.toyou.core.domain.model.DiaryCardNumList
import com.toyou.core.domain.model.DiaryCardPerDayList
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.repository.IRecordRepository
import com.toyou.core.network.api.RecordService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepositoryImpl @Inject constructor(
    private val recordService: RecordService
) : IRecordRepository {

    override suspend fun getMyRecord(year: Int, month: Int): DomainResult<DiaryCardList> {
        return recordService.getDiarycardsMine(year, month).toDomainResult { it.toDomain() }
    }

    override suspend fun getFriendRecordNum(year: Int, month: Int): DomainResult<DiaryCardNumList> {
        return recordService.getDiarycardsNumFriend(year, month).toDomainResult { it.toDomain() }
    }

    override suspend fun getFriendRecordPerDay(year: Int, month: Int, day: Int): DomainResult<DiaryCardPerDayList> {
        return recordService.getDiarycardsPerDayFriend(year, month, day).toDomainResult { it.toDomain() }
    }

    override suspend fun deleteDiaryCard(cardId: Int): DomainResult<Unit> {
        return recordService.deleteDiarycard(cardId).toDomainResultUnit()
    }

    override suspend fun patchDiaryCard(cardId: Int): DomainResult<CardExposure> {
        return recordService.patchDiarycardExposure(cardId).toDomainResult { it.toDomain() }
    }

    override suspend fun getCardDetails(cardId: Long): DomainResult<CardDetailInfo> {
        return recordService.getCardDetail(cardId).toDomainResult { it.toDomain() }
    }
}
