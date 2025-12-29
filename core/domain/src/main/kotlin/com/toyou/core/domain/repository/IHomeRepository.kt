package com.toyou.core.domain.repository

import com.toyou.core.domain.model.CardDetailInfo
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.YesterdayCardList

interface IHomeRepository {
    suspend fun getCardDetail(id: Long): DomainResult<CardDetailInfo>
    suspend fun getYesterdayCard(): DomainResult<YesterdayCardList>
}
