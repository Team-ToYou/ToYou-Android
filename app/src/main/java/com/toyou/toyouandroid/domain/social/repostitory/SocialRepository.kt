package com.toyou.toyouandroid.domain.social.repostitory

import com.toyou.toyouandroid.data.home.dto.response.CardDetail
import com.toyou.toyouandroid.data.record.dto.DiaryCardResponse
import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.data.social.dto.response.ResponseFriend
import com.toyou.toyouandroid.data.social.dto.response.SearchFriendDto
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.BaseResponse
import com.toyou.toyouandroid.utils.TokenManager
import retrofit2.Call
import timber.log.Timber

class SocialRepository(private val socialService: SocialService) {
    private val client = AuthNetworkModule.getClient().create(SocialService::class.java)

    suspend fun getFriendsData(): BaseResponse<FriendsDto> {
        return socialService.getFriends()
    }

    suspend fun getSearchData(name : String): BaseResponse<SearchFriendDto> {
        return socialService.getSearchFriend(name)
    }

    suspend fun patchApproveFriend(request: RequestFriend): BaseResponse<ResponseFriend> {
        return socialService.patchApprove(request)
    }

    suspend fun deleteFriendData(request: RequestFriend): BaseResponse<Unit> {
        return socialService.deleteFriend(request)
    }

    suspend fun postRequest(request: RequestFriend): BaseResponse<ResponseFriend> {
        return socialService.postFriendRequest(request)
    }

//    suspend fun postRequest(request : RequestFriend){
//        try {
//            val response = client.postFriendRequest(request)
//            // 응답 처리
//            if (response.isSuccess) {
//                Timber.tag("친구 요청 성공!").d(response.message)
//            } else {
//                Timber.tag("친구 요청 실패!").d(response.message)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Timber.tag("친구 요청 실패!").d(e.message.toString())
//
//        }
//    }

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