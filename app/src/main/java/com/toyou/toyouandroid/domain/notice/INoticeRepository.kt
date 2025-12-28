package com.toyou.toyouandroid.domain.notice

import com.toyou.toyouandroid.data.notice.dto.AlarmDeleteResponse
import com.toyou.toyouandroid.data.notice.dto.AlarmResponse
import com.toyou.toyouandroid.data.notice.dto.FriendsRequestResponse
import retrofit2.Response

interface INoticeRepository {
    suspend fun getNotices(): Response<AlarmResponse>
    suspend fun getFriendsRequestNotices(): Response<FriendsRequestResponse>
    suspend fun deleteNotice(alarmId: Int): Response<AlarmDeleteResponse>
}
