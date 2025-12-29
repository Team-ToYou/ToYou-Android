package com.toyou.core.network.api

import com.toyou.core.network.model.BaseResponse
import com.toyou.core.network.model.social.FriendsDto
import com.toyou.core.network.model.social.QuestionDto
import com.toyou.core.network.model.social.RequestFriend
import com.toyou.core.network.model.social.ResponseFriend
import com.toyou.core.network.model.social.SearchFriendDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SocialService {

    @POST("/questions")
    suspend fun postQuestion(
        @Body request: QuestionDto
    ): BaseResponse<ResponseFriend>

    @GET("/friends")
    suspend fun getFriends(): BaseResponse<FriendsDto>

    @GET("/friends/search")
    suspend fun getSearchFriend(
        @Query("keyword") name: String
    ): BaseResponse<SearchFriendDto>

    @POST("/friends/requests")
    suspend fun postFriendRequest(
        @Body friend: RequestFriend
    ): BaseResponse<ResponseFriend>

    @HTTP(method = "DELETE", path = "/friends", hasBody = true)
    suspend fun deleteFriend(
        @Body friend: RequestFriend
    ): BaseResponse<Unit>

    @PATCH("/friends/requests/approve")
    suspend fun patchApprove(
        @Body friend: RequestFriend
    ): BaseResponse<ResponseFriend>
}
