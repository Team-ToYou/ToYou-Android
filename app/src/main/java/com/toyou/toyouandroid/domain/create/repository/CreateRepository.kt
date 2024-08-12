package com.toyou.toyouandroid.domain.create.repository

import com.toyou.toyouandroid.data.create.service.CreateService
import com.toyou.toyouandroid.network.RetrofitInstance

class CreateRepository {
    private val client = RetrofitInstance.getInstance().create(CreateService::class.java)

    suspend fun getAllData() = client.getQuestions(1)
}
