package com.toyou.core.network.model.social

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("targetId") val targetId: Long,
    @SerializedName("content") var content: String,
    @SerializedName("questionType") val type: String,
    @SerializedName("anonymous") var anonymous: Boolean = false,
    @SerializedName("answerOptionList") var options: List<String>?,
)

data class RequestFriend(
    @SerializedName("userId") val userId: Long
)

data class FriendsDto(
    @SerializedName("friendList") val friends: List<Friend>
)

data class Friend(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("emotion") val emotion: String?,
    @SerializedName("ment") val ment: String?,
)

data class ResponseFriend(
    @SerializedName("myName") val name: String
)

data class SearchFriendDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val name: String,
    @SerializedName("friendStatus") val status: String
)
