package com.toyou.core.network.model.fcm

import com.google.gson.annotations.SerializedName

data class FCM(
    @SerializedName("token") val token: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
)

data class Token(
    @SerializedName("token") val token: String
)

data class GetToken(
    @SerializedName("token") val tokens: List<String>
)
