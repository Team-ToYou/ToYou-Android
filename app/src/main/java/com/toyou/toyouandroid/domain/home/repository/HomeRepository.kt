package com.toyou.toyouandroid.domain.home.repository

import com.toyou.toyouandroid.data.home.service.HomeService
import com.toyou.toyouandroid.network.AuthNetworkModule

class HomeRepository {
    private val client = AuthNetworkModule.getClient().create(HomeService::class.java)

    suspend fun getCardDetail(id : Long)=client.getCardDetail(id)
}