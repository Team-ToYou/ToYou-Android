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
class ProfileRepository @Inject constructor(
    private val onboardingService: OnboardingService
) {
    suspend fun checkNickname(nickname: String, userId: Int): Response<NicknameCheckResponse> {
        return onboardingService.getNicknameCheckSuspend(nickname, userId)
    }
    
    suspend fun updateNickname(nickname: String): Response<PatchNicknameResponse> {
        return onboardingService.patchNicknameSuspend(PatchNicknameRequest(nickname))
    }
    
    suspend fun updateStatus(status: String): Response<PatchNicknameResponse> {
        return onboardingService.patchStatusSuspend(PatchStatusRequest(status))
    }
}