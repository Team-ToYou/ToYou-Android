package com.toyou.toyouandroid.domain.notice

import com.toyou.toyouandroid.data.notice.dto.AlarmDeleteResponse
import com.toyou.toyouandroid.data.notice.dto.AlarmResponse
import com.toyou.toyouandroid.data.notice.dto.FriendsRequestResponse
import com.toyou.toyouandroid.data.notice.service.NoticeService
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepository @Inject constructor(private val noticeService: NoticeService) {

    fun getNotices(): Call<AlarmResponse> {
        return noticeService.getAlarms()
    }

    fun getFriendsRequestNotices(): Call<FriendsRequestResponse> {
        return noticeService.getFriendsRequest()
    }

    fun deleteNotice(alarmId: Int): Call<AlarmDeleteResponse> {
        return noticeService.deleteAlarm(alarmId)
    }
}