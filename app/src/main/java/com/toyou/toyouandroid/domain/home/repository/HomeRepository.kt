package com.toyou.toyouandroid.domain.home.repository

import com.toyou.toyouandroid.data.home.service.HomeService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val homeService: HomeService
) {
    suspend fun getCardDetail(id: Long) = homeService.getCardDetail(id)

    suspend fun getYesterdayCard() = homeService.getCardYesterday()
}