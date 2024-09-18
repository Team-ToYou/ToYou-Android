package com.toyou.toyouandroid.presentation.fragment.notice.network

import retrofit2.Call

class NoticeRepository(private val noticeService: NoticeService) {

    fun getNotices(): Call<AlarmResponse> {
        return noticeService.getAlarms()
    }

    fun deleteNotice(alarmId: Int): Call<AlarmDeleteResponse> {
        return noticeService.deleteAlarm(alarmId)
    }
}