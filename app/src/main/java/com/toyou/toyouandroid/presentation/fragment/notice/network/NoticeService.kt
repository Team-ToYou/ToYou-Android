package com.toyou.toyouandroid.presentation.fragment.notice.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface NoticeService {
    @GET("/alarms")
    fun getAlarms(
        @Header("userId") userId: Int
    ): Call<AlarmResponse>

    @DELETE("alarms/{id}")
    fun deleteAlarm(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): Call<AlarmDeleteResponse>
}