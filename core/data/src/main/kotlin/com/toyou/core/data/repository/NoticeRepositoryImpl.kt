package com.toyou.core.data.repository

import com.toyou.core.data.mapper.toDomain
import com.toyou.core.data.mapper.toDomainResult
import com.toyou.core.data.mapper.toDomainResultUnit
import com.toyou.core.domain.model.AlarmList
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FriendRequestList
import com.toyou.core.domain.repository.INoticeRepository
import com.toyou.core.network.api.NoticeService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepositoryImpl @Inject constructor(
    private val noticeService: NoticeService
) : INoticeRepository {

    override suspend fun getNotices(): DomainResult<AlarmList> {
        return noticeService.getAlarms().toDomainResult { it.toDomain() }
    }

    override suspend fun getFriendsRequestNotices(): DomainResult<FriendRequestList> {
        return noticeService.getFriendsRequest().toDomainResult { it.toDomain() }
    }

    override suspend fun deleteNotice(alarmId: Int): DomainResult<Unit> {
        return noticeService.deleteAlarm(alarmId).toDomainResultUnit()
    }
}
