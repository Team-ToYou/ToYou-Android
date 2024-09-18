package com.toyou.toyouandroid.domain.home.repository

import com.toyou.toyouandroid.data.create.service.CreateService
import com.toyou.toyouandroid.data.home.service.HomeService
import com.toyou.toyouandroid.network.RetrofitInstance
import com.toyou.toyouandroid.utils.TokenStorage

class HomeRepository(private val tokenStorage: TokenStorage) {
    private val client = RetrofitInstance.getInstance().create(HomeService::class.java)

    suspend fun getCardDetail(id : Long)=client.getCardDetail("Bearer ${tokenStorage.getAccessToken().toString()}", id)

}