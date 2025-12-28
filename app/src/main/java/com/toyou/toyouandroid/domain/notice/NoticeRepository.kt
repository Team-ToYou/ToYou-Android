package com.toyou.toyouandroid.domain.notice

import com.toyou.toyouandroid.data.notice.dto.AlarmDeleteResponse
import com.toyou.toyouandroid.data.notice.dto.AlarmResponse
import com.toyou.toyouandroid.data.notice.dto.FriendsRequestResponse
import com.toyou.toyouandroid.data.notice.service.NoticeService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepositoryImpl @Inject constructor(
    private val noticeService: NoticeService
) : INoticeRepository {

    override suspend fun getNotices(): Response<AlarmResponse> {
        return noticeService.getAlarms()
    }

    override suspend fun getFriendsRequestNotices(): Response<FriendsRequestResponse> {
        return noticeService.getFriendsRequest()
    }

    override suspend fun deleteNotice(alarmId: Int): Response<AlarmDeleteResponse> {
        return noticeService.deleteAlarm(alarmId)
    }
}