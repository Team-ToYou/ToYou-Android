package com.toyou.toyouandroid.domain.social.repostitory

import android.util.Log
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.network.RetrofitInstance

class SocialRepository {
    private val client = RetrofitInstance.getInstance().create(SocialService::class.java)

    suspend fun getFriendsData() = client.getFriends(1)

    suspend fun postQuestionData(
        questionDto: QuestionDto,
    ) {
        try {
            val response = client.postQuestion(1, request = questionDto)
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