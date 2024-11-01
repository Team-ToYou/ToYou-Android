package com.toyou.toyouandroid.fcm.domain

import android.util.Log
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.fcm.dto.request.FCM
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.fcm.service.FCMService
import com.toyou.toyouandroid.network.RetrofitInstance
import com.toyou.toyouandroid.utils.TokenStorage

class FCMRepository(private val tokenStorage: TokenStorage) {

    private val client = RetrofitInstance.getInstance().create(FCMService::class.java)

    suspend fun postToken(
        token: Token,
    ) {
        try {
            val response = client.postToken("Bearer ${tokenStorage.getAccessToken().toString()}", token)
            Log.d("로그인","Bearer ${tokenStorage.getAccessToken().toString()}")
            // 응답 처리
            if (response.isSuccess) {
                Log.d("post성공!", response.message)
            } else {
                Log.d("post실패!", "Response Code: ${response.code}, Message: ${response.message}")
                response.result?.let {
                    Log.d("post실패!", "Response Body: ${it}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("post실패!", e.message.toString())
        }
    }

    suspend fun getToken(name : String) = client.getToken("Bearer ${tokenStorage.getAccessToken().toString()}", name)

    suspend fun postFCM(
        request : FCM
    ){
        try {
            val response = client.postFCM("Bearer ${tokenStorage.getAccessToken().toString()}",request = request)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("fcm 성공!", response.message)
            } else {
                Log.d("fcm 실패!", response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fcm 실패!", e.message.toString())

        }
    }

    suspend fun patchToken(
        request: Token
    ){
        try {
            val response = client.patchToken("Bearer ${tokenStorage.getAccessToken().toString()}",request = request)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("fcm 갱신 성공!", response.message)
            } else {
                Log.d("fcm 갱신 실패!", response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fcm 갱신 실패!", e.message.toString())

        }
    }

}