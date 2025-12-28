package com.toyou.toyouandroid.domain.social.repostitory

import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.data.social.dto.response.ResponseFriend
import com.toyou.toyouandroid.data.social.dto.response.SearchFriendDto
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.network.BaseResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepository @Inject constructor(private val socialService: SocialService) {

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

    suspend fun postQuestionData(questionDto: QuestionDto): BaseResponse<ResponseFriend> {
        return socialService.postQuestion(questionDto)
    }
}