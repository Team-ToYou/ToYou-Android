package com.toyou.toyouandroid.fcm.service

import com.toyou.toyouandroid.data.create.dto.request.AnswerDto
import com.toyou.toyouandroid.data.create.dto.response.AnswerPost
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FCMService {
    @POST("/fcm/token")
    suspend fun postToken(
        @Header("userId") id : Long,
        @Body request : Token
    ) : BaseResponse<Unit>
}