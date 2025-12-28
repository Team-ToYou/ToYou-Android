package com.toyou.toyouandroid.domain.social.repostitory

import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.data.social.dto.response.ResponseFriend
import com.toyou.toyouandroid.data.social.dto.response.SearchFriendDto
import com.toyou.toyouandroid.network.BaseResponse

interface ISocialRepository {
    suspend fun getFriendsData(): BaseResponse<FriendsDto>
    suspend fun getSearchData(name: String): BaseResponse<SearchFriendDto>
    suspend fun patchApproveFriend(request: RequestFriend): BaseResponse<ResponseFriend>
    suspend fun deleteFriendData(request: RequestFriend): BaseResponse<Unit>
    suspend fun postRequest(request: RequestFriend): BaseResponse<ResponseFriend>
    suspend fun postQuestionData(questionDto: QuestionDto): BaseResponse<ResponseFriend>
}
