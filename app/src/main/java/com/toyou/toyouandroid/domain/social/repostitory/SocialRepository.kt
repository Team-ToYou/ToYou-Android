package com.toyou.toyouandroid.domain.social.repostitory

import android.util.Log
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.network.RetrofitInstance
import com.toyou.toyouandroid.utils.TokenStorage

class SocialRepository(private val tokenStorage: TokenStorage) {
    private val client = RetrofitInstance.getInstance().create(SocialService::class.java)

    suspend fun getFriendsData() = client.getFriends("Bearer ${tokenStorage.getAccessToken().toString()}")

    suspend fun patchApproveFriend(
        request: RequestFriend
    ){
        try {
            val response = client.patchApprove("Bearer ${tokenStorage.getAccessToken().toString()}", friend = request)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("친구승인 성공!", response.message)
            } else {
                Log.d("친구승인 실패!", response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("친구승인 실패!", e.message.toString())

        }
    }

    suspend fun deleteFriendData(
        request: RequestFriend
    ){
        try {
            val response = client.deleteFriend("Bearer ${tokenStorage.getAccessToken().toString()}", friend = request)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("친구삭제 성공!", response.message)
            } else {
                Log.d("친구삭제 실패!", response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("친구삭제 실패!", e.message.toString())

        }
    }
    suspend fun postRequest(
        request : RequestFriend
    ){
        try {
            val response = client.postFriendRequest("Bearer ${tokenStorage.getAccessToken().toString()}", friend = request)
            // 응답 처리
            if (response.isSuccess) {
                Log.d("친구요청 성공!", response.message)
            } else {
                Log.d("친구요청 실패!", response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("친구요청 실패!", e.message.toString())

        }
    }


    suspend fun getSearchData(
        name : String,
    ) = client.getSearchFriend("Bearer ${tokenStorage.getAccessToken().toString()}", name)

    suspend fun postQuestionData(
        questionDto: QuestionDto,
    ) {
        try {
            val response = client.postQuestion("Bearer ${tokenStorage.getAccessToken().toString()}", request = questionDto)
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