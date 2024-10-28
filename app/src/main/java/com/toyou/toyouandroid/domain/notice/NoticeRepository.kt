package com.toyou.toyouandroid.domain.notice

import com.toyou.toyouandroid.data.notice.dto.AlarmDeleteResponse
import com.toyou.toyouandroid.data.notice.dto.AlarmResponse
import com.toyou.toyouandroid.data.notice.service.NoticeService
import retrofit2.Call

class NoticeRepository(private val noticeService: NoticeService) {

    fun getNotices(): Call<AlarmResponse> {
        return noticeService.getAlarms()
    }

    fun deleteNotice(alarmId: Int): Call<AlarmDeleteResponse> {
        return noticeService.deleteAlarm(alarmId)
    }
}