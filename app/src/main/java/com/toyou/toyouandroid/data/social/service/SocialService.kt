package com.toyou.toyouandroid.data.social.service

import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.data.social.dto.response.ResponseFriend
import com.toyou.toyouandroid.data.social.dto.response.SearchFriendDto
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SocialService {

    @POST("/questions")
    suspend fun postQuestion(
        @Body request : QuestionDto
    ) : BaseResponse<ResponseFriend>

    @GET("/friends")
    suspend fun getFriends() : BaseResponse<FriendsDto>

    @GET("/friends/search")
    suspend fun getSearchFriend(
        @Query("keyword") name: String
    ): BaseResponse<SearchFriendDto>

    @POST("/friends/requests")
    suspend fun postFriendRequest(
        @Body friend : RequestFriend
    ) : BaseResponse<ResponseFriend>

    @HTTP(method = "DELETE", path = "/friends", hasBody = true)
    suspend fun deleteFriend(
        @Body friend : RequestFriend
    ) : BaseResponse<Unit>

    @PATCH("/friends/requests/approve")
    suspend fun patchApprove(
        @Body friend : RequestFriend
    ) : BaseResponse<ResponseFriend>
}

