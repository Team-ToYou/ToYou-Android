package com.toyou.toyouandroid.fcm.dto.response

import com.google.gson.annotations.SerializedName

data class GetToken(
    @SerializedName("token") val tokens : List<String>
)

