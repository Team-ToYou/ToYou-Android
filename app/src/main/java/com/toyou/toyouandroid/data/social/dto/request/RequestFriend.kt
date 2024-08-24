package com.toyou.toyouandroid.data.social.dto.request

import com.google.gson.annotations.SerializedName

data class RequestFriend(
    @SerializedName("nickname") val name : String,
    )