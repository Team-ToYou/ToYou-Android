package com.toyou.toyouandroid.data.social.service

import com.toyou.toyouandroid.data.social.dto.request.QuestionDto
import com.toyou.toyouandroid.data.social.dto.response.FriendsDto
import com.toyou.toyouandroid.data.social.dto.response.SearchFriendDto
import com.toyou.toyouandroid.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SocialService {

    @POST("/questions")
    suspend fun postQuestion(
        @Header("userId") id : Long,
        @Body request : QuestionDto
    ) : BaseResponse<Unit>

    @GET("/friends")
    suspend fun getFriends(
        @Header("userId") id : Long
    ) : BaseResponse<FriendsDto>

    @GET("/friends/search")
    suspend fun getSearchFriend(
        @Header("userId") id: Long,
        @Query("keyword") name: String
    ): BaseResponse<SearchFriendDto>
}