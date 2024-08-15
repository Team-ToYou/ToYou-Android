package com.toyou.toyouandroid.presentation.fragment.notice.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface NoticeService {
    @GET("/alarms")
    fun getAlarms(
        @Header("userId") userId: Int
    ): Call<AlarmResponse>
}