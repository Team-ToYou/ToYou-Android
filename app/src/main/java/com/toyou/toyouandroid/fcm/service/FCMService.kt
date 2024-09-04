package com.toyou.toyouandroid.fcm.service

import com.toyou.toyouandroid.data.create.dto.request.AnswerDto
import com.toyou.toyouandroid.data.create.dto.response.AnswerPost
import com.toyou.toyouandroid.fcm.dto.request.FCM
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.fcm.dto.response.GetToken
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface FCMService {
    @POST("/fcm/token")
    suspend fun postToken(
        @Header("userId") id : Long,
        @Body request : Token
    ) : BaseResponse<Unit>

    @GET("/fcm/token")
    suspend fun getToken(
        @Header("userId") id : Long,
        @Query("keyword") name: String
    ) : BaseResponse<GetToken>

    @POST("/fcm/send")
    suspend fun postFCM(
        @Body request: FCM
    ) : BaseResponse<Unit>

    @HTTP(method = "DELETE", path = "/fcm/token", hasBody = true)
    suspend fun deleteToken(
        @Header("userId") id : Long,
        @Body request: Token
        ) : BaseResponse<Unit>
}