package com.toyou.toyouandroid.data.notice.service

import com.toyou.toyouandroid.data.notice.dto.AlarmDeleteResponse
import com.toyou.toyouandroid.data.notice.dto.AlarmResponse
import com.toyou.toyouandroid.data.notice.dto.FriendsRequestResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface NoticeService {
    @GET("/alarms")
    suspend fun getAlarms(): Response<AlarmResponse>

    @DELETE("alarms/{id}")
    suspend fun deleteAlarm(@Path("id") id: Int): Response<AlarmDeleteResponse>

    @GET("/friends/requests")
    suspend fun getFriendsRequest(): Response<FriendsRequestResponse>
}