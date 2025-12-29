package com.toyou.core.domain.repository

import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FcmToken
import com.toyou.core.domain.model.FcmTokenList
import com.toyou.core.domain.model.PushNotification

interface IFCMRepository {
    suspend fun postToken(token: FcmToken): DomainResult<Unit>
    suspend fun getToken(id: Long): DomainResult<FcmTokenList>
    suspend fun postFCM(notification: PushNotification): DomainResult<Unit>
    suspend fun patchToken(token: FcmToken): DomainResult<Unit>
}
