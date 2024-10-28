package com.toyou.toyouandroid.data.notice.service

import com.toyou.toyouandroid.data.notice.dto.AlarmDeleteResponse
import com.toyou.toyouandroid.data.notice.dto.AlarmResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface NoticeService {
    @GET("/alarms")
    fun getAlarms(): Call<AlarmResponse>

    @DELETE("alarms/{id}")
    fun deleteAlarm(@Path("id") id: Int): Call<AlarmDeleteResponse>
}