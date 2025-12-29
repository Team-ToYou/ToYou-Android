package com.toyou.core.network.api

import com.toyou.core.network.model.BaseResponse
import com.toyou.core.network.model.fcm.FCM
import com.toyou.core.network.model.fcm.GetToken
import com.toyou.core.network.model.fcm.Token
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface FCMService {
    @POST("/fcm/token")
    suspend fun postToken(
        @Body request: Token
    ): BaseResponse<Unit>

    @GET("/fcm/token")
    suspend fun getToken(
        @Query("userId") userId: Long
    ): BaseResponse<GetToken>

    @POST("/fcm/send")
    suspend fun postFCM(
        @Body request: FCM
    ): BaseResponse<Unit>

    @PATCH("/fcm/token")
    suspend fun patchToken(
        @Body request: Token
    ): BaseResponse<Unit>
}
