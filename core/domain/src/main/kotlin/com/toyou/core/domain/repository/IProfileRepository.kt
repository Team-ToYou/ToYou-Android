package com.toyou.core.domain.repository

import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.NicknameCheckResult

interface IProfileRepository {
    suspend fun checkNickname(nickname: String, userId: Int): DomainResult<NicknameCheckResult>
    suspend fun updateNickname(nickname: String): DomainResult<Unit>
    suspend fun updateStatus(status: String): DomainResult<Unit>
}
