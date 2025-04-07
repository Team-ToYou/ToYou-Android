package com.toyou.toyouandroid.data.social.dto.response

import com.google.gson.annotations.SerializedName

data class SearchFriendDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val name : String,
    @SerializedName("friendStatus") val status : String
)
