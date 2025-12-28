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
class SocialRepositoryImpl @Inject constructor(
    private val socialService: SocialService
) : ISocialRepository {

    override suspend fun getFriendsData(): BaseResponse<FriendsDto> {
        return socialService.getFriends()
    }

    override suspend fun getSearchData(name: String): BaseResponse<SearchFriendDto> {
        return socialService.getSearchFriend(name)
    }

    override suspend fun patchApproveFriend(request: RequestFriend): BaseResponse<ResponseFriend> {
        return socialService.patchApprove(request)
    }

    override suspend fun deleteFriendData(request: RequestFriend): BaseResponse<Unit> {
        return socialService.deleteFriend(request)
    }

    override suspend fun postRequest(request: RequestFriend): BaseResponse<ResponseFriend> {
        return socialService.postFriendRequest(request)
    }

    override suspend fun postQuestionData(questionDto: QuestionDto): BaseResponse<ResponseFriend> {
        return socialService.postQuestion(questionDto)
    }
}