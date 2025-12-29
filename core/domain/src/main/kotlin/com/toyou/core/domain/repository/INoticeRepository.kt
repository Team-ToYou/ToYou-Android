package com.toyou.core.domain.repository

import com.toyou.core.domain.model.AlarmList
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FriendRequestList

interface INoticeRepository {
    suspend fun getNotices(): DomainResult<AlarmList>
    suspend fun getFriendsRequestNotices(): DomainResult<FriendRequestList>
    suspend fun deleteNotice(alarmId: Int): DomainResult<Unit>
}
