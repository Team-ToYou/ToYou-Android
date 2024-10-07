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
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface FCMService {
    @POST("/fcm/token")
    suspend fun postToken(
        @Header("Authorization") id : String,
        @Body request : Token
    ) : BaseResponse<Unit>

    @GET("/fcm/token")
    suspend fun getToken(
        @Header("Authorization") id : String,
        @Query("keyword") name: String
    ) : BaseResponse<GetToken>

    @POST("/fcm/send")
    suspend fun postFCM(
        @Header("Authorization") id : String,
        @Body request: FCM
    ) : BaseResponse<Unit>

    @PATCH("/fcm/token")
    suspend fun patchFCM(
        @Header("Authorization") id : String,
        @Body request: FCM
    ) : BaseResponse<Unit>

}