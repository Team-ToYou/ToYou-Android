package com.toyou.toyouandroid.domain.home.repository

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCardResponse
import com.toyou.toyouandroid.data.home.service.HomeService
import com.toyou.toyouandroid.network.BaseResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeService: HomeService
) : IHomeRepository {
    override suspend fun getCardDetail(id: Long): BaseResponse<CardDetail> =
        homeService.getCardDetail(id)

    override suspend fun getYesterdayCard(): BaseResponse<YesterdayCardResponse> =
        homeService.getCardYesterday()
}