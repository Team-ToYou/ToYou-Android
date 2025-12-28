package com.toyou.toyouandroid.domain.profile.repository

import com.toyou.toyouandroid.data.onboarding.dto.NicknameCheckResponse
import com.toyou.toyouandroid.data.onboarding.dto.PatchNicknameResponse
import retrofit2.Response

interface IProfileRepository {
    suspend fun checkNickname(nickname: String, userId: Int): Response<NicknameCheckResponse>
    suspend fun updateNickname(nickname: String): Response<PatchNicknameResponse>
    suspend fun updateStatus(status: String): Response<PatchNicknameResponse>
}
