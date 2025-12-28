package com.toyou.toyouandroid.domain.profile.repository

import com.toyou.toyouandroid.data.onboarding.dto.NicknameCheckResponse
import com.toyou.toyouandroid.data.onboarding.dto.PatchNicknameRequest
import com.toyou.toyouandroid.data.onboarding.dto.PatchNicknameResponse
import com.toyou.toyouandroid.data.onboarding.dto.PatchStatusRequest
import com.toyou.toyouandroid.data.onboarding.service.OnboardingService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val onboardingService: OnboardingService
) : IProfileRepository {

    override suspend fun checkNickname(nickname: String, userId: Int): Response<NicknameCheckResponse> {
        return onboardingService.getNicknameCheck(nickname, userId)
    }

    override suspend fun updateNickname(nickname: String): Response<PatchNicknameResponse> {
        return onboardingService.patchNickname(PatchNicknameRequest(nickname))
    }

    override suspend fun updateStatus(status: String): Response<PatchNicknameResponse> {
        return onboardingService.patchStatus(PatchStatusRequest(status))
    }
}