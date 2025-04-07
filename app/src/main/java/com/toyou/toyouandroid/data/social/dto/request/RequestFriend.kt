package com.toyou.toyouandroid.data.social.dto.request

import com.google.gson.annotations.SerializedName

data class RequestFriend(
    @SerializedName("userId") val userId : Long
    )