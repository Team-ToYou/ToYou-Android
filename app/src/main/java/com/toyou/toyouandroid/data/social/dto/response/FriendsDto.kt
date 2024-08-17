package com.toyou.toyouandroid.data.social.dto.response

import com.google.gson.annotations.SerializedName

data class FriendsDto(
    @SerializedName("friendList") val friends : List<Friend>
)

data class Friend(
    @SerializedName("nickname") val nickname : String,
    @SerializedName("emotion") val emotion : String?,
    @SerializedName("ment") val ment : String?,
)
