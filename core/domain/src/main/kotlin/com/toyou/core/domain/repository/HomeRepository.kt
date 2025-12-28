package com.toyou.core.domain.repository

import com.toyou.core.domain.model.Card

interface HomeRepository {
    suspend fun getCardDetail(cardId: Long): Result<Card>
    suspend fun getYesterdayCard(): Result<Card?>
}
