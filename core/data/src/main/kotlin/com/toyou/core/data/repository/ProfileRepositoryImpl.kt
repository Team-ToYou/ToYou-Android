package com.toyou.core.data.repository

import com.toyou.core.data.mapper.toDomain
import com.toyou.core.data.mapper.toDomainResult
import com.toyou.core.data.mapper.toDomainResultUnit
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.NicknameCheckResult
import com.toyou.core.domain.repository.IProfileRepository
import com.toyou.core.network.api.OnboardingService
import com.toyou.core.network.model.onboarding.PatchNicknameRequest
import com.toyou.core.network.model.onboarding.PatchStatusRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val onboardingService: OnboardingService
) : IProfileRepository {

    override suspend fun checkNickname(nickname: String, userId: Int): DomainResult<NicknameCheckResult> {
        return onboardingService.getNicknameCheck(nickname, userId).toDomainResult { it.toDomain() }
    }

    override suspend fun updateNickname(nickname: String): DomainResult<Unit> {
        return onboardingService.patchNickname(PatchNicknameRequest(nickname)).toDomainResultUnit()
    }

    override suspend fun updateStatus(status: String): DomainResult<Unit> {
        return onboardingService.patchStatus(PatchStatusRequest(status)).toDomainResultUnit()
    }
}
