package com.toyou.core.data.repository

import com.toyou.core.data.mapper.toDomain
import com.toyou.core.data.mapper.toDomainResult
import com.toyou.core.data.mapper.toDomainResultUnit
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FcmToken
import com.toyou.core.domain.model.FcmTokenList
import com.toyou.core.domain.model.PushNotification
import com.toyou.core.domain.repository.IFCMRepository
import com.toyou.core.network.api.FCMService
import com.toyou.core.network.model.fcm.FCM
import com.toyou.core.network.model.fcm.Token
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMRepositoryImpl @Inject constructor(
    private val fcmService: FCMService
) : IFCMRepository {

    override suspend fun postToken(token: FcmToken): DomainResult<Unit> {
        return fcmService.postToken(Token(token.token)).toDomainResultUnit()
    }

    override suspend fun getToken(id: Long): DomainResult<FcmTokenList> {
        return fcmService.getToken(id).toDomainResult { it.toDomain() }
    }

    override suspend fun postFCM(notification: PushNotification): DomainResult<Unit> {
        return fcmService.postFCM(
            FCM(
                token = notification.token,
                title = notification.title,
                body = notification.body
            )
        ).toDomainResultUnit()
    }

    override suspend fun patchToken(token: FcmToken): DomainResult<Unit> {
        return fcmService.patchToken(Token(token.token)).toDomainResultUnit()
    }
}
