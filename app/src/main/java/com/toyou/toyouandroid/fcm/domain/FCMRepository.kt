package com.toyou.toyouandroid.fcm.domain

import com.toyou.toyouandroid.fcm.dto.request.FCM
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.fcm.dto.response.GetToken
import com.toyou.toyouandroid.fcm.service.FCMService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.BaseResponse
import com.toyou.toyouandroid.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class FCMRepository(private val fcmService: FCMService) {

    //private val client = AuthNetworkModule.getClient().create(FCMService::class.java)


    suspend fun postToken(token: Token): BaseResponse<Unit> {
        return fcmService.postToken(token)
    }

    suspend fun getToken(name: String): BaseResponse<GetToken> {
        return fcmService.getToken(name)
    }

    suspend fun postFCM(request: FCM): BaseResponse<Unit> {
        return fcmService.postFCM(request)
    }

    suspend fun patchToken(request: Token): BaseResponse<Unit> {
        return fcmService.patchToken(request)
    }
    /*suspend fun postToken(
        token: Token
    ) {
        try {
            val response = client.postToken(token)
            // 응답 처리
            if (response.isSuccess) {
                Timber.tag("post 성공!").d(response.message)
            } else {
                Timber.tag("post 실패!").d(response.message)
                tokenManager.refreshToken(
                    onSuccess = {
                        CoroutineScope(Dispatchers.IO).launch {
                            postToken(token)
                        }
                    },
                    onFailure = { Timber.e("postToken API call failed") }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("post 실패!").d(e.message.toString())
        }
    }

    suspend fun getToken(name : String) = client.getToken(name)

    suspend fun postFCM(
        request: FCM
    ){
        try {
            val response = client.postFCM(request)
            // 응답 처리
            if (response.isSuccess) {
                Timber.tag("fcm 성공!").d(response.message)
            } else {
                Timber.tag("fcm 실패!").d(response.message)
                tokenManager.refreshToken(
                    onSuccess = {
                        CoroutineScope(Dispatchers.IO).launch {
                            postFCM(request)
                        }
                    },
                    onFailure = { Timber.e("postFCM API call failed") }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("fcm 실패!").d(e.message.toString())
        }
    }

    suspend fun patchToken(
        request: Token
    ){
        try {
            val response = client.patchToken(request)
            // 응답 처리
            if (response.isSuccess) {
                Timber.tag("fcm 갱신 성공!").d(response.message)
            } else {
                Timber.tag("fcm 갱신 실패!").d(response.message)
                tokenManager.refreshToken(
                    onSuccess = {
                        CoroutineScope(Dispatchers.IO).launch {
                            patchToken(request)
                        }
                    },
                    onFailure = { Timber.e("patchToken API call failed") }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("fcm 갱신 실패!").d(e.message.toString())
        }
    }*/
}