package com.toyou.core.domain.repository

import com.toyou.core.domain.model.CardPostResult
import com.toyou.core.domain.model.CreateQuestionList
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.HomeInfo
import com.toyou.core.domain.model.PreviewCardModel

interface ICreateRepository {
    suspend fun getAllData(): DomainResult<CreateQuestionList>
    suspend fun getHomeEntryData(): DomainResult<HomeInfo>
    suspend fun patchCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean,
        cardId: Int
    ): DomainResult<Unit>
    suspend fun postCardData(
        previewCardModels: List<PreviewCardModel>,
        exposure: Boolean
    ): DomainResult<CardPostResult>
}
