package com.toyou.toyouandroid.presentation.fragment.notice.network

import retrofit2.Call

class NoticeRepository(private val noticeService: NoticeService) {

    fun getNotices(userId: Int): Call<AlarmResponse> {
        return noticeService.getAlarms(userId)
    }
}