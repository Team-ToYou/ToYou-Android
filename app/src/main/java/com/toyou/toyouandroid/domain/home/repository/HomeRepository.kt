package com.toyou.toyouandroid.domain.home.repository

import com.toyou.toyouandroid.data.create.service.CreateService
import com.toyou.toyouandroid.data.home.service.HomeService
import com.toyou.toyouandroid.network.RetrofitInstance

class HomeRepository {
    private val client = RetrofitInstance.getInstance().create(HomeService::class.java)

    suspend fun getHomeEntryData() = client.getHomeEntry(1)

}