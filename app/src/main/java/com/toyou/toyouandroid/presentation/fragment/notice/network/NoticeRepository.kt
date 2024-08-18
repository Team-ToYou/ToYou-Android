package com.toyou.toyouandroid.presentation.fragment.notice.network

import retrofit2.Call

class NoticeRepository(private val noticeService: NoticeService) {

    fun getNotices(userId: Int): Call<AlarmResponse> {
        return noticeService.getAlarms(userId)
    }

    fun deleteNotice(alarmId: Int, userId: Int): Call<AlarmDeleteResponse> {
        return noticeService.deleteAlarm(alarmId, userId)
    }
}