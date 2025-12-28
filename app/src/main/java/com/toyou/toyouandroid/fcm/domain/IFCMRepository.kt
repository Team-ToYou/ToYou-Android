package com.toyou.toyouandroid.fcm.domain

import com.toyou.toyouandroid.fcm.dto.request.FCM
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.fcm.dto.response.GetToken
import com.toyou.toyouandroid.network.BaseResponse

interface IFCMRepository {
    suspend fun postToken(token: Token): BaseResponse<Unit>
    suspend fun getToken(id: Long): BaseResponse<GetToken>
    suspend fun postFCM(request: FCM): BaseResponse<Unit>
    suspend fun patchToken(request: Token): BaseResponse<Unit>
}
