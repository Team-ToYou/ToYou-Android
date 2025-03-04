package com.toyou.toyouandroid.domain.home.repository

import com.toyou.toyouandroid.data.home.dto.response.YesterdayCardResponse
import com.toyou.toyouandroid.data.home.service.HomeService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.BaseResponse

class HomeRepository {
    private val client = AuthNetworkModule.getClient().create(HomeService::class.java)

    suspend fun getCardDetail(id : Long)=client.getCardDetail(id)

    suspend fun getYesterdayCard() = client.getCardYesterday()
}