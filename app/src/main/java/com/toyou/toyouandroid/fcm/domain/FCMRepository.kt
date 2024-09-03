package com.toyou.toyouandroid.fcm.domain

import android.util.Log
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.fcm.service.FCMService
import com.toyou.toyouandroid.network.RetrofitInstance

class FCMRepository {

    private val client = RetrofitInstance.getInstance().create(FCMService::class.java)

    suspend fun postQuestionData(
        token: Token,
    ) {
        try {
            val response = client.postToken(1, token)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("post성공!", response.message)
            } else {
                Log.d("post실패!", response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("post실패!", e.message.toString())

        }
    }

}