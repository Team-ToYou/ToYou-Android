package com.toyou.core.data.repository

import com.toyou.core.data.mapper.toDomain
import com.toyou.core.data.mapper.toDomainResult
import com.toyou.core.domain.model.CardDetailInfo
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.YesterdayCardList
import com.toyou.core.domain.repository.IHomeRepository
import com.toyou.core.network.api.HomeService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeService: HomeService
) : IHomeRepository {

    override suspend fun getCardDetail(id: Long): DomainResult<CardDetailInfo> {
        return homeService.getCardDetail(id).toDomainResult { it.toDomain() }
    }

    override suspend fun getYesterdayCard(): DomainResult<YesterdayCardList> {
        return homeService.getCardYesterday().toDomainResult { it.toDomain() }
    }
}
