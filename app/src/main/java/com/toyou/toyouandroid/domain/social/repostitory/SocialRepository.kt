package com.toyou.toyouandroid.domain.social.repostitory

import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.utils.TokenManager
import timber.log.Timber

class SocialRepository(private val tokenManager: TokenManager) {
    private val client = AuthNetworkModule.getClient().create(SocialService::class.java)

    suspend fun getFriendsData() = client.getFriends()

    suspend fun patchApproveFriend(request: RequestFriend): Boolean {
        return try {
            val response = client.patchApprove(request)
            if (response.isSuccess) {
                Timber.tag("SocialRepository").d("친구 승인 성공: ${response.message}")
                true // 성공하면 true 반환
            } else {
                Timber.tag("SocialRepository").d("친구 승인 실패: ${response.message}")
                false // 실패하면 false 반환
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("친구 승인 실패!").d(e.message.toString())
            false // 예외 발생 시 false 반환
        }
    }

    suspend fun deleteFriendData(request: RequestFriend){
        try {
            val response = client.deleteFriend(request)
            // 응답 처리
            if (response.isSuccess) {
                Timber.tag("친구 삭제 성공!").d(response.message)
            } else {
                Timber.tag("친구 삭제 실패!").d(response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("친구 삭제 실패!").d(e.message.toString())

        }
    }
    suspend fun postRequest(request : RequestFriend){
        try {
            val response = client.postFriendRequest(request)
            // 응답 처리
            if (response.isSuccess) {
                Timber.tag("친구 요청 성공!").d(response.message)
            } else {
                Timber.tag("친구 요청 실패!").d(response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("친구 요청 실패!").d(e.message.toString())

        }
    }


    suspend fun getSearchData(name : String) = client.getSearchFriend(name)

    suspend fun postQuestionData(questionDto: QuestionDto) {
        try {
            val response = client.postQuestion(questionDto)
            // 응답 처리
            if (response.isSuccess) {
                Timber.tag("post 성공!").d(response.message)
            } else {
                Timber.tag("post 실패!").d(response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("post 실패!").d(e.message.toString())
        }
    }
}