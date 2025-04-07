package com.toyou.toyouandroid.fcm.service

import com.toyou.toyouandroid.fcm.dto.request.FCM
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.fcm.dto.response.GetToken
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface FCMService {
    @POST("/fcm/token")
    suspend fun postToken(
        @Body request : Token
    ) : BaseResponse<Unit>

    @GET("/fcm/token")
    suspend fun getToken(
        @Query("userId") userId: Long
    ) : BaseResponse<GetToken>

    @POST("/fcm/send")
    suspend fun postFCM(
        @Body request: FCM
    ) : BaseResponse<Unit>

    @PATCH("/fcm/token")
    suspend fun patchToken(
        @Body request: Token
    ) : BaseResponse<Unit>
}