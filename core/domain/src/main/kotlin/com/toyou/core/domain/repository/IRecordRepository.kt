package com.toyou.core.domain.repository

import com.toyou.core.domain.model.CardDetailInfo
import com.toyou.core.domain.model.CardExposure
import com.toyou.core.domain.model.DiaryCardList
import com.toyou.core.domain.model.DiaryCardNumList
import com.toyou.core.domain.model.DiaryCardPerDayList
import com.toyou.core.domain.model.DomainResult

interface IRecordRepository {
    suspend fun getMyRecord(year: Int, month: Int): DomainResult<DiaryCardList>
    suspend fun getFriendRecordNum(year: Int, month: Int): DomainResult<DiaryCardNumList>
    suspend fun getFriendRecordPerDay(year: Int, month: Int, day: Int): DomainResult<DiaryCardPerDayList>
    suspend fun deleteDiaryCard(cardId: Int): DomainResult<Unit>
    suspend fun patchDiaryCard(cardId: Int): DomainResult<CardExposure>
    suspend fun getCardDetails(cardId: Long): DomainResult<CardDetailInfo>
}
