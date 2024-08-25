package com.toyou.toyouandroid.data.social.service

import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.request.RequestFriend
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.data.social.dto.response.SearchFriendDto
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SocialService {

    @POST("/questions")
    suspend fun postQuestion(
        @Header("userId") id : Int,
        @Body request : QuestionDto
    ) : BaseResponse<Unit>

    @GET("/friends")
    suspend fun getFriends(
        @Header("userId") id : Int
    ) : BaseResponse<FriendsDto>

    @GET("/friends/search")
    suspend fun getSearchFriend(
        @Header("userId") id: Int,
        @Query("keyword") name: String
    ): BaseResponse<SearchFriendDto>

    @POST("/friends/requests")
    suspend fun postFriendRequest(
        @Header("userId") id: Int,
        @Body friend : RequestFriend
    ) : BaseResponse<Unit>

    @HTTP(method = "DELETE", path = "/friends", hasBody = true)
    suspend fun deleteFriend(
        @Header("userId") id: Int,
        @Body friend : RequestFriend
    ) : BaseResponse<Unit>

    @PATCH("/friends/requests/approve")
    suspend fun patchApprove(
        @Header("userId") id: Int,
        @Body friend : RequestFriend
    ) : BaseResponse<Unit>
}

