package com.toyou.core.domain.repository

import com.toyou.core.domain.model.Notice

interface NoticeRepository {
    suspend fun getNotices(): Result<List<Notice>>
    suspend fun deleteNotice(noticeId: Long): Result<Unit>
}
